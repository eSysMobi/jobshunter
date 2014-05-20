package mobi.esys.jobshunter;

import mobi.esys.constants.Constants;
import mobi.esys.fragments.ConditionFragment;
import mobi.esys.fragments.JobsFragment;
import mobi.esys.fragments.MenuFragment;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {
	private transient boolean isFirst = true;

	private transient Fragment conditionFragment;

	private transient ListFragment menuFragment;

	private transient ImageView menuBtn;
	private transient ImageView filterBtn;

	private transient TextView fragmentLabel;

	private transient boolean filterFlag;

	private transient RelativeLayout filterLayout;

	private transient Button salSortBtn;
	private transient Button dateSortBtn;
	private transient Button rangeSortBtn;

	private transient int width;

	private transient View menuClickView;
	private transient View filterClickView;

	private transient SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_fragment_layout);
		setBehindContentView(R.layout.slidingmenu_layout);

		filterFlag = false;

		filterClickView = (View) findViewById(R.id.filterClickView);
		menuClickView = (View) findViewById(R.id.menuClickView);

		preferences = getSharedPreferences(Constants.PREF_STRING, MODE_PRIVATE);

		width = preferences.getInt("screenWidth", 0);
		int height = preferences.getInt("screenHeight", 0);

		RelativeLayout.LayoutParams dsblp = new RelativeLayout.LayoutParams(
				width / 3, RelativeLayout.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams rsblp = new RelativeLayout.LayoutParams(
				width / 3, RelativeLayout.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams ssblp = new RelativeLayout.LayoutParams(
				width / 3, RelativeLayout.LayoutParams.MATCH_PARENT);

		RelativeLayout.LayoutParams fblp = new RelativeLayout.LayoutParams(
				width / 10, height / 10 - 20);
		RelativeLayout.LayoutParams mblp = new RelativeLayout.LayoutParams(
				width / 10, height / 10 - 20);

		RelativeLayout.LayoutParams mcvlp = new RelativeLayout.LayoutParams(
				width / 36 + width / 10,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		mcvlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		menuClickView.setLayoutParams(mcvlp);

		RelativeLayout.LayoutParams fcvlp = new RelativeLayout.LayoutParams(
				width / 36 + width / 10,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		fcvlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		filterClickView.setLayoutParams(fcvlp);

		mblp.addRule(RelativeLayout.CENTER_VERTICAL);
		mblp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mblp.setMargins(width / 72, width / 72, width / 72, width / 72);

		fblp.addRule(RelativeLayout.CENTER_VERTICAL);
		fblp.setMargins(width / 72, width / 72, width / 72, width / 72);
		fblp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		dsblp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		rsblp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		ssblp.addRule(RelativeLayout.CENTER_HORIZONTAL);

		menuBtn = (ImageView) findViewById(R.id.menuBtn);
		menuBtn.setLayoutParams(mblp);

		filterBtn = (ImageView) findViewById(R.id.filterBtn);
		filterBtn.setLayoutParams(fblp);

		filterLayout = (RelativeLayout) findViewById(R.id.filterLayout);

		dateSortBtn = (Button) findViewById(R.id.dateSortBtn);
		dateSortBtn.setLayoutParams(dsblp);
		dateSortBtn.setEnabled(false);

		salSortBtn = (Button) findViewById(R.id.salSortBtn);
		salSortBtn.setLayoutParams(ssblp);

		dateSortBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (filterBtn.getVisibility() == View.VISIBLE) {
					jobsSort("");
					salSortBtn.setEnabled(true);
					dateSortBtn.setEnabled(false);
				}
			}
		});

		salSortBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (filterBtn.getVisibility() == View.VISIBLE) {
					jobsSort("sum");
					salSortBtn.setEnabled(false);
					dateSortBtn.setEnabled(true);
				}
			}

		});

		rangeSortBtn = (Button) findViewById(R.id.rangeSortBtn);
		rangeSortBtn.setLayoutParams(rsblp);
		rangeSortBtn.setEnabled(false);

		filterClickView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (filterFlag) {
					LinearLayout.LayoutParams flP = new LinearLayout.LayoutParams(
							0, 0);
					filterLayout.setLayoutParams(flP);
					filterFlag = false;
				} else {
					LinearLayout.LayoutParams flP = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.1f);
					filterLayout.setLayoutParams(flP);
					filterFlag = true;
				}

			}
		});

		fragmentLabel = (TextView) findViewById(R.id.fragmentText);
		fragmentLabel.setText("Поиск вакансий");

		conditionFragment = new ConditionFragment();
		menuFragment = new MenuFragment();

		initSlidingMenu();

		menuClickView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSlidingMenu().showMenu();
			}
		});

		hideFilter();

		getFragmentManager().beginTransaction()
				.replace(R.id.frmCont, conditionFragment).commit();

	}

	private void initSlidingMenu() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setBehindOffset(width / 2);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		getFragmentManager().beginTransaction()
				.replace(R.id.menuCont, menuFragment, "menuFrag").commit();
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public void setFragmentLabel(String label) {
		fragmentLabel.setText(label);
	}

	public String getFragmentLabel() {
		return fragmentLabel.getText().toString();
	}

	public void showFilter() {
		filterClickView.setEnabled(true);
		RelativeLayout.LayoutParams fP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		fP.addRule(RelativeLayout.CENTER_VERTICAL);
		fP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		filterBtn.setLayoutParams(fP);
	}

	public void hideFilter() {
		filterClickView.setEnabled(false);
		RelativeLayout.LayoutParams fP = new RelativeLayout.LayoutParams(0, 0);
		fP.addRule(RelativeLayout.CENTER_VERTICAL);
		fP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		filterBtn.setLayoutParams(fP);
	}

	private void jobsSort(String sort) {
		Fragment fragment = getFragmentManager().findFragmentByTag("jobsTag");
		if (fragment instanceof JobsFragment) {
			((JobsFragment) fragment).getVac(sort);
		}
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			getSlidingMenu().toggle();
			return true;
		}

		return super.onKeyDown(keycode, e);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}
