package com.example.yb.hstt.IM;

import android.text.TextUtils;
import android.util.Log;

import com.example.yb.hstt.Base.BaseApplication;
import com.example.yb.hstt.Base.GlobalManager;
import com.example.yb.hstt.Utils.SpUtil;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.bean.Friend;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装 Smack 常用方法
 */
public class SmackUtils {

    private static SmackUtils smackUtils;

    public static SmackUtils getInstance() {
        if (smackUtils == null) {
            smackUtils = new SmackUtils();
        }
        return smackUtils;
    }

    /**
     * 建立连接
     */
    public void getXMPPConnection() {
        if (BaseApplication.connection == null || !BaseApplication.connection.isConnected()) {
            XMPPTCPConnectionConfiguration builder = XMPPTCPConnectionConfiguration.builder()
                    .setHost(GlobalManager.IM_HOST)//ip
                    .setPort(GlobalManager.IM_PORT)//端口
                    .setServiceName(GlobalManager.IM_SERVER)//此处填写openfire服务器名称
                    .setCompressionEnabled(false)//是否允许使用压缩
                    .setSendPresence(true)//是否发送Presece信息
                    .setDebuggerEnabled(true)//是否开启调试
                    .setResource("Android")//设置登陆设备标识
                    .setConnectTimeout(15 * 1000)//连接超时时间
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)//设置TLS安全模式时使用的连接
                    .build();
            // 是否使用SASL
            SASLAuthentication.blacklistSASLMechanism(SASLMechanism.DIGESTMD5);
            BaseApplication.connection = new XMPPTCPConnection(builder);
            try {
                BaseApplication.connection.connect();
                ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(BaseApplication.connection);
                reconnectionManager.setFixedDelay(1000);//重联间隔
                reconnectionManager.enableAutomaticReconnection();//开启重联机制
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查连接
     */
    private void checkConnect() {
        if (BaseApplication.connection == null) {//null
            getXMPPConnection();
        }
        if (!BaseApplication.connection.isConnected()) {//没有连接到服务器
            try {
                BaseApplication.connection.connect();
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 断开连接
     */
    public void exitConnect() {
        if (BaseApplication.connection != null && BaseApplication.connection.isConnected()) {
            BaseApplication.connection.disconnect();
            BaseApplication.connection = null;
        }
    }


    /**
     * 检查登录
     */
    private boolean checkLogin() {
        if (!BaseApplication.connection.isAuthenticated()) {//没有连接到服务器
            try {
                String username = (String) SpUtil.get(BaseApplication.instances.getApplicationContext(), "phoneNo", "");
                String phonePsD = (String) SpUtil.get(BaseApplication.instances.getApplicationContext(), "phonePsD", "");
                if (TextUtils.isEmpty(username)) {
                    ToastUtil.showToast(BaseApplication.instances.getApplicationContext(), "请检查用户名和密码是否正确/网络是否可用");
                    return false;
                }
                BaseApplication.connection.login(username,phonePsD);
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     */
    public boolean register(String username, String password) {
        try {
            checkConnect();
            Map<String, String> map = new HashMap<String, String>();
            map.put("phone", "Android");
            AccountManager accountManager = AccountManager.getInstance(BaseApplication.connection);
            //敏感操作跳过不安全的连接
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(username, password, map);
        } catch (SmackException | XMPPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */

    public boolean login(String username, String password) {
        try {
            checkConnect();
            if (BaseApplication.connection.isAuthenticated()) {//已经登录
                return true;
            } else {
                BaseApplication.connection.login(username, password);//登录
                return BaseApplication.connection.isAuthenticated();
            }
        } catch (IOException | SmackException | XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public List<Friend> getFriendsList() {
        checkConnect();
        checkLogin();
        List<Friend> list = new ArrayList<Friend>();
        //Roster对象翻译成中文为"花名册",表示用户的所有好友清单以及申请加好友的用户清单
        Roster roster = Roster.getInstanceFor(BaseApplication.connection);
        Collection<RosterEntry> rosterEntries = roster.getEntries();
        for (RosterEntry rosterentry : rosterEntries) {
            Friend friend = new Friend();
            friend.setName(rosterentry.getName());
            list.add(friend);
        }
        Log.i("IMDemo",list.toString());
        return list;
    }

    /**
     * 发送消息
     *
     * @param message
     * @param to
     */
    public void sendMessage(String message, String to) {
        try {
            checkConnect();
            if (checkLogin()) {
                ChatManager mChatManager = ChatManager.getInstanceFor(BaseApplication.connection);
                Chat mChat = mChatManager.createChat(to + "@" + GlobalManager.IM_SERVER);
                mChat.sendMessage(message);
                mChat.close();
            }
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加好友
     *
     * @param userName
     */
    public void addFriend(String userName) {
        try {
            checkConnect();
            checkLogin();
            Roster roster = Roster.getInstanceFor(BaseApplication.connection);
            roster.createEntry(userName, userName, null);
        } catch (SmackException | XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除好友
     *
     * @param userJID
     * @return
     */
    public void deleteFriend(String userJID) {
        try {
            checkConnect();
            checkLogin();
            Roster roster = Roster.getInstanceFor(BaseApplication.connection);
            roster.removeEntry(roster.getEntry(userJID));
        } catch (SmackException | XMPPException e) {
            e.printStackTrace();
        }
    }
}
