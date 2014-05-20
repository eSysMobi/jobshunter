package mobi.esys.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.constants.Constants;
import mobi.esys.custom_widgets.SpezAutoCompleteView;
import mobi.esys.jobshunter.MainActivity;
import mobi.esys.jobshunter.R;
import mobi.esys.specific_data_type.Category;
import mobi.esys.specific_data_type.City;
import mobi.esys.specific_data_type.SuperCategory;
import mobi.esys.specific_data_type.Vacancy;
import mobi.esys.swipe_dismiss.SwipeDismissListViewTouchListener;
import mobi.esys.tasks.GetCityTask;
import mobi.esys.tasks.GetSupCatsTask;
import mobi.esys.tasks.GetVacTask;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

public class ConditionFragment extends Fragment {
	private transient SharedPreferences preferences;
	private transient View view;
	private transient List<SuperCategory> categories;
	private transient List<Category> subCategories;
	private transient List<Category> subCatBuilder;
	private transient ListView cityList;
	private transient List<City> cities;
	private transient ListView subSpecList;
	private transient ProgressDialog dialog;
	private transient SpezAutoCompleteView subSpecEdit;
	private transient EditText cityEdit;
	private transient StringBuilder subCatNamesBuilder;
	private transient String sortString = "";

	private static final String CITY_NAME_STR = "cityName";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.cond_fragment, container, false);
		subCatBuilder = new ArrayList<Category>();
		subCatNamesBuilder = new StringBuilder();

		preferences = getActivity().getSharedPreferences(Constants.PREF_STRING,
				Context.MODE_PRIVATE);

		cityEdit = (EditText) view.findViewById(R.id.cityEdit);

		ImageButton cityBtn = (ImageButton) view.findViewById(R.id.cityBtn);
		cityBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences preferences = getActivity()
						.getSharedPreferences(Constants.PREF_STRING,
								Context.MODE_PRIVATE);

				if (preferences.getString(CITY_NAME_STR, "").equals("")
						|| preferences.getString(CITY_NAME_STR, "").equals(
								"Not Found")) {
					Toast.makeText(
							getActivity(),
							getActivity().getResources().getString(
									R.string.find_city_fail),
							Toast.LENGTH_SHORT).show();
				} else {
					cityEdit.setText(preferences.getString(CITY_NAME_STR, ""));
				}
			}
		});

		categories = new ArrayList<SuperCategory>();
		subCategories = new ArrayList<Category>();
		// subName = new ArrayList<Category>();
		GetSupCatsTask supCatsTask = new GetSupCatsTask(getActivity());
		supCatsTask.execute(getActivity().getIntent().getExtras()
				.getBundle("splashBundle").getString("cats"));
		subSpecEdit = (SpezAutoCompleteView) view
				.findViewById(R.id.subSpecEdit);
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

		AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view
				.findViewById(R.id.supSpecEdit);
		autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, cats));
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

		Button chcBtn = (Button) view.findViewById(R.id.checkCityBtn);

		chcBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				GetCityTask cityTask = new GetCityTask(getActivity());
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

				int height = preferences.getInt("screenHeight", 0);

				cityList = (ListView) view.findViewById(R.id.cityListView);
				cityList.setAdapter(new ArrayAdapter<String>(getActivity(),
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
											WindowManager wm = (WindowManager) getActivity()
													.getSystemService(
															Context.WINDOW_SERVICE);
											final Display display = wm
													.getDefaultDisplay();
											Point size = new Point();
											display.getSize(size);
											int height = size.y;

											cityList = (ListView) view
													.findViewById(R.id.cityListView);
											cityList.setAdapter(new ArrayAdapter<String>(
													getActivity(),
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

		Button findVacBtn = (Button) view.findViewById(R.id.findVacBtn);
		findVacBtn.setOnClickListener(new OnClickListener() {

			StringBuilder paramBuilder = new StringBuilder();

			@Override
			public void onClick(final View v) {
				if (isNetworkConnected()) {
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

					GetVacTask getVacTask = new GetVacTask(getActivity(),
							ConditionFragment.this);
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
					bundle.putString("sort", sortString);
					getVacTask.execute(bundle);

					ArrayList<Vacancy> jobs = new ArrayList<Vacancy>();
					try {
						jobs = getVacTask.get();
					} catch (InterruptedException e) {
					} catch (ExecutionException e) {
					}

					((MainActivity) getActivity()).showFilter();

					Bundle bundle2 = new Bundle();
					bundle2.putParcelableArrayList("jobsList", jobs);
					bundle2.putBundle("getVacParams", bundle);
					bundle2.putBoolean("isFav", false);
					bundle2.putString("cats", subCatNamesBuilder.toString());
					bundle2.putString("sort", "");

					Fragment jobsFragment = new JobsFragment();
					jobsFragment.setArguments(bundle2);
					((MainActivity) getActivity()).getFragmentManager()
							.beginTransaction()
							.replace(R.id.frmCont, jobsFragment, "jobsTag")
							.commit();

					((MainActivity) getActivity()).setFragmentLabel("Вакансии");

				} else {
					Toast.makeText(
							getActivity(),
							"Нет подключения к интерненту.Невозможно получить список вакансий",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		return view;
	}

	private void initProgressDialog() {
		dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Поиск вакансий");
		dialog.setMessage("Подождите. Идёт поиск вакансий по вашим параметрам");
		dialog.setIndeterminate(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void dismissDialog() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void dissmissCityDialog() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
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
