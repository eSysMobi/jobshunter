package mobi.esys.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.constants.Constants;
import mobi.esys.custom_edit_format_validators.AgeValidator;
import mobi.esys.custom_widgets.SpezAutoCompleteView;
import mobi.esys.jobshunter.R;
import mobi.esys.specific_data_type.Category;
import mobi.esys.specific_data_type.City;
import mobi.esys.specific_data_type.SuperCategory;
import mobi.esys.swipe_dismiss.SwipeDismissListViewTouchListener;
import mobi.esys.tasks.GetCityTask;
import mobi.esys.tasks.GetSupCatsTask;
import mobi.esys.tasks.RegGCMTask;
import mobi.esys.tasks.SubscribeTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tokenautocomplete.TokenCompleteTextView.TokenClickStyle;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;

public class CVFragments extends Fragment implements OnClickListener {
	private transient View view;

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

	private transient List<City> cities;

	private transient ListView cityList;

	private transient String[] citiesIds;

	private transient List<Category> subCategories;
	private transient List<Category> subCatBuilder;

	private transient StringBuilder subCatNamesBuilder;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.cv_fragment, container, false);
		preferences = getActivity().getSharedPreferences(Constants.PREF_STRING,
				Context.MODE_PRIVATE);

		subCatBuilder = new ArrayList<Category>();
		subCatNamesBuilder = new StringBuilder();

		subSpecEdit = (SpezAutoCompleteView) view
				.findViewById(R.id.subCardSpecEdit);
		subSpecEdit.setTokenClickStyle(TokenClickStyle.Delete);
		subSpecEdit.allowDuplicates(false);

		cities = new ArrayList<City>();
		cityList = (ListView) view.findViewById(R.id.cityCardListView);

		cityCardEdit = (EditText) view.findViewById(R.id.cityCardEdit);
		ImageButton cityButton = (ImageButton) view
				.findViewById(R.id.cityCardBtn);

		GetSupCatsTask supCatsTask = new GetSupCatsTask(getActivity());
		supCatsTask.execute(getActivity().getIntent().getExtras()
				.getBundle("splashBundle").getString("cats"));
		subSpecEdit = (SpezAutoCompleteView) view
				.findViewById(R.id.subCardSpecEdit);
		subSpecEdit.setTokenClickStyle(TokenClickStyle.Delete);
		subSpecEdit.allowDuplicates(false);

		try {
			categories = supCatsTask.get();

		} catch (Exception e1) {
		}

		String[] cats = new String[categories.size()];
		for (int i = 0; i < cats.length; i++) {
			cats[i] = categories.get(i).getName();
		}

		AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view
				.findViewById(R.id.supCardSpecEdit);
		autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, cats));

		autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view;
				String name = text.getText().toString();

				for (int i = 0; i < categories.size(); i++) {
					if (categories.get(i).getName().equals(name)) {
						subCategories = categories.get(i).getSubCategories();
					}
				}
				String[] subCats = new String[subCategories.size()];

				for (int i = 0; i < subCats.length; i++) {
					subCats[i] = subCategories.get(i).getName();
				}
				Log.d("s_cats", Arrays.asList(subCats).toString());
				subSpecEdit.setAdapter(new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1, subCats));

				subSpecEdit.setTokenListener(new TokenListener() {

					@Override
					public void onTokenRemoved(Object arg0) {
						for (int i = 0; i < subCatBuilder.size(); i++) {
							if (subCatBuilder.get(i).getName()
									.equals((String) arg0)) {
								subCatBuilder.remove(i);
								if (subCatNamesBuilder.toString().length() > 0) {
									String str = subCatNamesBuilder.toString();
									String strCon = str.replace((String) arg0,
											"");
									subCatNamesBuilder.delete(0, str.length());
									subCatNamesBuilder.append(strCon);
								}
							}
						}
					}

					@Override
					public void onTokenAdded(Object arg0) {
						for (int i = 0; i < subCategories.size(); i++) {
							if (subCategories.get(i).getName()
									.equals((String) arg0)) {
								subCatBuilder.add(subCategories.get(i));
								if (subCatNamesBuilder.toString().length() == 0) {
									subCatNamesBuilder.append((String) arg0);
								} else {
									subCatNamesBuilder.append(" "
											+ (String) arg0);
								}
							}
						}
					}
				});
			}
		});

		cityButton.setOnClickListener(this);

		subscribeParams = new StringBuilder();

		EditText telEditText = (EditText) view.findViewById(R.id.telEdit);
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

		EditText salaryFromEdit = (EditText) view
				.findViewById(R.id.salaryFromEdit);
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
		} catch (Exception e) {
		}

		Log.d("work types", workTypes.toString());
		timeCardLayout = (LinearLayout) view.findViewById(R.id.timeBoxLayout);
		for (int i = 0; i < workTypes.length(); i++) {
			try {
				JSONObject currObj = workTypes.getJSONObject(i);
				CheckBox box = new CheckBox(getActivity());
				box.setText(currObj.getString("name"));
				timeCardLayout.addView(box);
			} catch (Exception e) {
			}
		}

		Button cardCheckCityBtn = (Button) view
				.findViewById(R.id.checkCardCityBtn);
		cardCheckCityBtn.setOnClickListener(this);

		Button signBtn = (Button) view.findViewById(R.id.signBtn);
		cloudMessaging = GoogleCloudMessaging.getInstance(getActivity());
		signBtn.setOnClickListener(this);

		mailEdit = (FormEditText) view.findViewById(R.id.mailEdit);
		mailEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!mailEdit.hasFocus()) {
					mailEdit.testValidity();
				}
			}
		});

		dateEdit = (FormEditText) view.findViewById(R.id.dateEdit);
		dateEdit.addValidator(new AgeValidator(getActivity()));

		cardContainer = (LinearLayout) view.findViewById(R.id.cards_container);
		inpcp = new LinearLayout.LayoutParams(0, 0);
		addBtn = (ImageButton) view.findViewById(R.id.addCardBtn);
		for (int i = 1; i < cardContainer.getChildCount() - 1; i++) {
			LinearLayout currLayout = (LinearLayout) cardContainer
					.getChildAt(i);
			currLayout.getChildAt(0).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteCard(v);
				}

			});
		}

		LinearLayout personalCard = (LinearLayout) view
				.findViewById(R.id.personalDataCard);

		int height = preferences.getInt("screenHeight", 0);
		pcp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		pcp.setMargins(height / 32, height / 32, height / 32, height / 32);
		personalCard.setLayoutParams(pcp);

		dateEdit.setOnClickListener(this);

		addBtn = (ImageButton) view.findViewById(R.id.addCardBtn);
		addBtn.setOnClickListener(this);
		return view;
	};

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

	private String getRegistatrationID(Context context) {

		String registID = preferences.getString("registration_id", "");
		Log.d("reg_id", registID);
		if (registID.isEmpty()) {
			return "";
		}

		return registID;
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
								.getInstance(getActivity());
					}

					Log.d("reg",
							regID = cloudMessaging.register("829773271300"));
					cloudMessaging.close();

					RegGCMTask gcmTask = new RegGCMTask(getActivity());
					gcmTask.execute(regID);

					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("registration_id", regID);

					editor.commit();
				} catch (Exception exception) {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cityCardBtn:
			cityCardEdit.setText(preferences.getString("cityName", ""));
			break;
		case R.id.addCardBtn:
			addCard();
			break;

		case R.id.signBtn:
			if (isNetworkConnected()) {
				sign();
			} else {
				Toast.makeText(
						getActivity(),
						"Нет подключения к интернету. Невозможно оформить подписку",
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.dateEdit:
			dateDialog();
			break;

		case R.id.checkCardCityBtn:
			checkCity();
			break;

		default:
			break;
		}

	}

	private void addCard() {
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

	private void deleteCard(View v) {
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

	private void sign() {
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
			} catch (Exception e) {
			}
		}

		if (cityList.getChildCount() > 0) {
			if (citiesIds.length > 0) {
				Log.d("city ids", Arrays.asList(citiesIds).toString());
				for (int i = 0; i < citiesIds.length; i++) {
					subscribeParams.append("&city[]=" + citiesIds[i]);
				}
			}
		}

		EditText salaryEdit = (EditText) view.findViewById(R.id.salaryFromEdit);
		CheckBox includeBox = (CheckBox) view
				.findViewById(R.id.includeCheckBox);

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
				subscribeParams.append("&salarywonull[]="
						+ salaryEdit.getText().toString());
			}

		}

		if (subCatBuilder.size() > 0) {
			for (int i = 0; i < subCatBuilder.size(); i++) {
				subscribeParams.append("&category[]="
						+ subCatBuilder.get(i).getId());
			}
		}

		subscribeParams.append(workTypesString.toString());

		SubscribeTask subscribeTask = new SubscribeTask(getActivity());
		subscribeTask.execute(subscribeParams.toString());
		Log.d("sub_params", subscribeParams.toString());

		if (getRegistatrationID(getActivity()).equals("")) {
			registerInBackground();
		}

	}

	private void dateDialog() {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	private void checkCity() {
		GetCityTask cityTask = new GetCityTask(getActivity());
		Log.d("city", cityCardEdit.getText().toString());
		cityTask.execute(cityCardEdit.getText().toString());
		try {
			cities = cityTask.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
		String[] citiesNames = new String[cities.size()];
		citiesIds = new String[cities.size()];
		for (int i = 0; i < citiesNames.length; i++) {
			citiesNames[i] = cities.get(i).getCityName();
			citiesIds[i] = cities.get(i).getCityID();
			Log.d("city names", Arrays.asList(citiesNames).toString());
			Log.d("city ids", Arrays.asList(citiesIds).toString());
		}

		int height = preferences.getInt("screenHeight", 0);

		cityList.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, citiesNames));
		RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, citiesNames.length
						* height / 10);
		clp.addRule(RelativeLayout.BELOW, R.id.checkCardCityBtn);
		cityList.setLayoutParams(clp);

		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				cityList,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {

							for (int i = 0; i < cities.size(); i++) {
								if (cities
										.get(i)
										.getCityName()
										.equals(listView.getAdapter().getItem(
												position))) {

									cities.remove(i);

									String[] citiesNames = new String[cities
											.size()];
									for (int j = 0; j < citiesNames.length; j++) {
										citiesNames[j] = cities.get(j)
												.getCityName();
									}

									int height = preferences.getInt(
											"screenHeight", 0);

									cityList = (ListView) view
											.findViewById(R.id.cityCardListView);
									cityList.setAdapter(new ArrayAdapter<String>(
											getActivity(),
											android.R.layout.simple_list_item_1,
											citiesNames));
									RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(
											RelativeLayout.LayoutParams.MATCH_PARENT,
											citiesNames.length * height / 10);
									clp.addRule(RelativeLayout.BELOW,
											R.id.checkCardCityBtn);
									cityList.setLayoutParams(clp);
								}
							}
						}
					}
				});
		cityList.setOnTouchListener(touchListener);
		cityList.setOnScrollListener(touchListener.makeScrollListener());
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

}
