package mobi.esys.jobshunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.specific_data_type.Category;
import mobi.esys.specific_data_type.City;
import mobi.esys.specific_data_type.SuperCategory;
import mobi.esys.specific_data_type.Vacancy;
import mobi.esys.tasks.GetCityTask;
import mobi.esys.tasks.GetSupCatsTask;
import mobi.esys.tasks.GetVacTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ConditionsActivity extends Activity {
	private transient List<SuperCategory> categories;
	private transient List<Category> subCategories;
	private transient List<Category> subName;
	private transient ListView cityList;
	private transient List<City> cities;
	private transient ListView subSpecList;
	private transient ProgressDialog dialog;

	AutoCompleteTextView subSpecEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cond_activity);
		categories = new ArrayList<SuperCategory>();
		subCategories = new ArrayList<Category>();
		subName = new ArrayList<Category>();
		GetSupCatsTask supCatsTask = new GetSupCatsTask();
		supCatsTask.execute();
		subSpecEdit = (AutoCompleteTextView) findViewById(R.id.subSpecEdit);
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
				subSpecEdit.setText("");
				subSpecEdit.setAdapter(null);
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
			}
		});

		subSpecEdit.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final TextView text = ((TextView) arg1);
				Log.d("text", text.getText().toString());
				subSpecList = (ListView) findViewById(R.id.subSpecList);
				for (int i = 0; i < subCategories.size(); i++) {
					if (subCategories.get(i).getName()
							.equals(text.getText().toString())
							&& !subName.contains(text.getText().toString())) {
						subName.add(subCategories.get(i));
					}
				}

				String[] names = new String[subName.size()];
				for (int i = 0; i < names.length; i++) {
					names[i] = subName.get(i).getName();
				}
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int height = size.y;

				subSpecList.setAdapter(new ArrayAdapter<String>(
						ConditionsActivity.this,
						android.R.layout.simple_list_item_1, names));

				subSpecList
						.setOnItemLongClickListener(new OnItemLongClickListener() {

							@Override
							public boolean onItemLongClick(
									final AdapterView<?> arg0, final View arg1,
									final int arg2, final long arg3) {
								subName.remove(arg2);

								String[] names = new String[subName.size()];
								for (int i = 0; i < names.length; i++) {
									names[i] = subName.get(i).getName();
								}
								WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
								Display display = wm.getDefaultDisplay();
								Point size = new Point();
								display.getSize(size);
								final int height = size.y;

								subSpecList
										.setAdapter(new ArrayAdapter<String>(
												ConditionsActivity.this,
												android.R.layout.simple_list_item_1,
												names));
								final RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.MATCH_PARENT,
										names.length * height / 10);
								clp.addRule(RelativeLayout.BELOW,
										R.id.subSpecEdit);
								subSpecList.setLayoutParams(clp);
								return true;
							}
						});

				RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, names.length
								* height / 10);
				clp.addRule(RelativeLayout.BELOW, R.id.subSpecEdit);
				subSpecList.setLayoutParams(clp);
			}
		});

		Button chcBtn = (Button) findViewById(R.id.checkCityBtn);

		chcBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText cityEdit = (EditText) findViewById(R.id.cityEdit);
				GetCityTask cityTask = new GetCityTask();
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
				cityList.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(final AdapterView<?> arg0,
							final View arg1, final int arg2, final long arg3) {
						TextView textView = (TextView) arg1;
						for (int i = 0; i < cities.size(); i++) {
							if (cities.get(i).getCityName()
									.equals(textView.getText().toString())) {

								cities.remove(i);

								String[] citiesNames = new String[cities.size()];
								for (int j = 0; j < citiesNames.length; j++) {
									citiesNames[j] = cities.get(j)
											.getCityName();
								}
								WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
								final Display display = wm.getDefaultDisplay();
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
										citiesNames.length * height / 10);
								clp.addRule(RelativeLayout.BELOW,
										R.id.checkCityBtn);
								cityList.setLayoutParams(clp);
							}
						}
						return false;
					}
				});
			}
		});

		Button findVacBtn = (Button) findViewById(R.id.findVacBtn);
		findVacBtn.setOnClickListener(new OnClickListener() {

			StringBuilder paramBuilder = new StringBuilder();

			@Override
			public void onClick(final View v) {
				if (cityList != null && subSpecList != null) {
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
					if (subSpecList != null) {
						for (int i = 0; i < subSpecList.getChildCount(); i++) {
							final TextView currText = (TextView) subSpecList
									.getChildAt(i);
							for (int j = 0; j < subName.size(); j++) {
								if (subName
										.get(j)
										.getName()
										.contains(currText.getText().toString())) {
									if (subName.get(j).getProvider()
											.equals("hh")) {
										paramBuilder.append("&hhcats[]=")
												.append(subName.get(j).getId());
									} else {
										paramBuilder.append("&sjcats[]=")
												.append(subName.get(j).getId());
									}
								}
							}
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
							"Выберете хотя бы одни параметр",
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
}
