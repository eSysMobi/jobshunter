package mobi.esys.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.adapters.MenuAdapter;
import mobi.esys.constants.Constants;
import mobi.esys.jobshunter.MainActivity;
import mobi.esys.jobshunter.R;
import mobi.esys.specific_data_type.MenuItem;
import mobi.esys.specific_data_type.Vacancy;
import mobi.esys.tasks.GetFavTask;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuFragment extends ListFragment {
	private transient List<MenuItem> menuItems;

	private static final int[] imgRes = { R.drawable.resume, R.drawable.find,
			R.drawable.subs, R.drawable.fav, R.drawable.login };
	private static String[] menLabels = { "Оформить подписку",
			"Поиск вакансий", "Подписки", "Избранное", "Войти" };
	private static final String[] fragTag = { "cvFrag", "jobsFrag", "subFrag",
			"jobsFrag", "loginFrag" };
	private transient Fragment cvFragments;
	private transient Fragment conditionFragment;
	private transient Fragment subsFragment;
	private transient Fragment jobsFragment;
	private transient Fragment logReqFragment;
	private transient Fragment loginFragment;
	private transient SharedPreferences preferences;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		menuItems = new ArrayList<MenuItem>();

		preferences = getActivity().getSharedPreferences(Constants.PREF_STRING,
				Context.MODE_PRIVATE);

		cvFragments = new CVFragments();
		conditionFragment = new ConditionFragment();
		subsFragment = new SubsFragment();
		jobsFragment = new JobsFragment();
		loginFragment = new LoginFragment();
		logReqFragment = new LogRequiredFragment();
		Fragment[] fragments = { cvFragments, conditionFragment, subsFragment,
				jobsFragment, loginFragment };

		for (int i = 0; i < imgRes.length; i++) {

			if (i == imgRes.length - 1) {
				if (preferences.getString("apiKey", "").equals("")) {
					menuItems.add(new MenuItem(menLabels[i], imgRes[i],
							fragments[i]));
				}
			} else {

				menuItems.add(new MenuItem(menLabels[i], imgRes[i],
						fragments[i]));

			}
		}

		MenuAdapter adapter = new MenuAdapter(menuItems, getActivity());
		setListAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (preferences.getString("apiKey", "").equals("")
						&& position != 1 && position != 4) {
					((MainActivity) getActivity())
							.getFragmentManager()
							.beginTransaction()
							.replace(R.id.frmCont, logReqFragment, "logRegFrag")
							.commit();

					((MainActivity) getActivity())
							.setFragmentLabel(menLabels[position]);

					if (((MainActivity) getActivity()).getSlidingMenu()
							.isActivated()) {
						((MainActivity) getActivity()).getSlidingMenu()
								.toggle();
					}

					((MainActivity) getActivity()).hideFilter();
				}

				else {
					if (position == 3) {
						Bundle bundle = new Bundle();
						bundle.putBoolean("isFav", true);
						menuItems.get(position).getFragment()
								.setArguments(bundle);

						GetFavTask favTask = new GetFavTask(getActivity());
						favTask.execute();

						try {
							ArrayList<Vacancy> favVac = favTask.get();
							bundle.putParcelableArrayList("jobsList", favVac);
						} catch (InterruptedException e) {
						} catch (ExecutionException e) {
						}

					}

					((MainActivity) getActivity())
							.getFragmentManager()
							.beginTransaction()
							.replace(R.id.frmCont,
									menuItems.get(position).getFragment(),
									fragTag[position]).commit();

					((MainActivity) getActivity())
							.setFragmentLabel(menLabels[position]);
					if (((MainActivity) getActivity()).getSlidingMenu()
							.isShown()) {
						((MainActivity) getActivity()).getSlidingMenu()
								.toggle();
					}
					((MainActivity) getActivity()).hideFilter();
				}

			}

		});
	}

	public void refreshMenu() {
		preferences = getActivity().getSharedPreferences(Constants.PREF_STRING,
				Context.MODE_PRIVATE);

		cvFragments = new CVFragments();
		conditionFragment = new ConditionFragment();
		subsFragment = new SubsFragment();
		jobsFragment = new JobsFragment();
		loginFragment = new LoginFragment();
		logReqFragment = new LogRequiredFragment();
		Fragment[] fragments = { cvFragments, conditionFragment, subsFragment,
				jobsFragment, loginFragment };

		for (int i = 0; i < imgRes.length; i++) {

			if (i == imgRes.length - 1) {
				if (preferences.getString("apiKey", "").equals("")) {
					menuItems.add(new MenuItem(menLabels[i], imgRes[i],
							fragments[i]));
				}
			} else {
				if (i == imgRes.length - 2) {
					menuItems.add(new MenuItem(menLabels[i] + " "
							+ preferences.getString("favCount", ""), imgRes[i],
							fragments[i]));
				} else {
					menuItems.add(new MenuItem(menLabels[i], imgRes[i],
							fragments[i]));
				}
			}
		}

		MenuAdapter adapter = new MenuAdapter(menuItems, getActivity());
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (preferences.getString("apiKey", "").equals("")
						&& position != 1 && position != 4) {
					((MainActivity) getActivity())
							.getFragmentManager()
							.beginTransaction()
							.replace(R.id.frmCont, logReqFragment, "logRegFrag")
							.commit();

					((MainActivity) getActivity())
							.setFragmentLabel(menLabels[position]);

					if (((MainActivity) getActivity()).getSlidingMenu()
							.isActivated()) {
						((MainActivity) getActivity()).getSlidingMenu()
								.toggle();
					}

					((MainActivity) getActivity()).hideFilter();
				}

				else {
					if (position == 3) {
						Bundle bundle = new Bundle();
						bundle.putBoolean("isFav", true);
						menuItems.get(position).getFragment()
								.setArguments(bundle);

						GetFavTask favTask = new GetFavTask(getActivity());
						favTask.execute();

						try {
							ArrayList<Vacancy> favVac = favTask.get();
							bundle.putParcelableArrayList("jobsList", favVac);
						} catch (InterruptedException e) {
						} catch (ExecutionException e) {
						}

					}

					((MainActivity) getActivity())
							.getFragmentManager()
							.beginTransaction()
							.replace(R.id.frmCont,
									menuItems.get(position).getFragment(),
									fragTag[position]).commit();

					((MainActivity) getActivity())
							.setFragmentLabel(menLabels[position]);
					if (((MainActivity) getActivity()).getSlidingMenu()
							.isShown()) {
						((MainActivity) getActivity()).getSlidingMenu()
								.toggle();
					}
					((MainActivity) getActivity()).hideFilter();
				}

			}

		});
	}
}
