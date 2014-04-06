package mobi.esys.jobshunter;

import java.util.ArrayList;
import java.util.List;

import mobi.esys.specific_data_type.Subscribe;
import mobi.esys.specific_data_type.SupSubs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SubsActivity extends Activity {
	private transient List<String> alreadyTypes;
	private transient List<SupSubs> supSubs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subs);

		alreadyTypes = new ArrayList<String>();

		SharedPreferences preferences = getSharedPreferences("JHPref",
				MODE_PRIVATE);

		int height = preferences.getInt("screenHeight", 0);
		LinearLayout layout = (LinearLayout) findViewById(R.id.subsCardsContainer);
		try {
			JSONArray subs = new JSONArray(preferences.getString("subs", ""));
			JSONArray subsTypes = new JSONArray(preferences.getString(
					"subsTypes", ""));
			for (int i = 0; i < subs.length(); i++) {
				for (int j = 0; j < subsTypes.length(); j++) {
					JSONObject subsCurr = subs.getJSONObject(i);
					JSONObject subsTypesCurr = subsTypes.getJSONObject(j);
					if (subsCurr.getString("sub_type").equals(
							subsTypesCurr.getString("id"))) {
						if (!alreadyTypes.contains(subsTypesCurr
								.getString("id"))) {
							List<Subscribe> subscribes = new ArrayList<Subscribe>();

							supSubs.add(new SupSubs(subsTypesCurr
									.getString("id"), subscribes));
							alreadyTypes.add(subsTypesCurr.getString("id"));
							LinearLayout.LayoutParams cp = new LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);

							cp.setMargins(height / 32, height / 32,
									height / 32, height / 32);
							LinearLayout card = new LinearLayout(
									SubsActivity.this);
							card.setBackgroundResource(R.drawable.info_frame);
							card.setLayoutParams(cp);
							TextView textView = new TextView(SubsActivity.this);
							textView.setText(subsTypesCurr.getString("name"));
							card.addView(textView);
							layout.addView(card);
						}

					}
				}
			}
			Log.d("subs", supSubs.toString());
		} catch (JSONException e) {
		}

	}
}
