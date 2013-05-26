package com.zeng.monitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.zeng.monitor.util.ActivityUtil;
import com.zeng.monitor.util.DatabaseHelper;

public class MonitorClient extends Activity {

	public final static String TAG = "MonitorClient";

	protected TextView view;
	protected DatabaseHelper helper;
	protected Spinner spinner;

	protected Button btnAdd;
	protected Button btnModify;
	protected Button btnDelete;
	protected Button btnConnect;
	private SimpleCursorAdapter adapter;
	private Cursor cursor;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			findView();
			fillDataWithCursor();
			setListenner();
		} catch (Exception e) {
			ActivityUtil.showAlert(MonitorClient.this, "Error", e.getMessage(), "确定");
		}
	}
	
	private void fillDataWithCursor() throws Exception {
		DatabaseHelper helper = new DatabaseHelper(this);
		cursor = helper.loadAllName();
		int count = cursor.getCount();
		cursor.moveToFirst();
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, cursor,
				new String[] { "name" }, new int[] { android.R.id.text1 });
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.refreshDrawableState();
		helper.close();
		if (count <= 0) {
			btnConnect.setEnabled(false);
			btnDelete.setEnabled(false);
			btnModify.setEnabled(false);
			spinner.setEnabled(false);
		} else {
			btnConnect.setEnabled(true);
			btnDelete.setEnabled(true);
			btnModify.setEnabled(true);
			spinner.setEnabled(true);
		}
	}
	
	private void findView() {
		spinner = (Spinner) findViewById(R.id.SpinnerLdapConfig);
		btnAdd = (Button) findViewById(R.id.BtnNew);
		btnConnect = (Button) findViewById(R.id.BtnConnect);
		btnModify = (Button) findViewById(R.id.BtnModify);
		btnDelete = (Button) findViewById(R.id.BtnDelete);
	}
	
	private void setListenner() {
		btnAdd.setOnClickListener(btnAddListener);
		btnModify.setOnClickListener(btnModifyListener);
		btnDelete.setOnClickListener(btnDeleteListener);
		btnConnect.setOnClickListener(btnConnectListener);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				try {
					fillDataWithCursor();
				} catch (Exception e) {
					ActivityUtil.showAlert(MonitorClient.this, "Error", e.getMessage(),
							"确定");
				}
			}
		}
	}
	
	private OnClickListener btnConnectListener = new OnClickListener() {

		public void onClick(View v) {
			try {
				Cursor cc = (Cursor) spinner.getSelectedItem();
				int id = cc.getInt(0);
				//Intent intent = new Intent(MonitorClient.this, ServerAct.class);
				Intent intent = new Intent();
				intent.setClass(MonitorClient.this, ServerAct.class);
				intent.putExtra("id", id);
				startActivity(intent);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				ActivityUtil.showAlert(MonitorClient.this, "Error", e.getMessage(), "确定");
			}
		}

	};

	private OnClickListener btnAddListener = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent(MonitorClient.this, MonitorConfigActivity.class);
			startActivityForResult(intent, 0);
		}

	};

	private OnClickListener btnModifyListener = new OnClickListener() {

		public void onClick(View v) {
			try {
				Cursor cc = (Cursor) spinner.getSelectedItem();
				int id = cc.getInt(0);
				Intent intent = new Intent(MonitorClient.this, MonitorConfigActivity.class);
				intent.putExtra("id", id);
				startActivityForResult(intent, 1);
				// DatabaseHelper.testInsert(MonitorClient.this);
				// fillData();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				ActivityUtil.showAlert(MonitorClient.this, "Error", e.getMessage(), "确定");
			}
		}

	};

	private OnClickListener btnDeleteListener = new OnClickListener() {

		public void onClick(View v) {
			try {
				Cursor cc = (Cursor) spinner.getSelectedItem();
				final int id = cc.getInt(0);
				String name = cc.getString(1);
				new AlertDialog.Builder(MonitorClient.this).setTitle("确定删除吗？")
						.setMessage("确定删除" + name + "吗？").setPositiveButton(
								"确定", new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										try {
											DatabaseHelper
													.delete(MonitorClient.this, id);
											fillDataWithCursor();
											ActivityUtil.openToast(MonitorClient.this,
													"删除成功!");
										} catch (Exception e) {
											Log.e(TAG, e.getMessage(), e);
											ActivityUtil.openToast(MonitorClient.this, e
													.getMessage());
										}
									}
								}).setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}

								}).show();
				// DatabaseHelper.Delete(CamMonitorClient.this, name);
			} catch (Exception e) {
				ActivityUtil.showAlert(MonitorClient.this, "Error", e.getMessage(), "确定");
			}
		}

	};
}
