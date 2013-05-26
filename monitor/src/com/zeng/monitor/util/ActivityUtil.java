package com.zeng.monitor.util;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.Toast;

public class ActivityUtil{

	  public static void showAlert(Context context,CharSequence title,CharSequence message,CharSequence btnTitle){
	    	new AlertDialog.Builder(context).setTitle(title)
	    	.setMessage(message).setPositiveButton(btnTitle, new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					
				}
	    		
	    	}).show();
	    }
	    public static void openToast(Context context,String str){
	    	//Looper.prepare();
	    	Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	    	Looper.loop();
	    }
}