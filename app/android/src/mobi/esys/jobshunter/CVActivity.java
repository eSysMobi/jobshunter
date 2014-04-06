package mobi.esys.jobshunter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.custom_edit_format_validators.AgeValidator;
import mobi.esys.custom_widgets.SpezAutoCompleteView;
import mobi.esys.specific_data_type.SuperCategory;
import mobi.esys.tasks.GetSupCatsTask;
import mobi.esys.tasks.RegGCMTask;
import mobi.esys.tasks.SubscribeTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.andreabaccega.widget.FormEditText;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tokenautocomplete.TokenCompleteTextView.TokenClickStyle;

public class CVActivity extends Activity {
	private transient SharedPreferences preferences;
	private static FormEditText dateEdit;
	private transient FormEditText mailEdit;
	private transient int visibleCardCount = 1;
	private transient LinearLayout.LayoutParams pcp;
	private transient LinearLayout.LayoutParams inpcp;
	private transient GoogleCloudMessaging cloudMessaging;
	private transient LinearLayout cardContainer;
	private transient ImageButton addBtn;
	private transient String regID = "";
	private transient StringBuilder subscribeParams;
	private transient LinearLayout timeCardLayout;
	private transient JSONArray workTypes;
	private transient EditText cityCardEdit;
	private transient List<SuperCategory> categories;
	private transient SpezAutoCompleteView subSpecEdit;


	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_cv);

		preferences = getSharedPreferences("JHPref", MODE_PRIVATE);

		cityCardEdit = (EditText) findViewById(R.id.cityCardEdit);
		ImageButton cityButton = (ImageButton) findViewById(R.id.cityCardBtn);

		GetSupCatsTask supCatsTask = new GetSupCatsTask(CVActivity.this);
		supCatsTask.execute(getIntent().getExtras().getBundle("splashBundle")
				.getString("cats"));
		subSpecEdit = (SpezAutoCompleteView) findViewById(R.id.subCardSpecEdit);
		subSpecEdit.setTokenClickStyle(TokenClickStyle.Delete);
		subSpecEdit.allowDuplicates(false);

		try {
			categories = supCatsTask.get();

		} catch (InterruptedException e1) {
		} catch (ExecutionException e1) {
		}

		String[] cats = new String[categories.size()];
		for (int i = 0; i < cats.length; i++) {
			cats[i] = categories.get(i).getName();
		}

		AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.supCardSpecEdit);
		autoCompleteTextView.setAdapter(new ArrayAdapter<String>(
				CVActivity.this, android.R.layout.simple_list_item_1, cats));

		cityButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cityCardEdit.setText(preferences.getString("cityName", ""));
			}
		});

		subscribeParams = new StringBuilder();

		EditText telEditText = (EditText) findViewById(R.id.telEdit);
		telEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				if (s.length() == 3 || s.length() == 7 || s.length() == 10) {
					s = s.append("-");
				}
			}
		});

		EditText salaryFromEdit = (EditText) findViewById(R.id.salaryFromEdit);
		salaryFromEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		});

		try {
			workTypes = new JSONArray(preferences.getString("workTypes", ""));
		} catch (JSONException e) {
		}
		Log.d("work types", workTypes.toString());
		timeCardLayout = (LinearLayout) findViewById(R.id.timeBoxLayout);
		for (int i = 0; i < workTypes.length(); i++) {
			try {
				JSONObject currObj = workTypes.getJSONObject(i);
				CheckBox box = new CheckBox(CVActivity.this);
				box.setText(currObj.getString("name"));
				timeCardLayout.addView(box);
			} catch (JSONException e) {
			}
		}

		Button signBtn = (Button) findViewById(R.id.signBtn);
		cloudMessaging = GoogleCloudMessaging.getInstance(CVActivity.this);
		signBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<Integer> workTypesMask = new ArrayList<Integer>();
				for (int i = 0; i < timeCardLayout.getChildCount(); i++) {
					if (timeCardLayout.getChildAt(i) instanceof CheckBox) {
						CheckBox timeTypeCheckBox = (CheckBox) timeCardLayout
								.getChildAt(i);
						if (timeTypeCheckBox.isChecked()) {
							workTypesMask.add(i);
						}
					}
				}

				StringBuilder workTypesString = new StringBuilder();
				for (int i = 0; i < workTypesMask.size(); i++) {
					try {
						workTypesString.append("&work[]=").append(
								workTypes.getJSONObject(workTypesMask.get(i))
										.getString("id"));
					} catch (JSONException e) {
					}
				}

				EditText salaryEdit = (EditText) findViewById(R.id.salaryFromEdit);
				CheckBox includeBox = (CheckBox) findViewById(R.id.includeCheckBox);

				if (!(salaryEdit.getText().toString().isEmpty() && !includeBox
						.isChecked())) {
					if (includeBox.isChecked()) {
						if (salaryEdit.getText().toString().isEmpty()) {
							subscribeParams.append("&salarywnull[]=999999");
						} else {
							subscribeParams.append("&salarywnull[]="
									+ salaryEdit.getText().toString());
						}
					} else {
						subscribeParams.append("salarywonull[]="
								+ salaryEdit.getText().toString());
					}
				}

				subscribeParams.append(workTypesString.toString());

				SubscribeTask subscribeTask = new SubscribeTask(CVActivity.this);
				subscribeTask.execute(subscribeParams.toString());
				Log.d("sub_params", subscribeParams.toString());
			}
		});

		mailEdit = (FormEditText) findViewById(R.id.mailEdit);
		mailEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!mailEdit.hasFocus()) {
					mailEdit.testValidity();
				}
			}
		});

		dateEdit = (FormEditText) findViewById(R.id.dateEdit);
		dateEdit.addValidator(new AgeValidator());

		cardContainer = (LinearLayout) findViewById(R.id.cards_container);
		inpcp = new LinearLayout.LayoutParams(0, 0);
		addBtn = (ImageButton) findViewById(R.id.addCardBtn);
		for (int i = 1; i < cardContainer.getChildCount() - 1; i++) {
			LinearLayout currLayout = (LinearLayout) cardContainer
					.getChildAt(i);
			currLayout.getChildAt(0).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (visibleCardCount > 1) {
						if (!addBtn.isEnabled()) {
							addBtn.setEnabled(true);
							addBtn.setVisibility(View.VISIBLE);
						}
						((LinearLayout) v.getParent()).setVisibility(View.GONE);
						((LinearLayout) v.getParent()).setLayoutParams(inpcp);
						visibleCardCount--;
					}
				}
			});
		}

		LinearLayout personalCard = (LinearLayout) findViewById(R.id.personalDataCard);

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		pcp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		pcp.setMargins(height / 32, height / 32, height / 32, height / 32);
		personalCard.setLayoutParams(pcp);

		dateEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new SelectDateFragment();
				newFragment.show(getFragmentManager(), "DatePicker");
			}
		});

		addBtn = (ImageButton) findViewById(R.id.addCardBtn);
		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LinearLayout lastVisibleCard = (LinearLayout) cardContainer
						.getChildAt(visibleCardCount);
				lastVisibleCard.setLayoutParams(pcp);
				lastVisibleCard.setVisibility(View.VISIBLE);

				visibleCardCount++;
				if (visibleCardCount == 5) {
					addBtn.setEnabled(false);
					addBtn.setVisibility(View.GONE);
				}

			}

		});
	}

	private String getRegistatrationID(Context context) {
		final SharedPreferences gcmPreferences = getGCMPreferences();
		String registID = gcmPreferences.getString("registration_id", "");
		Log.d("reg_id", registID);
		if (registID.isEmpty()) {
			return "";
		}

		return registID;
	}

	private SharedPreferences getGCMPreferences() {
		return getSharedPreferences("JHPref", MODE_PRIVATE);
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				Log.d("reg", "reg task");
				String msg = "";
				try {
					if (cloudMessaging == null) {
						cloudMessaging = GoogleCloudMessaging
								.getInstance(CVActivity.this);
					}

					Log.d("reg",
							regID = cloudMessaging.register("829773271300"));
					cloudMessaging.close();

					RegGCMTask gcmTask = new RegGCMTask(CVActivity.this);
					gcmTask.execute(regID);

					final SharedPreferences prefs = getGCMPreferences();
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("registration_id", regID);

					editor.commit();
				} catch (IOException exception) {
					msg = "error " + exception.getMessage();
				}
				Log.d("msg", msg);
				return msg;
			}

			protected void onPostExecute(String result) {
				cancel(true);
			};
		}.execute();

	}

	public static class SelectDateFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Calendar calendar = Calendar.getInstance();
			return new DatePickerDialog(getActivity(), SelectDateFragment.this,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			String dValue = "";
			String mValue = "";
			if (String.valueOf(dd).length() == 2) {
				dValue = String.valueOf(dd);
			} else {
				dValue = "0" + String.valueOf(dd);
			}

			if (String.valueOf(mm + 1).length() == 2) {
				mValue = String.valueOf(mm + 1);
			} else {
				mValue = "0" + String.valueOf(mm + 1);
			}
			dateEdit.setText(dValue + "." + mValue + "." + String.valueOf(yy));
			dateEdit.testValidity();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startActivity(new Intent(CVActivity.this, MainActivity.class).putExtra(
				"splashBundle",
				getIntent().getExtras().getBundle("splashBundle")));
	}
}
