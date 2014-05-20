package mobi.esys.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mobi.esys.constants.Constants;
import mobi.esys.fragments.MenuFragment;
import mobi.esys.jobshunter.R;
import mobi.esys.specific_data_type.Vacancy;
import mobi.esys.string_process.DateStringProcess;
import mobi.esys.tasks.AddToFavTask;
import mobi.esys.tasks.DeleteFavTask;

import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Html;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;

public class JobsListAdapter extends ArrayAdapter<Vacancy> {
	private transient List<Vacancy> vacancies;

	private transient final LayoutInflater inflater;

	private transient final Context context;

	private transient int layout;
	private transient int defHeight;
	private transient int margin;
	private transient int openPos = -1;
	private transient int mcbHeight;

	private transient List<String> vacNames;

	private transient ListView parentView;

	private transient boolean isFav = false;

	private transient JobsListViewHolder holder;

	private transient Bitmap loadedBitmap;

	private transient SwipeListView listView;

	private static final String USER_ID = Constants.USER_ID;
	private static final String PREF = Constants.PREF_STRING;
	private static final String API_KEY = Constants.API_KEY;

	public JobsListAdapter(final List<Vacancy> vacancies, final int layout,
			final Context context, final int height, final boolean isFav,
			SwipeListView listView) {
		super(context, layout, vacancies);
		this.layout = layout;
		this.vacancies = vacancies;
		this.isFav = isFav;
		this.loadedBitmap = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.ic_launcher);
		this.context = context;
		this.defHeight = height / 7;
		this.margin = height / 64;
		this.mcbHeight = height / 10;
		this.inflater = ((LayoutInflater) context.getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		this.listView = listView;

	}

	@Override
	public int getCount() {
		return vacancies.size();

	}

	@Override
	public Vacancy getItem(final int index) {
		return vacancies.get(index);
	}

	@Override
	public long getItemId(final int itemID) {
		return vacancies.get(itemID).hashCode();
	}

	@Override
	public View getView(final int arg0, final View arg1, final ViewGroup arg2) {
		View vacView = arg1;

		if (vacView == null) {
			vacView = this.inflater.inflate(layout, arg2, false);
		}
		vacView.setFocusable(true);
		vacView.setFocusableInTouchMode(true);

		parentView = (ListView) arg2;
		parentView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(final AbsListView view,
					final int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem > openPos) {
					openPos = -1;
					listView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
				}
			}
		});
		ImageButton favButton = (ImageButton) vacView
				.findViewById(R.id.vacFavBtn);

		if (isFav) {
			favButton.setEnabled(false);
			favButton.setVisibility(View.GONE);
		} else {
			favButton.setEnabled(true);
			favButton.setVisibility(View.VISIBLE);
		}

		try {
			if (vacancies.get(arg0).getVacancyProvider().equals("sj")) {
				loadedBitmap = BitmapFactory.decodeStream(context.getAssets()
						.open("sj.jpg"));
			} else {
				loadedBitmap = BitmapFactory.decodeStream(context.getAssets()
						.open("hh.png"));
			}
		} catch (IOException e) {
		}

		holder = new JobsListViewHolder();
		holder.position = arg0;

		holder.itemLayout = (RelativeLayout) vacView
				.findViewById(R.id.jobsListItem);
		final AbsListView.LayoutParams iParams = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT, defHeight);

		holder.itemLayout.setLayoutParams(iParams);

		holder.hiddenLayout = (RelativeLayout) vacView
				.findViewById(R.id.hiddenLayout);
		final TextView vacDesc = (TextView) vacView
				.findViewById(R.id.hiddenVacDescription);

		vacDesc.setText(Html.fromHtml(getItem(arg0).getVacancyDescription()));

		final ImageButton delVacButton = (ImageButton) vacView
				.findViewById(R.id.vacDelBtn);
		delVacButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View clickView) {

				if (isFav) {
					DeleteFavTask deleteFavTask = new DeleteFavTask(context);
					Bundle deleteParams = new Bundle();
					final SharedPreferences preferences = context
							.getSharedPreferences(PREF, Context.MODE_PRIVATE);
					deleteParams.putString(USER_ID,
							preferences.getString(USER_ID, ""));
					deleteParams.putString(API_KEY,
							preferences.getString(API_KEY, ""));
					deleteParams
							.putString("favID", getItem(arg0).getFavVacID());
					deleteFavTask.execute(deleteParams);
				}

				vacancies.remove(arg0);
				notifyDataSetChanged();
			}
		});

		ImageButton favVacBtn = (ImageButton) vacView
				.findViewById(R.id.vacFavBtn);

		favVacBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View clickView) {

				SharedPreferences preferences = context.getSharedPreferences(
						PREF, Context.MODE_PRIVATE);
				AddToFavTask addToFavTask = new AddToFavTask(context);
				Bundle addParams = new Bundle();
				addParams.putString("siteID", getItem(arg0).getVacancyID());
				addParams.putString("site", getItem(arg0).getVacancyProvider());
				String apiKey = preferences.getString(API_KEY, "");
				String userID = preferences.getString(USER_ID, "");
				if (!apiKey.equals("")) {
					addParams.putString(API_KEY, apiKey);
					addParams.putString(USER_ID, userID);
				}
				Log.d("fav_params", addParams.toString());
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
					Toast.makeText(context, R.string.fav_success,
							Toast.LENGTH_SHORT).show();
					Fragment menuFrag = ((Activity) context)
							.getFragmentManager().findFragmentByTag("menuFrag");
					if (menuFrag instanceof MenuFragment) {
						((MenuFragment) menuFrag).refreshMenu();
					}
				} else if (status.equals("Already bookmarked")) {
					Toast.makeText(context, R.string.fav_already,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		final RelativeLayout jobsListLayout = (RelativeLayout) vacView
				.findViewById(R.id.jobs_list_item_layout);
		jobsListLayout.setFocusable(false);
		jobsListLayout.setFocusableInTouchMode(false);

		jobsListLayout.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				View root = (View) v.getParent();
				holder.hiddenLayout = (RelativeLayout) root
						.findViewById(R.id.hiddenLayout);
				vacNames = new ArrayList<String>();
				for (int i = 0; i < vacancies.size(); i++) {
					vacNames.add(vacancies.get(i).getVacancyName());
				}

				if (holder.hiddenLayout.getVisibility() == View.GONE) {
					TextView text = (TextView) v.findViewById(R.id.vacName);
					openPos = vacNames.indexOf(text.getText().toString());

					RelativeLayout.LayoutParams cParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT, 0);

					cParams.addRule(RelativeLayout.BELOW, R.id.vacProviderImage);
					cParams.setMargins(margin, margin, margin, margin);

					for (int i = 0; i < parentView.getChildCount(); i++) {
						View child = parentView.getChildAt(i);
						RelativeLayout childhidLayout = (RelativeLayout) child
								.findViewById(R.id.hiddenLayout);
						if (childhidLayout.getVisibility() == View.VISIBLE
								&& i != openPos) {
							childhidLayout.setLayoutParams(cParams);
							childhidLayout.setVisibility(View.GONE);
						}
					}

					RelativeLayout.LayoutParams jllParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					jllParams.addRule(RelativeLayout.BELOW,
							R.id.vacProviderImage);
					jllParams.setMargins(margin, margin, margin, margin);

					final LinearLayout btnLayout = (LinearLayout) holder.hiddenLayout
							.findViewById(R.id.mailCallBtnLayout);
					final RelativeLayout.LayoutParams blParams = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, defHeight);
					blParams.addRule(RelativeLayout.BELOW,
							R.id.hiddenVacDescription);
					btnLayout.setLayoutParams(blParams);

					AbsListView.LayoutParams rootParams = new AbsListView.LayoutParams(
							AbsListView.LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					holder.hiddenLayout.setVisibility(View.VISIBLE);
					root.setLayoutParams(rootParams);
					holder.hiddenLayout.setLayoutParams(jllParams);
					listView.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);

				}

				else {
					RelativeLayout.LayoutParams jllParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT, 0);
					AbsListView.LayoutParams rootParams = new AbsListView.LayoutParams(
							AbsListView.LayoutParams.MATCH_PARENT, defHeight);
					jllParams.addRule(RelativeLayout.BELOW,
							R.id.vacProviderImage);
					// jllParams.setMargins(margin, margin, margin, margin);
					root.setLayoutParams(rootParams);
					holder.hiddenLayout.setVisibility(View.GONE);
					holder.hiddenLayout.setLayoutParams(jllParams);
					openPos = -1;
					listView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
				}

				return true;
			}
		});

		holder.vacProvImg = (ImageView) vacView
				.findViewById(R.id.vacProviderImage);
		RelativeLayout.LayoutParams vciParams = new RelativeLayout.LayoutParams(
				defHeight + 10, defHeight);
		holder.vacProvImg.setLayoutParams(vciParams);

		new AsyncTask<JobsListViewHolder, Void, Bitmap>() {
			private JobsListViewHolder holder;

			@Override
			protected Bitmap doInBackground(JobsListViewHolder... params) {
				holder = params[0];
				return loadedBitmap;
			};

			@Override
			protected void onPostExecute(Bitmap result) {
				if (holder.position == arg0) {
					holder.vacProvImg.setImageBitmap(result);

				}
			}
		}.execute(holder);

		holder.vacName = (TextView) vacView.findViewById(R.id.vacName);
		holder.vacSalary = (TextView) vacView.findViewById(R.id.salName);
		holder.vacDate = (TextView) vacView.findViewById(R.id.dateName);
		holder.vacCity = (TextView) vacView.findViewById(R.id.cityName);

		holder.vacName.setText(getItem(arg0).getVacancyName());
		holder.vacSalary.setText("Зарплата: "
				+ getItem(arg0).getVacancySalary());
		holder.vacCity.setText("Город: " + getItem(arg0).getVacCity());
		DateStringProcess dateStringProcess = new DateStringProcess(getItem(
				arg0).getVacancyDate());
		Log.d("date", dateStringProcess.toString());
		holder.vacDate.setText("Опубликовано: "
				+ dateStringProcess.getIntervalStr());

		Log.d("vac_mail", getItem(arg0).getVacancyMail());
		ImageButton callBtn = (ImageButton) holder.hiddenLayout
				.findViewById(R.id.vacCallBtn);
		ImageButton mailBtn = (ImageButton) holder.hiddenLayout
				.findViewById(R.id.vacMailBtn);

		if (getItem(arg0).getVacancyTel().equals("")) {

			callBtn.setEnabled(false);
			callBtn.setVisibility(View.GONE);
		}
		if (getItem(arg0).getVacancyMail().equals("")) {

			mailBtn.setEnabled(false);
			mailBtn.setVisibility(View.GONE);
		}

		holder.mailCallBtnLayout = (LinearLayout) vacView
				.findViewById(R.id.mailCallBtnLayout);
		RelativeLayout.LayoutParams mcblp;

		if (callBtn.getVisibility() == View.GONE
				&& mailBtn.getVisibility() == View.GONE) {
			Log.d("all_gone",
					String.valueOf(callBtn.getVisibility() == View.GONE)
							+ " "
							+ String.valueOf(mailBtn.getVisibility() == View.GONE));
			mcblp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 0);
			mcblp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mcblp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			holder.mailCallBtnLayout.setLayoutParams(mcblp);
		} else {
			mcblp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, this.mcbHeight);
			mcblp.addRule(RelativeLayout.BELOW, R.id.hiddenVacDescription);
			mcblp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			holder.mailCallBtnLayout.setLayoutParams(mcblp);
		}

		vacView.setTag(holder);

		return vacView;
	}

	static class JobsListViewHolder {
		int position;
		ImageView vacProvImg;
		TextView vacName;
		TextView vacSalary;
		TextView vacDate;
		TextView vacCity;
		RelativeLayout hiddenLayout;
		RelativeLayout itemLayout;
		LinearLayout mailCallBtnLayout;
	}

}
