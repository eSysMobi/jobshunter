package mobi.esys.jobshunter;

import java.util.concurrent.ExecutionException;

import mobi.esys.tasks.GetFavTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	private transient boolean isFirst = true;

	private transient double lat;
	private transient double lon;

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria myCriteria = new Criteria();
		myCriteria.setPowerRequirement(Criteria.POWER_LOW);

		lm.requestLocationUpdates(lm.getBestProvider(myCriteria, true), 0, 0,
				mLocationListener);

		ImageButton findBtn = (ImageButton) findViewById(R.id.findBtn);
		ImageButton goToFavBtn = (ImageButton) findViewById(R.id.goToFavBtn);

		findBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						ConditionsActivity.class));

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
									favTask.get()).putExtra("isFav", true));
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
			}
		});

	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	private final LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			setLon(location.getLongitude());
			setLat(location.getLatitude());

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	};

}
