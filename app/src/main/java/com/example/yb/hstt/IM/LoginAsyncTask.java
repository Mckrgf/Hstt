package com.example.yb.hstt.IM;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.yb.hstt.Base.BaseApplication;
import com.example.yb.hstt.EventBus.SgeoEvent;
import com.example.yb.hstt.Utils.SpUtil;
import com.example.yb.hstt.Utils.ToastUtil;
import com.example.yb.hstt.bean.Friend;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;

import java.util.ArrayList;

/**
 * 登录-异步任务
 * <p>
 * 执行次序	执行时机	方法名称	调用方
 * 1.异步任务执行前	 onPreExecute       UI线程
 * 2.异步任务执行中	 doInBackground     后台线程
 * 3.异步任务执行中    publishProgress	    后台线程
 * 4.异步任务执行中    onProgressUpdate    UI线程
 * 5.异步任务执行后    onPostExecute	    UI线程
 */

public class LoginAsyncTask extends AsyncTask<String, String, Boolean> {
    private static final String TAG = "LoginAsyncTask";
    private Context mContext;

    public LoginAsyncTask(Context context) {
        mContext = context;
    }

    /**
     * 该方法运行在UI线程中,在执行耗时操作前被调用，主要用于UI控件初始化操作
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //显示登录提示条
    }


    /**
     * 必须重写的方法
     * 该方法不运行在UI线程中,主要用于耗时的处理工作,
     * 可调用publishProgress()方法，触发onProgressUpdate对UI进行操作，更新任务进度。
     */
    @Override
    protected Boolean doInBackground(String... strings) {
        //获取执行异步任务时传入的参数
        String username = strings[0];
        String pwd = strings[1];
        //建立连接
        SmackUtils.getInstance().getXMPPConnection();
        //登录成功
        if (SmackUtils.getInstance().login(username, pwd)) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //获取好友列表,为0就添加admin,列表里没有admin 也添加admin
            ArrayList<Friend> friends = (ArrayList<Friend>) SmackUtils.getInstance().getFriendsList();
            if (friends.size() == 0) {
                SmackUtils.getInstance().addFriend("admin");
                friends = (ArrayList<Friend>) SmackUtils.getInstance().getFriendsList();
                Log.i("IMDemo", "好友列表" + friends.toString());
            } else {
                boolean has_admin = false;
                for (int i = 0; i < friends.size(); i++) {
                    Friend friend = friends.get(i);
                    String name = friend.getName();
                    if (!TextUtils.isEmpty(name)&&name.equals("admin")) {
                        has_admin = true;
                    }
                }
                if (!has_admin) {
                    SmackUtils.getInstance().addFriend("admin");
                    friends = (ArrayList<Friend>) SmackUtils.getInstance().getFriendsList();
                    Log.i("IMDemo", "好友列表" + friends.toString());
                }
            }

            //保存密码到本地
            SpUtil.put(BaseApplication.instances.getApplicationContext(), "phoneNo", username);
//            SpUtil.put(BaseApplication.instances.getApplicationContext(),"phonePsD", pwd);
//            ChatManager manager=ChatManager.getInstanceFor(BaseApplication.connection);
//            manager.addChatListener(new ChatManagerListener() {
//                @Override
//                public void chatCreated(Chat chat, boolean createdLocally) {
//                    chat.addMessageListener(new ChatMessageListener() {
//                        @Override
//                        public void processMessage(Chat chat, Message message) {
//                            String body = message.getBody();
//                            if (!TextUtils.isEmpty(body)){
//                                Log.i(TAG, "processMessage: "+body);
//                            }
//                        }
//                    });
//                }
//            });
            //初始化，监听包
            BaseApplication.connection.addAsyncStanzaListener(
                    new PacketListener(),
                    new StanzaFilter() {
                        @Override
                        public boolean accept(Stanza stanza) {
                            return true;
                        }
                    }
            );


            return true;//传送给onPostExecute布尔值
        }
        return false;//传送给onPostExecute布尔值
    }

    /**
     * doInBackground执行完成后，此方法被UI线程调用，计算结果传递到UI线程。
     */
    @Override
    protected void onPostExecute(Boolean bool) {
        //关闭等待条
        //登陆成功
        if (bool) {
            //登陆成功
            //发消息
            SgeoEvent event = new SgeoEvent();
            event.setEventId(SgeoEvent.IM_LOGIN_OK);
            event.setMsg("ok");
            EventBus.getDefault().post(event);
        } else {
            ToastUtil.showToast(mContext, "请检查用户名和密码是否正确/网络是否可用");
        }
    }
}
