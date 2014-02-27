package mobi.esys.jobshunter;

import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.adapters.JobsListAdapter;
import mobi.esys.specific_data_type.Vacancy;
import mobi.esys.tasks.AddToFavTask;
import mobi.esys.tasks.GetVacTask;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

public class JobsListActivity extends Activity {
	private transient SwipeListView jobsList;
	private transient List<Vacancy> vacancies;
	private transient JobsListAdapter jobsListAdapter;
	private transient int pageCount = 0;
	private transient Button backBtn;
	private transient Button addBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_joblist);

		vacancies = getIntent().getParcelableArrayListExtra("jobsList");

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		Log.d("vac", vacancies.toString());
		addBtn = (Button) findViewById(R.id.addBtn);
		backBtn = (Button) findViewById(R.id.backBtn);

		if (getIntent().getExtras().getBoolean("isFav")) {
			backBtn.setEnabled(false);

			RelativeLayout.LayoutParams bbP = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 0);
			bbP.addRule(RelativeLayout.ABOVE, R.id.addBtn);
			backBtn.setLayoutParams(bbP);

			addBtn.setEnabled(false);
			RelativeLayout.LayoutParams abP = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 0);
			abP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			addBtn.setLayoutParams(abP);
		}
		jobsList = (SwipeListView) findViewById(R.id.jobsListView);
		jobsListAdapter = new JobsListAdapter(vacancies,
				R.layout.jobslist_item, JobsListActivity.this, height,
				getIntent().getExtras().getBoolean("isFav"));

		jobsList.setSwipeOpenOnLongPress(false);
		jobsList.setAnimationCacheEnabled(true);
		jobsList.setAdapter(new SlideExpandableListAdapter(jobsListAdapter,
				R.id.moreInfoBtn, R.id.hiddenLayout));

		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageCount++;
				getVac();

			}

		});

		if (pageCount == 0) {
			backBtn.setEnabled(false);

			RelativeLayout.LayoutParams bbP = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 0);
			bbP.addRule(RelativeLayout.ABOVE, R.id.addBtn);
			backBtn.setLayoutParams(bbP);
		}
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				pageCount--;
				getVac();

			}
		});

	}

	public void lockSwipe() {
		jobsList.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
	}

	public void unlockSwipe() {
		jobsList.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
	}

	public void addVac2Fav(Vacancy addVacancy) {

		SharedPreferences preferences = getSharedPreferences("JHPref",
				MODE_PRIVATE);
		AddToFavTask addToFavTask = new AddToFavTask();
		Bundle addParams = new Bundle();
		addParams.putString("siteID", addVacancy.getVacancyID());
		addParams.putString("site", addVacancy.getVacancyProvider());
		String apiKey = preferences.getString("apiKey", "");
		String userID = preferences.getString("userID", "");
		if (!apiKey.equals("")) {
			addParams.putString("apiKey", apiKey);
			addParams.putString("userID", userID);
		}
		addToFavTask.execute(addParams);
		String status = "";
		try {
			if (addToFavTask.get().has("status")) {
				status = addToFavTask.get().getString("status");
			}
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		} catch (JSONException e) {
		}
		if (status.equals("success")) {
			Toast.makeText(JobsListActivity.this,
					"¬аканси€ успешно добавлена в избранное",
					Toast.LENGTH_SHORT).show();
		} else if (status.equals("Already bookmarked")) {
			Toast.makeText(JobsListActivity.this,
					"ƒанна€ ваканси€ уже есть в избранном", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void getVac() {
		GetVacTask getVacTask = new GetVacTask(JobsListActivity.this);
		Bundle bundle = getIntent().getExtras().getBundle("getVacParams");
		Bundle newVacBundle = new Bundle();
		newVacBundle.putString("params", bundle.getString("params"));
		newVacBundle.putString("page", String.valueOf(pageCount));

		getVacTask.execute(newVacBundle);
		try {
			jobsListAdapter.clear();
			jobsListAdapter.addAll(getVacTask.get());
			if (!Boolean.valueOf(getVacTask.get().get(0).getHaveNextPage())) {
				addBtn.setEnabled(false);
				RelativeLayout.LayoutParams abP = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, 0);
				abP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				addBtn.setLayoutParams(abP);
			} else {
				addBtn.setEnabled(true);
				RelativeLayout.LayoutParams abP = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				abP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				addBtn.setLayoutParams(abP);
			}
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
		jobsListAdapter.notifyDataSetChanged();

		if (pageCount == 0) {
			backBtn.setEnabled(false);
			RelativeLayout.LayoutParams bbP = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 0);
			bbP.addRule(RelativeLayout.ABOVE, R.id.addBtn);
			backBtn.setLayoutParams(bbP);
		} else {
			backBtn.setEnabled(true);
			RelativeLayout.LayoutParams bbP = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			bbP.addRule(RelativeLayout.ABOVE, R.id.addBtn);
			backBtn.setLayoutParams(bbP);
		}

		jobsList.smoothScrollToPosition(0);
	}

	@Override
	protected void onStop() {
		super.onStop();

		finish();
		Log.d("stop", "stop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		finish();
		Log.d("stop", "destroy");
	}

	@Override
	protected void onPause() {
		super.onPause();

		finish();
		Log.d("stop", "pause");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		finish();
		if (!getIntent().getExtras().getBoolean("isFav")) {
			startActivity(new Intent(JobsListActivity.this,
					ConditionsActivity.class));
		} else {
			startActivity(new Intent(JobsListActivity.this, MainActivity.class));
		}
		Log.d("stop", "back");
	}

}
