package com.example.yb.hstt.Utils;

import android.os.Handler;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class AutoRefresh {

	
	private static int sleepTime = 3000;
	private  static boolean[] stop = new boolean[10];
	
	public static void start(final Handler handler, final int i){
		stop[i] = false;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!stop[i]) {
					try {
						Thread.sleep(sleepTime);
						handler.sendEmptyMessage(i);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}).start();
	}
	
	public static void stop(int i) {
		stop[i] = true;
	}
	
	public static void setSleepTime(int time) {
		sleepTime = time;
	}
	
	public static void notifyDataSetChanged(ListView listView, BaseAdapter adapter) {
		for (int i = listView.getFirstVisiblePosition(); i <= listView.getLastVisiblePosition(); i++) {
			adapter.getView(i, listView.getChildAt(i), listView);
		}
	}
}
