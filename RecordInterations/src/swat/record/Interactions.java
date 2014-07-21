package swat.record;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import android.util.Log;
import android.view.Menu;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Interactions extends Activity {
	private final static String LT = "interactionLog";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		ActionBar actionBar = getActionBar();
		actionBar.hide(); 

		setContentView(R.layout.interaction);
		ListView lv = (ListView) findViewById(R.id.listInteractions);
	
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				getLogs());
		lv.setAdapter(modeAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View textview, int arg2,
					long arg3) {
				Intent intent = new Intent(getBaseContext(), RunInteraction.class);
				intent.putExtra("interaction", ((TextView)textview).getText().toString());
				startActivity(intent);
			}

		});

	}
	public String[] getLogs(){
		File fil = new File(Environment.getExternalStorageDirectory()
				+ "/intlog/intrusions");
		File [] logs = fil.listFiles();
		
		String[] interactionLogs = new String[logs.length];
		for(int i =0;i< interactionLogs.length ; i++){
			interactionLogs[i] =logs[i].getName();
		}
		return interactionLogs;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run_macro, menu);

		return true;
	}

}
