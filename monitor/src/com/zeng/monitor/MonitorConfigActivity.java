package com.zeng.monitor;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.zeng.monitor.util.ActivityUtil;
import com.zeng.monitor.util.DatabaseHelper;
import com.zeng.monitor.util.EditDialog;

public class MonitorConfigActivity extends Activity {

	private static final String TAG = "MonitorConfigActivity";

	private Spinner spinner;

	private Button btnCancle;
	private Button btnSave;

	private EditText editIp;
	private EditText editPort;
	private EditText editClientDir;
	private String name;
	private boolean isModify = false;
	private int id;

	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.add);
			findView();
			fillView();
			setListener();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	protected void setListener() {
		btnSave.setOnClickListener(btnSaveListener);
		btnCancle.setOnClickListener(btnCancleListener);
	}

	private void findView() {
		spinner = (Spinner) findViewById(R.id.SpinnerFtpType);
		btnSave = (Button) findViewById(R.id.BtnSave);
		btnCancle = (Button) findViewById(R.id.BtnCancle);
		editClientDir = (EditText) findViewById(R.id.FtpLocalDir);
		editIp = (EditText) findViewById(R.id.FtpIp);
		editPort = (EditText) findViewById(R.id.FtpPort);

	}

	private void fillView() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "Socket",
						"HTTP" });
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		if (this.getIntent().getExtras() != null
				&& this.getIntent().getExtras().containsKey("id")) {
			try {
				isModify = true;
				int id = this.getIntent().getExtras().getInt("id");
				this.id = id;
				DatabaseHelper helper = new DatabaseHelper(this);
				Cursor cursor = helper.query(id);
				cursor.moveToFirst();
				this.name = cursor.getString(1);
				editIp.setText(cursor.getString(2));
				editPort.setText(String.valueOf(cursor.getInt(3)));
				editClientDir.setText(cursor.getString(6));
				String type=String.valueOf(cursor.getInt(7));
				if(type.equals("1")){
				
				} if(type.equals("2")){
				spinner.setSelection(1);
				}
				helper.close();
			} catch (Exception e) {
				Log.e(MonitorConfigActivity.TAG, e.getMessage(), e);
				ActivityUtil.openToast(MonitorConfigActivity.this, "错误，错误原因:"
						+ e.getMessage());
			}
		}
	}

	private EditDialog.OnDataSetListener saveListener = new EditDialog.OnDataSetListener() {
		public void onDataSet(String text) {
			try {
				ContentValues contentValue = new ContentValues();
				contentValue.put("name", text);
				contentValue.put("ip", editIp.getText().toString());
				contentValue.put("port",
						Integer.parseInt(editPort.getText().toString()));
				contentValue.put("client_dir", editClientDir.getText()
						.toString());
			//	Log.i("1113",spinner.getSelectedItemId()+"");
				contentValue.put("connect_type",spinner.getSelectedItemId()+1);
				if (isModify) {
					DatabaseHelper.update(MonitorConfigActivity.this,
							"tb_monitor_configs", contentValue, id);
				} else {
					DatabaseHelper.insert(MonitorConfigActivity.this,
							"tb_monitor_configs", contentValue);
				}
				setResult(Activity.RESULT_OK);
				finish();
				ActivityUtil.openToast(MonitorConfigActivity.this, "保存成功");
			} catch (Exception e) {
				Log.e(MonitorConfigActivity.TAG, e.getMessage(), e);
				ActivityUtil.openToast(MonitorConfigActivity.this, "错误，错误原因:"
						+ e.getMessage());
			}

		}

	};

	private View.OnClickListener btnSaveListener = new View.OnClickListener() {

		public void onClick(View v) {
			if (editIp.getText().toString().trim().length() <= 0) {
				ActivityUtil.showAlert(MonitorConfigActivity.this,
						getText(R.string.error), getText(R.string.error_ip),
						getText(R.string.btn_ok));
				return;
			}
			if (editPort.getText().toString().trim().length() <= 0) {
				ActivityUtil.showAlert(MonitorConfigActivity.this,
						getText(R.string.error), getText(R.string.error_port),
						getText(R.string.btn_ok));
				return;
			}
			String cfgname = editIp.getText().toString();
			if (isModify) {
				cfgname = MonitorConfigActivity.this.name;
			}
			EditDialog dialog = new EditDialog(MonitorConfigActivity.this,
					getText(R.string.MonitorConfigActivty_Cfg_Name).toString(),
					cfgname, saveListener);
			dialog.show();
		}

	};

	private View.OnClickListener btnCancleListener = new View.OnClickListener() {

		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}

	};

}
