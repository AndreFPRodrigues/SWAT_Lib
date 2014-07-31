package hcim.auric.intrusiondetection;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.record.RunInteraction;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class IntrusionsList extends Activity {

	private Context context;
	private IntrusionsDatabase intrusionsDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intrusions_list);

		context = this.getApplicationContext();
		intrusionsDB = IntrusionsDatabase.getInstance(context);

		Bundle extras = getIntent().getExtras();
		String date = extras.getString("value1");

		TextView t = (TextView) findViewById(R.id.textView1);
		t.append(" " + date);

		List<Intrusion> intrusions = intrusionsDB.getIntrusionsFromADay(date);

		if (intrusions != null)
			addButtons(intrusions);
	}

	private void addButtons(final List<Intrusion> intrusions) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				LinearLayout layout = (LinearLayout) findViewById(R.id.listoflogs);
				Drawable d = getResources().getDrawable(
						R.drawable.button_background);

				for (Intrusion i : intrusions) {
					Button b = new Button(context);
					b.setText(i.toString());
					b.setTextColor(Color.WHITE);
					b.setBackgroundDrawable(d);
					layout.addView(b);
					b.setOnClickListener(new IntrusionClickListener(i.getID()));
				}
			}
		});
	}

	class IntrusionClickListener implements OnClickListener {
		private String intrusion;

		public IntrusionClickListener(String intrusion) {
			this.intrusion = intrusion;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(IntrusionsList.this,
					RunInteraction.class);
			intent.putExtra("interaction", intrusion);
			startActivity(intent);
		}

	}
}
