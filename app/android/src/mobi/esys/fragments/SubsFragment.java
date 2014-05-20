package mobi.esys.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.constants.Constants;
import mobi.esys.jobshunter.R;
import mobi.esys.specific_data_type.Subscribe;
import mobi.esys.specific_data_type.SupSubs;
import mobi.esys.swipe_dismiss.SwipeDismissListViewTouchListener;
import mobi.esys.swipe_dismiss.SwipeDismissListViewTouchListener.DismissCallbacks;
import mobi.esys.tasks.GetCityNameTask;
import mobi.esys.tasks.GetSubscribesTask;
import mobi.esys.tasks.UnsubscribeTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SubsFragment extends Fragment {
	private transient View view;
	private transient List<String> alreadyTypes;
	private transient List<SupSubs> supSubs;
	private transient JSONArray subs;
	private transient JSONArray subsTypes;
	private transient JSONObject subsTypesCurr;
	private transient JSONObject subsCurr;
	private transient LinearLayout layout;
	private transient SharedPreferences preferences;
	private transient JSONArray workTypes;

	private transient List<String> salaryNull;
	private transient List<String> salary;
	private transient LinearLayout currCard;

	private transient List<String> workIDs = new ArrayList<String>();
	private transient List<String> cityIDs = new ArrayList<String>();
	private transient List<String> catIDs = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.subs_fragment, container, false);

		supSubs = new ArrayList<SupSubs>();
		alreadyTypes = new ArrayList<String>();

		preferences = getActivity().getSharedPreferences(Constants.PREF_STRING,
				Context.MODE_PRIVATE);

		int height = preferences.getInt("screenHeight", 0);
		layout = (LinearLayout) view.findViewById(R.id.subsCardsContainer);
		layout.setOrientation(LinearLayout.VERTICAL);

		try {
			subs = new JSONArray(preferences.getString("subs", ""));
		} catch (JSONException e) {
		}

		if (subs.length() > 0) {

			try {
				subsTypes = new JSONArray(
						preferences.getString("subsTypes", ""));
			} catch (JSONException e) {
			}
			for (int i = 0; i < subs.length(); i++) {
				for (int j = 0; j < subsTypes.length(); j++) {
					try {
						subsCurr = subs.getJSONObject(i);
					} catch (JSONException e) {
					}
					try {
						subsTypesCurr = subsTypes.getJSONObject(j);
					} catch (JSONException e) {
					}
					try {
						if (subsCurr.getString("sub_type").equals(
								subsTypesCurr.getString("id"))) {

							if (subsCurr.getString("sub_type").equals(
									subsTypesCurr.getString("id"))) {

								if (supSubs.size() > 0) {
									for (int k = 0; k < supSubs.size(); k++) {
										if (supSubs
												.get(k)
												.getSubsID()
												.equals(subsTypesCurr
														.getString("id"))) {
											supSubs.get(k)
													.addSubs(
															new Subscribe(
																	subsCurr.getString("sub_type"),
																	subsTypesCurr
																			.getString("name"),
																	subsCurr.getString("sub_id")));
										}
									}
								}

							}
							if (!alreadyTypes.contains(subsTypesCurr
									.getString("id"))) {

								alreadyTypes.add(subsTypesCurr.getString("id"));

								List<Subscribe> subscribes = new ArrayList<Subscribe>();

								subscribes.add(new Subscribe(subsCurr
										.getString("sub_type"), subsTypesCurr
										.getString("name"), subsCurr
										.getString("sub_id")));

								Log.d("subscr", subscribes.toString());
								supSubs.add(new SupSubs(subsTypesCurr
										.getString("id"), subscribes));

								Log.d("subscr", supSubs.toString());

								LinearLayout.LayoutParams cp = new LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT);

								cp.setMargins(height / 32, height / 32,
										height / 32, height / 32);
								LinearLayout card = new LinearLayout(
										layout.getContext());
								card.setBackgroundResource(R.drawable.info_frame);
								card.setLayoutParams(cp);
								TextView textView = new TextView(
										layout.getContext());
								ListView listView = new ListView(
										layout.getContext());

								card.addView(textView);
								card.addView(listView);
								layout.addView(card);
							}

						}

					} catch (JSONException e) {
					}
				}
			}

			Log.d("subs", supSubs.toString());
			Log.d("subs", String.valueOf(supSubs.size()));

			for (int k = 0; k < alreadyTypes.size(); k++) {
				currCard = (LinearLayout) layout.getChildAt(k);
				TextView label = (TextView) currCard.getChildAt(0);

				for (int k1 = 0; k1 < supSubs.size(); k1++) {

					List<String> workSchedule = new ArrayList<String>();

					if (alreadyTypes.get(k).equals(supSubs.get(k1).getSubsID())) {
						Log.d("list parent", supSubs.toString());
						Log.d("list size", String.valueOf(supSubs.size()));
						Log.d("list", supSubs.get(k1).getSubscribes()
								.toString());
						List<Subscribe> subscribes2 = supSubs.get(k1)
								.getSubscribes();
						salaryNull = new ArrayList<String>();
						Log.d("size", String.valueOf(subscribes2.size()));
						for (int l = 0; l < subscribes2.size(); l++) {
							if (subscribes2.get(l).getSubID().equals("4")) {
								label.setText(subscribes2.get(l).getSubName());

								salaryNull.add(supSubs.get(k1).getSubscribes()
										.get(l).getSpecID()
										+ " руб.");
								ListView list = (ListView) currCard
										.getChildAt(1);
								Log.d("salary", salaryNull.toString());
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										getActivity(),
										android.R.layout.simple_list_item_1,
										salaryNull);
								list.setAdapter(adapter);

								SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
										list, new DismissCallbacks() {

											@Override
											public void onDismiss(
													ListView listView,
													int[] reverseSortedPositions) {

												List<String> newSalary = new ArrayList<String>();
												for (int i = 0; i < listView
														.getCount(); i++) {
													newSalary.add(listView
															.getAdapter()
															.getItem(i)
															.toString());
												}
												for (int position : reverseSortedPositions) {

													for (int i = 0; i < newSalary
															.size(); i++) {

														if (newSalary
																.get(i)

																.equals(listView
																		.getAdapter()
																		.getItem(
																				position))) {
															newSalary.remove(i);
															UnsubscribeTask task = new UnsubscribeTask(
																	getActivity());
															String str = listView
																	.getAdapter()
																	.getItem(
																			position)
																	.toString()
																	.replace(
																			" руб.",
																			"");
															task.execute("&salarywonull[]="
																	+ str);
														}
													}

												}
												ArrayAdapter<String> adapter = new ArrayAdapter<String>(
														getActivity(),
														android.R.layout.simple_list_item_1,
														newSalary);

												listView.setAdapter(adapter);
												if (listView.getCount() == 0) {
													layout.removeView((View) listView
															.getParent());
												}

												GetSubscribesTask getSubscribesTask = new GetSubscribesTask(
														getActivity());
												getSubscribesTask.execute();

											}

											@Override
											public boolean canDismiss(
													int position) {
												return true;
											}
										});

								list.setOnTouchListener(touchListener);
								list.setOnScrollListener(touchListener
										.makeScrollListener());

							}

							salary = new ArrayList<String>();
							if (subscribes2.get(l).getSubID().equals("5")) {
								label.setText(subscribes2.get(l).getSubName());

								salary.add(supSubs.get(k1).getSubscribes()
										.get(l).getSpecID()
										+ " руб.");
								ListView list = (ListView) currCard
										.getChildAt(1);
								Log.d("salary", salary.toString());
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										getActivity(),
										android.R.layout.simple_list_item_1,
										salary);
								list.setAdapter(adapter);

								SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
										list, new DismissCallbacks() {

											@Override
											public void onDismiss(
													ListView listView,
													int[] reverseSortedPositions) {

												List<String> newSalary = new ArrayList<String>();
												for (int i = 0; i < listView
														.getCount(); i++) {
													newSalary.add(listView
															.getAdapter()
															.getItem(i)
															.toString());
												}
												for (int position : reverseSortedPositions) {

													for (int i = 0; i < newSalary
															.size(); i++) {

														if (newSalary
																.get(i)

																.equals(listView
																		.getAdapter()
																		.getItem(
																				position))) {
															newSalary.remove(i);
															UnsubscribeTask task = new UnsubscribeTask(
																	getActivity());
															String str = listView
																	.getAdapter()
																	.getItem(
																			position)
																	.toString()
																	.replace(
																			" руб.",
																			"");
															task.execute("&salarywnull[]="
																	+ str);
														}
													}

												}
												ArrayAdapter<String> adapter = new ArrayAdapter<String>(
														getActivity(),
														android.R.layout.simple_list_item_1,
														newSalary);

												listView.setAdapter(adapter);
												if (listView.getCount() == 0) {
													layout.removeView((View) listView
															.getParent());
												}

												GetSubscribesTask getSubscribesTask = new GetSubscribesTask(
														getActivity());
												getSubscribesTask.execute();

											}

											@Override
											public boolean canDismiss(
													int position) {
												return true;
											}
										});

								list.setOnTouchListener(touchListener);
								list.setOnScrollListener(touchListener
										.makeScrollListener());
							}
							if (subscribes2.get(l).getSubID().equals("2")) {
								label.setText(subscribes2.get(l).getSubName());

								try {
									workTypes = new JSONArray(
											preferences.getString("workTypes",
													""));
								} catch (JSONException e) {
									e.printStackTrace();
								}

								for (int m = 0; m < workTypes.length(); m++) {
									try {
										if (workTypes
												.getJSONObject(m)
												.getString("id")
												.equals(supSubs.get(k1)
														.getSubscribes().get(l)
														.getSpecID())) {
											workIDs.add(supSubs.get(k1)
													.getSubscribes().get(l)
													.getSpecID());
											workSchedule.add(workTypes
													.getJSONObject(m)
													.getString("name"));
										}
									} catch (JSONException e) {
									}
									ListView list = (ListView) currCard
											.getChildAt(1);
									ArrayAdapter<String> adapter = new ArrayAdapter<String>(
											getActivity(),
											android.R.layout.simple_list_item_1,
											workSchedule);
									list.setAdapter(adapter);

									SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
											list, new DismissCallbacks() {

												@Override
												public void onDismiss(
														ListView listView,
														int[] reverseSortedPositions) {

													List<String> newWorks = new ArrayList<String>();
													for (int i = 0; i < listView
															.getCount(); i++) {
														newWorks.add(listView
																.getAdapter()
																.getItem(i)
																.toString());
													}
													for (int position : reverseSortedPositions) {

														for (int i = 0; i < newWorks
																.size(); i++) {

															if (newWorks
																	.get(i)

																	.equals(listView
																			.getAdapter()
																			.getItem(
																					position))) {
																newWorks.remove(i);
																UnsubscribeTask task = new UnsubscribeTask(
																		getActivity());

																task.execute("&work[]="
																		+ workIDs
																				.get(i));

																workIDs.remove(i);
															}
														}

													}
													ArrayAdapter<String> adapter = new ArrayAdapter<String>(
															getActivity(),
															android.R.layout.simple_list_item_1,
															newWorks);

													listView.setAdapter(adapter);
													if (listView.getCount() == 0) {
														layout.removeView((View) listView
																.getParent());
													}

													GetSubscribesTask getSubscribesTask = new GetSubscribesTask(
															getActivity());
													getSubscribesTask.execute();

												}

												@Override
												public boolean canDismiss(
														int position) {
													return true;
												}
											});

									list.setOnTouchListener(touchListener);
									list.setOnScrollListener(touchListener
											.makeScrollListener());

								}

							}
							if (subscribes2.get(l).getSubID().equals("3")) {
								label.setText(subscribes2.get(l).getSubName());
								ListView list = (ListView) currCard
										.getChildAt(1);

								String[] citiesIds = new String[supSubs.get(k1)
										.getSubscribes().size()];

								for (int i = 0; i < citiesIds.length; i++) {
									citiesIds[i] = supSubs.get(k1)
											.getSubscribes().get(i).getSpecID();
									cityIDs.add(supSubs.get(k1).getSubscribes()
											.get(i).getSpecID());
								}

								GetCityNameTask cityNameTask = new GetCityNameTask(
										getActivity());
								cityNameTask.execute(citiesIds);
								List<String> citiesNames = new ArrayList<String>();

								try {
									citiesNames = cityNameTask.get();
								} catch (InterruptedException e) {
								} catch (ExecutionException e) {
								}

								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										getActivity(),
										android.R.layout.simple_list_item_1,
										citiesNames);
								list.setAdapter(adapter);

								SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
										list, new DismissCallbacks() {

											@Override
											public void onDismiss(
													ListView listView,
													int[] reverseSortedPositions) {

												List<String> newCity = new ArrayList<String>();
												for (int i = 0; i < listView
														.getCount(); i++) {
													newCity.add(listView
															.getAdapter()
															.getItem(i)
															.toString());
												}
												for (int position : reverseSortedPositions) {

													for (int i = 0; i < newCity
															.size(); i++) {

														if (newCity
																.get(i)

																.equals(listView
																		.getAdapter()
																		.getItem(
																				position))) {
															newCity.remove(i);
															UnsubscribeTask task = new UnsubscribeTask(
																	getActivity());

															task.execute("&city[]="
																	+ cityIDs
																			.get(i));

															cityIDs.remove(i);
														}
													}

												}
												ArrayAdapter<String> adapter = new ArrayAdapter<String>(
														getActivity(),
														android.R.layout.simple_list_item_1,
														newCity);

												listView.setAdapter(adapter);
												if (listView.getCount() == 0) {
													layout.removeView((View) listView
															.getParent());
												}

												GetSubscribesTask getSubscribesTask = new GetSubscribesTask(
														getActivity());
												getSubscribesTask.execute();

											}

											@Override
											public boolean canDismiss(
													int position) {
												return true;
											}
										});

								list.setOnTouchListener(touchListener);
								list.setOnScrollListener(touchListener
										.makeScrollListener());
							}
							if (subscribes2.get(l).getSubID().equals("1")) {
								label.setText(subscribes2.get(l).getSubName());
								List<String> catNames = new ArrayList<String>();
								ListView list = (ListView) currCard
										.getChildAt(1);
								JSONArray cats = new JSONArray();
								try {
									cats = new JSONArray(preferences.getString(
											"cats", ""));
									if (cats.length() > 0) {
										for (int i = 0; i < cats.length(); i++) {
											for (int j = 0; j < supSubs.get(k1)
													.getSubscribes().size(); j++) {

												if (cats.getJSONObject(i)
														.getString("id")
														.equals(supSubs
																.get(k1)
																.getSubscribes()
																.get(j)
																.getSpecID())) {
													catNames.add(cats
															.getJSONObject(i)
															.getString("name"));
													catIDs.add(supSubs.get(k1)
															.getSubscribes()
															.get(j).getSpecID());

												}

											}
										}
									}
								} catch (JSONException e) {
								}

								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										getActivity(),
										android.R.layout.simple_list_item_1,
										catNames);
								list.setAdapter(adapter);

								SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
										list, new DismissCallbacks() {

											@Override
											public void onDismiss(
													ListView listView,
													int[] reverseSortedPositions) {

												List<String> newCat = new ArrayList<String>();
												for (int i = 0; i < listView
														.getCount(); i++) {
													newCat.add(listView
															.getAdapter()
															.getItem(i)
															.toString());
												}
												for (int position : reverseSortedPositions) {

													for (int i = 0; i < newCat
															.size(); i++) {

														if (newCat
																.get(i)

																.equals(listView
																		.getAdapter()
																		.getItem(
																				position))) {
															newCat.remove(i);
															UnsubscribeTask task = new UnsubscribeTask(
																	getActivity());

															task.execute("&category[]="
																	+ catIDs.get(i));

															catIDs.remove(i);
														}
													}

												}
												ArrayAdapter<String> adapter = new ArrayAdapter<String>(
														getActivity(),
														android.R.layout.simple_list_item_1,
														newCat);

												listView.setAdapter(adapter);
												if (listView.getCount() == 0) {
													layout.removeView((View) listView
															.getParent());
												}

												GetSubscribesTask getSubscribesTask = new GetSubscribesTask(
														getActivity());
												getSubscribesTask.execute();

											}

											@Override
											public boolean canDismiss(
													int position) {
												return true;
											}
										});

								list.setOnTouchListener(touchListener);
								list.setOnScrollListener(touchListener
										.makeScrollListener());
							}
						}
					}
				}
			}
		} else {
			Fragment emptyFragment = new EmptyFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.frmCont, emptyFragment, "empty")
					.addToBackStack(null).commit();
		}

		return view;
	}
}
