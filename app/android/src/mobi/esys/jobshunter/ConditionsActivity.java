package mobi.esys.jobshunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.custom_widgets.SpezAutoCompleteView;
import mobi.esys.specific_data_type.Category;
import mobi.esys.specific_data_type.City;
import mobi.esys.specific_data_type.SuperCategory;
import mobi.esys.specific_data_type.Vacancy;
import mobi.esys.swipe_dismiss.SwipeDismissListViewTouchListener;
import mobi.esys.tasks.GetCityTask;
import mobi.esys.tasks.GetSupCatsTask;
import mobi.esys.tasks.GetVacTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tokenautocomplete.TokenCompleteTextView.TokenClickStyle;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;

public class ConditionsActivity extends Activity {
	private transient List<SuperCategory> categories;
	private transient List<Category> subCategories;
	private transient List<Category> subCatBuilder;
	private transient ListView cityList;
	private transient List<City> cities;
	private transient ListView subSpecList;
	private transient ProgressDialog dialog;
	private transient SpezAutoCompleteView subSpecEdit;
	private transient EditText cityEdit;

	private static final int CITY_VOICE_REC_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		subCatBuilder = new ArrayList<Category>();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cond_activity);

		getCurrentFocus().clearFocus();

		cityEdit = (EditText) findViewById(R.id.cityEdit);

		ImageButton cityBtn = (ImageButton) findViewById(R.id.cityBtn);
		cityBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences preferences = getSharedPreferences("JHPref",
						MODE_PRIVATE);

				cityEdit.setText(preferences.getString("cityName", ""));
			}
		});

		categories = new ArrayList<SuperCategory>();
		subCategories = new ArrayList<Category>();
		// subName = new ArrayList<Category>();
		GetSupCatsTask supCatsTask = new GetSupCatsTask(ConditionsActivity.this);
		supCatsTask.execute(getIntent().getExtras().getBundle("splashBundle")
				.getString("cats"));
		subSpecEdit = (SpezAutoCompleteView) findViewById(R.id.subSpecEdit);
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

		AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.supSpecEdit);
		autoCompleteTextView.setAdapter(new ArrayAdapter<String>(
				ConditionsActivity.this, android.R.layout.simple_list_item_1,
				cats));
		autoCompleteTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, final int start,
					final int before, final int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(final Editable s) {

			}
		});

		autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView text = (TextView) arg1;
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
				subSpecEdit.setAdapter(new ArrayAdapter<String>(
						ConditionsActivity.this,
						android.R.layout.simple_list_item_1, subCats));

				subSpecEdit.setTokenListener(new TokenListener() {

					@Override
					public void onTokenRemoved(Object arg0) {
						for (int i = 0; i < subCatBuilder.size(); i++) {
							if (subCatBuilder.get(i).getName()
									.equals((String) arg0)) {
								subCatBuilder.remove(i);
							}
						}
					}

					@Override
					public void onTokenAdded(Object arg0) {
						for (int i = 0; i < subCategories.size(); i++) {
							if (subCategories.get(i).getName()
									.equals((String) arg0)) {
								subCatBuilder.add(subCategories.get(i));
							}
						}
					}
				});
			}
		});

		ImageButton cityVoiceRecBtn = (ImageButton) findViewById(R.id.voiceBtn);
		cityVoiceRecBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				voiceRecognition(CITY_VOICE_REC_CODE,
						"Назовите название города, в котором вы хотели бы искать вакансии");
			}
		});

		Button chcBtn = (Button) findViewById(R.id.checkCityBtn);

		chcBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				GetCityTask cityTask = new GetCityTask(ConditionsActivity.this);
				Log.d("city", cityEdit.getText().toString());
				cityTask.execute(cityEdit.getText().toString());
				cities = new ArrayList<City>();
				try {
					cities = cityTask.get();
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
				String[] citiesNames = new String[cities.size()];
				for (int i = 0; i < citiesNames.length; i++) {
					citiesNames[i] = cities.get(i).getCityName();
				}

				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int height = size.y;

				cityList = (ListView) findViewById(R.id.cityListView);
				cityList.setAdapter(new ArrayAdapter<String>(
						ConditionsActivity.this,
						android.R.layout.simple_list_item_1, citiesNames));
				RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						citiesNames.length * height / 10);
				clp.addRule(RelativeLayout.BELOW, R.id.checkCityBtn);
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
												.equals(listView.getAdapter()
														.getItem(position))) {

											cities.remove(i);

											String[] citiesNames = new String[cities
													.size()];
											for (int j = 0; j < citiesNames.length; j++) {
												citiesNames[j] = cities.get(j)
														.getCityName();
											}
											WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
											final Display display = wm
													.getDefaultDisplay();
											Point size = new Point();
											display.getSize(size);
											int height = size.y;

											cityList = (ListView) findViewById(R.id.cityListView);
											cityList.setAdapter(new ArrayAdapter<String>(
													ConditionsActivity.this,
													android.R.layout.simple_list_item_1,
													citiesNames));
											RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(
													RelativeLayout.LayoutParams.MATCH_PARENT,
													citiesNames.length * height
															/ 10);
											clp.addRule(RelativeLayout.BELOW,
													R.id.checkCityBtn);
											cityList.setLayoutParams(clp);
										}
									}
								}
							}
						});
				cityList.setOnTouchListener(touchListener);
				cityList.setOnScrollListener(touchListener.makeScrollListener());
			}
		});

		Button findVacBtn = (Button) findViewById(R.id.findVacBtn);
		findVacBtn.setOnClickListener(new OnClickListener() {

			StringBuilder paramBuilder = new StringBuilder();

			@Override
			public void onClick(final View v) {
				if (cityList != null && subCatBuilder.size() != 0) {
					if (cityList != null) {
						for (int i = 0; i < cityList.getChildCount(); i++) {
							TextView currText = (TextView) cityList
									.getChildAt(i);
							for (int j = 0; j < cities.size(); j++) {
								if (cities.get(j).getCityName()
										.equals(currText.getText().toString())) {
									paramBuilder.append("&city[]=").append(
											cities.get(j).getCityID());
								}
							}
						}
					}
					if (subCatBuilder.size() != 0) {

						for (int j = 0; j < subCatBuilder.size(); j++) {

							paramBuilder.append("&cats[]=").append(
									subCatBuilder.get(j).getId());

						}

					}

					GetVacTask getVacTask = new GetVacTask(
							ConditionsActivity.this);
					String params = "";
					if (paramBuilder.toString().startsWith("&")) {
						paramBuilder.deleteCharAt(0);
						params = paramBuilder.toString();
					} else {
						params = paramBuilder.toString();
					}
					initProgressDialog();
					Bundle bundle = new Bundle();
					bundle.putString("params", params);
					bundle.putString("page", "0");
					getVacTask.execute(bundle);

					ArrayList<Vacancy> jobs = new ArrayList<Vacancy>();
					try {
						jobs = getVacTask.get();
					} catch (InterruptedException e) {
					} catch (ExecutionException e) {
					}
					finish();

					Intent jobsIntent = new Intent(ConditionsActivity.this,
							JobsListActivity.class);
					jobsIntent.putParcelableArrayListExtra("jobsList", jobs);
					jobsIntent.putExtra("getVacParams", bundle);
					jobsIntent.putExtra("isFav", false);
					startActivity(jobsIntent);
				} else if (subSpecList == null) {
					Toast.makeText(ConditionsActivity.this,
							"Уточните специализации", Toast.LENGTH_SHORT)
							.show();

				} else {
					Toast.makeText(ConditionsActivity.this,
							"Заполните поля города и специализаций",
							Toast.LENGTH_SHORT).show();
				}
			}

		});

	}

	private void initProgressDialog() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Поиск вакансий");
		dialog.setMessage("Подождите. Идёт поиск вакансий по вашим параметрам");
		dialog.setIndeterminate(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void dissmissCityDialog() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void voiceRecognition(int RecCode, String extraPrompt) {
		final Intent intent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, extraPrompt);
		startActivityForResult(intent, RecCode);
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (requestCode == CITY_VOICE_REC_CODE && resultCode == RESULT_OK) {
			final ArrayList<String> comands = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			cityEdit.setText(comands.get(0));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
