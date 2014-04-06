package mobi.esys.jobshunter;

import java.util.concurrent.ExecutionException;

import mobi.esys.tasks.GetFavTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

public class MainActivity extends SlidingActivity {
	private transient boolean isFirst = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.slidingmenu_layout);

		initSlidingMenu();

		ImageButton findBtn = (ImageButton) findViewById(R.id.findBtn);
		ImageButton goToFavBtn = (ImageButton) findViewById(R.id.goToFavBtn);
		ImageButton cvBtn = (ImageButton) findViewById(R.id.cvBtn);
		ImageButton subBtn = (ImageButton) findViewById(R.id.subBtn);

		findBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						ConditionsActivity.class).putExtra("splashBundle",
						getIntent().getExtras().getBundle("splashBundle")));

			}
		});

		goToFavBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GetFavTask favTask = new GetFavTask(MainActivity.this);
				favTask.execute();
				try {
					startActivity(new Intent(MainActivity.this,
							JobsListActivity.class)
							.putParcelableArrayListExtra("jobsList",
									favTask.get())
							.putExtra("isFav", true)
							.putExtra(
									"splashBundle",
									getIntent().getExtras().getBundle(
											"splashBundle")));

				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
			}
		});

		cvBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, CVActivity.class)
						.putExtra("splashBundle", getIntent().getExtras()
								.getBundle("splashBundle")));
			}
		});

		subBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SubsActivity.class)
						.putExtra("splashBundle", getIntent().getExtras()
								.getBundle("splashBundle")));
			}
		});

	}

	private void initSlidingMenu() {
		final SlidingMenu slidingMenu = getSlidingMenu();

		slidingMenu.setFadeDegree(0.85f);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

}
