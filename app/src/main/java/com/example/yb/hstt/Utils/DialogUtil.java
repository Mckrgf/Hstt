package com.example.yb.hstt.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class DialogUtil {

	public static void showNormalDialog(String comment,Context context){
		final AlertDialog.Builder normalDialog =
				new AlertDialog.Builder(context);
		normalDialog.setTitle("故障信息描述");
		normalDialog.setMessage(comment);
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//...To-do
					}
				});
		normalDialog.show();
	}
}
