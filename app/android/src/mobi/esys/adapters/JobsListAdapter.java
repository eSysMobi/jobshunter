package mobi.esys.adapters;

import java.io.IOException;
import java.util.List;

import mobi.esys.jobshunter.JobsListActivity;
import mobi.esys.jobshunter.R;
import mobi.esys.specific_data_type.Vacancy;
import mobi.esys.tasks.DeleteFavTask;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class JobsListAdapter extends ArrayAdapter<Vacancy> {
	private transient List<Vacancy> vacancies;
	private transient final LayoutInflater inflater;
	private transient Context context;
	private transient int layout;
	private transient int defHeight;
	// private transient int margin;
	// private transient List<String> vacNames;
	private transient ListView parentView;
	private transient int openPos = -1;
	private transient boolean isFav = false;
	private transient JobsListViewHolder holder;
	private transient Bitmap loadedBitmap;

	public JobsListAdapter(List<Vacancy> vacancies, int layout,
			Context context, int height, boolean isFav) {
		super(context, layout, vacancies);
		this.layout = layout;
		this.vacancies = vacancies;
		this.isFav = isFav;
		this.loadedBitmap = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.ic_launcher);
		this.context = context;
		this.defHeight = height / 10;
		// this.margin = height / 64;
		this.inflater = ((LayoutInflater) context.getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

	}

	@Override
	public int getCount() {
		return vacancies.size();

	}

	@Override
	public Vacancy getItem(int arg0) {
		return vacancies.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return vacancies.get(arg0).hashCode();
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		View vacView = arg1;

		if (vacView == null) {
			vacView = this.inflater.inflate(layout, arg2, false);
		}
		vacView.setFocusable(true);
		vacView.setFocusableInTouchMode(true);

		parentView = (ListView) arg2;
		parentView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem > openPos) {
					openPos = -1;
					((JobsListActivity) context).unlockSwipe();
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

		holder.itemLayout = (LinearLayout) vacView
				.findViewById(R.id.jobsListItem);
		AbsListView.LayoutParams iParams = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT, defHeight);

		holder.itemLayout.setLayoutParams(iParams);

		TextView vacDesc = (TextView) vacView
				.findViewById(R.id.hiddenVacDescription);

		vacDesc.setText(getItem(arg0).getVacancyDescription());

		ImageButton delVacButton = (ImageButton) vacView
				.findViewById(R.id.vacDelBtn);
		delVacButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isFav) {
					DeleteFavTask deleteFavTask = new DeleteFavTask();
					Bundle deleteParams = new Bundle();
					SharedPreferences preferences = context
							.getSharedPreferences("JHPref",
									Context.MODE_PRIVATE);
					deleteParams.putString("userID",
							preferences.getString("userID", ""));
					deleteParams.putString("apiKey",
							preferences.getString("apiKey", ""));
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
			public void onClick(View v) {
				((JobsListActivity) context).addVac2Fav(getItem(arg0));

			}
		});

		LinearLayout jobsListLayout = (LinearLayout) vacView
				.findViewById(R.id.jobs_list_item_layout);
		jobsListLayout.setFocusable(false);
		jobsListLayout.setFocusableInTouchMode(false);

		// jobsListLayout.setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// // View root = (View) v.getParent();
		// // holder.hiddenLayout = (RelativeLayout) root
		// // .findViewById(R.id.hiddenLayout);
		// // vacNames = new ArrayList<String>();
		// for (int i = 0; i < vacancies.size(); i++) {
		// vacNames.add(vacancies.get(i).getVacancyName());
		// }
		//
		// if (holder.hiddenLayout.getVisibility() == View.GONE) {
		// TextView text = (TextView) v.findViewById(R.id.vacName);
		// openPos = vacNames.indexOf(text.getText().toString());
		//
		// RelativeLayout.LayoutParams cParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.MATCH_PARENT, 0);
		//
		// cParams.addRule(RelativeLayout.BELOW, R.id.vacProviderImage);
		// cParams.setMargins(margin, margin, margin, margin);
		//
		// for (int i = 0; i < parentView.getChildCount(); i++) {
		// View child = parentView.getChildAt(i);
		// RelativeLayout childhidLayout = (RelativeLayout) child
		// .findViewById(R.id.hiddenLayout);
		// if (childhidLayout.getVisibility() == View.VISIBLE
		// && i != openPos) {
		// childhidLayout.setLayoutParams(cParams);
		// childhidLayout.setVisibility(View.GONE);
		// }
		// }
		//
		// RelativeLayout.LayoutParams jllParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT);
		// jllParams.addRule(RelativeLayout.BELOW,
		// R.id.vacProviderImage);
		// jllParams.setMargins(margin, margin, margin, margin);
		//
		// LinearLayout btnLayout = (LinearLayout) holder.hiddenLayout
		// .findViewById(R.id.mailCallBtnLayout);
		// RelativeLayout.LayoutParams blParams = new
		// RelativeLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, defHeight);
		// blParams.addRule(RelativeLayout.BELOW,
		// R.id.hiddenVacDescription);
		// btnLayout.setLayoutParams(blParams);
		//
		// AbsListView.LayoutParams rootParams = new AbsListView.LayoutParams(
		// AbsListView.LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT);
		// holder.hiddenLayout.setVisibility(View.VISIBLE);
		// root.setLayoutParams(rootParams);
		// holder.hiddenLayout.setLayoutParams(jllParams);
		// ((JobsListActivity) context).lockSwipe();
		//
		// }
		//
		// else {
		// RelativeLayout.LayoutParams jllParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.MATCH_PARENT, 0);
		// AbsListView.LayoutParams rootParams = new AbsListView.LayoutParams(
		// AbsListView.LayoutParams.MATCH_PARENT, defHeight);
		// jllParams.addRule(RelativeLayout.BELOW,
		// R.id.vacProviderImage);
		// jllParams.setMargins(margin, margin, margin, margin);
		// root.setLayoutParams(rootParams);
		// holder.hiddenLayout.setVisibility(View.GONE);
		// holder.hiddenLayout.setLayoutParams(jllParams);
		// openPos = -1;
		// ((JobsListActivity) context).unlockSwipe();
		// }
		//
		// return true;
		// }
		// });

		holder.vacProvImg = (ImageView) vacView
				.findViewById(R.id.vacProviderImage);
		LinearLayout.LayoutParams vciParams = new LinearLayout.LayoutParams(
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
		holder.vacName.setText(String.valueOf(arg0 + 1) + " "
				+ getItem(arg0).getVacancyName());
		holder.vacSalary.setText(getItem(arg0).getVacancySalary());
		vacView.setTag(holder);
		return vacView;
	}

	static class JobsListViewHolder {
		int position;
		ImageView vacProvImg;
		TextView vacName;
		TextView vacSalary;
		LinearLayout itemLayout;
	}

}
