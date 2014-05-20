package mobi.esys.fragments;

import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.adapters.JobsListAdapter;
import mobi.esys.constants.Constants;
import mobi.esys.jobshunter.MainActivity;
import mobi.esys.jobshunter.R;
import mobi.esys.specific_data_type.Vacancy;
import mobi.esys.tasks.GetVacTask;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fortysevendeg.swipelistview.SwipeListView;

public class JobsFragment extends Fragment {
	private transient View view;
	private transient SwipeListView jobsList;
	private transient List<Vacancy> vacancies;
	private transient JobsListAdapter jobsListAdapter;
	private transient int pageCount = 0;
	private transient ImageButton backBtn;
	private transient ImageButton addBtn;
	private transient ImageButton upBtn;
	private transient ImageButton firstPageBtn;
	private transient RelativeLayout catLayout;
	private transient String sortString = "";
	private transient int width;
	private transient int height;
	private transient SharedPreferences preferences;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		sortString = getArguments().getString("sort");
		vacancies = getArguments().getParcelableArrayList("jobsList");
		if (vacancies.size() > 0) {
			view = inflater
					.inflate(R.layout.joblist_fragment, container, false);

			View mainView = inflater.inflate(
					R.layout.activity_main_fragment_layout, container, false);

			if (!getArguments().getBoolean("isFav")) {
				((MainActivity) getActivity()).setFragmentLabel("Вакансии\n"
						+ "Страница 1");

			}

			preferences = getActivity().getSharedPreferences(
					Constants.PREF_STRING, Context.MODE_PRIVATE);

			width = preferences.getInt("screenWidth", 0);
			height = preferences.getInt("screenHeight", 0);
			Log.d("vac", vacancies.toString());
			addBtn = (ImageButton) view.findViewById(R.id.addBtn);
			backBtn = (ImageButton) view.findViewById(R.id.backBtn);
			upBtn = (ImageButton) view.findViewById(R.id.upBtn);
			firstPageBtn = (ImageButton) view.findViewById(R.id.firsPageBtn);
			catLayout = (RelativeLayout) view.findViewById(R.id.catLayout);

			jobsList = (SwipeListView) view.findViewById(R.id.jobsListView);

			if (getArguments().getBoolean("isFav")) {
				backBtn.setEnabled(false);

				RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, 0);
				clp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				catLayout.setLayoutParams(clp);

				RelativeLayout.LayoutParams bbP = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, 0);
				bbP.addRule(RelativeLayout.ABOVE, R.id.addBtn);
				backBtn.setLayoutParams(bbP);

				addBtn.setEnabled(false);
				RelativeLayout.LayoutParams abP = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, 0);
				abP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				addBtn.setLayoutParams(abP);
				upBtn.setLayoutParams(abP);
			} else {

				RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, height / 10);
				clp.addRule(RelativeLayout.ABOVE, R.id.backBtn);
				clp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				catLayout.setLayoutParams(clp);

				RelativeLayout.LayoutParams ublp = new RelativeLayout.LayoutParams(
						height / 10 - 20, height / 10 - 20);
				ublp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				ublp.addRule(RelativeLayout.CENTER_VERTICAL);
				ublp.setMargins(width / 72, width / 72, width / 72, width / 72);
				upBtn.setLayoutParams(ublp);

				RelativeLayout.LayoutParams fpblp = new RelativeLayout.LayoutParams(
						height / 10 - 20, height / 10 - 20);
				fpblp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				fpblp.setMargins(width / 72, width / 72, width / 72, width / 72);
				firstPageBtn.setLayoutParams(fpblp);

				RelativeLayout.LayoutParams ablp = new RelativeLayout.LayoutParams(
						height / 10 - 20, height / 10 - 20);
				ablp.addRule(RelativeLayout.ALIGN_TOP, R.id.firsPageBtn);
				ablp.addRule(RelativeLayout.RIGHT_OF, R.id.centerView);
				ablp.setMargins(width / 72, width / 72, width / 72, width / 72);
				addBtn.setLayoutParams(ablp);

				RelativeLayout.LayoutParams bblp = new RelativeLayout.LayoutParams(
						height / 10 - 20, height / 10 - 20);
				bblp.addRule(RelativeLayout.ALIGN_TOP, R.id.upBtn);
				bblp.addRule(RelativeLayout.LEFT_OF, R.id.addBtn);
				bblp.setMargins(width / 72, width / 72, width / 72, width / 72);
				backBtn.setLayoutParams(bblp);

			}
			jobsList = (SwipeListView) view.findViewById(R.id.jobsListView);
			jobsListAdapter = new JobsListAdapter(vacancies,
					R.layout.jobslist_item, getActivity(), height,
					getArguments().getBoolean("isFav"), jobsList);

			jobsList.setSwipeOpenOnLongPress(false);
			jobsList.setAnimationCacheEnabled(true);
			jobsList.setAdapter(jobsListAdapter);

			if (jobsList.getCount() < 20) {
				addBtn.setEnabled(false);
				RelativeLayout.LayoutParams abP = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, 0);
				abP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				addBtn.setLayoutParams(abP);
			}

			addBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pageCount++;
					((MainActivity) getActivity())
							.setFragmentLabel("Вакансии\n" + "Страница "
									+ String.valueOf(pageCount + 1));
					getVac(sortString);

				}

			});

			if (pageCount == 0) {
				backBtn.setEnabled(false);

			}
			backBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pageCount--;
					((MainActivity) getActivity())
							.setFragmentLabel("Вакансии\n" + "Страница "
									+ String.valueOf(pageCount + 1));
					getVac(sortString);

				}
			});

			upBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					jobsList.smoothScrollToPosition(0);
				}
			});

			firstPageBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pageCount = 0;
					((MainActivity) getActivity())
							.setFragmentLabel("Вакансии\n" + "Страница "
									+ String.valueOf(pageCount + 1));
					getVac(sortString);
				}
			});
		} else {
			Fragment emptyFragment = new EmptyFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.frmCont, emptyFragment, "empty")
					.addToBackStack(null).commit();
		}
		return view;
	}

	public void getVac(String sort) {
		GetVacTask getVacTask = new GetVacTask(getActivity(), JobsFragment.this);
		Bundle bundle = getArguments().getBundle("getVacParams");
		Bundle newVacBundle = new Bundle();
		newVacBundle.putString("params", bundle.getString("params"));
		newVacBundle.putString("page", String.valueOf(pageCount));
		newVacBundle.putString("sort", sort);

		sortString = sort;

		getVacTask.execute(newVacBundle);
		try {
			jobsListAdapter.clear();
			jobsListAdapter.addAll(getVacTask.get());
			if (!Boolean.valueOf(getVacTask.get().get(0).getHaveNextPage())) {
				addBtn.setEnabled(false);

			} else {
				addBtn.setEnabled(true);

			}
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
		jobsListAdapter.notifyDataSetChanged();

		if (pageCount == 0) {
			backBtn.setEnabled(false);

		} else {
			backBtn.setEnabled(true);

		}

		jobsList.smoothScrollToPosition(0);
	}

}
