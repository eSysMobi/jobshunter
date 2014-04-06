package mobi.esys.jobshunter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mobi.esys.server_side.JHRequest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	private transient Intent nextActivityIntent;
	private transient SharedPreferences preferences;

	@Override
	protected void onCreate(final Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_splash);

		defineNextActivity();

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		int width = size.x;

		final SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("screenWidth", width);
		editor.putInt("screenHeight", height);
		editor.commit();

		final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		final Criteria myCriteria = new Criteria();
		myCriteria.setPowerRequirement(Criteria.POWER_LOW);

		locationManager.requestLocationUpdates(
				locationManager.getBestProvider(myCriteria, true), 0, 0,
				new LocationListener() {
					@Override
					public void onLocationChanged(final Location location) {
						final SharedPreferences.Editor editor = preferences
								.edit();
						editor.putString(
								"cityName",
								getLocationName(location.getLatitude(),
										location.getLongitude()));
						editor.commit();
					}

					@Override
					public void onProviderDisabled(final String provider) {

					}

					@Override
					public void onProviderEnabled(final String provider) {

					}

					@Override
					public void onStatusChanged(final String provider,
							final int status, final Bundle extras) {
					}
				});

		new AsyncTask<Void, Void, Void>() {
			Bundle splashBundle = new Bundle();
			JHRequest jhRequest = new JHRequest(SplashActivity.this);

			@Override
			protected Void doInBackground(Void... params) {
				splashBundle.putString("cats", jhRequest.getCats().toString());
				jhRequest.getWorkTypes();
				jhRequest.getSubscribes();
				return null;
			}

			protected void onPostExecute(final Void result) {
				startActivity(nextActivityIntent.putExtra("splashBundle",
						splashBundle));
			};
		}.execute();

	}

	public void defineNextActivity() {
		preferences = getSharedPreferences("JHPref", MODE_PRIVATE);
		String uID = preferences.getString("userID", "");
		String apiKey = preferences.getString("apiKey", "");
		if (uID.isEmpty() && apiKey.isEmpty()) {
			nextActivityIntent = new Intent(SplashActivity.this,
					LoginActivity.class);
		} else {
			nextActivityIntent = new Intent(SplashActivity.this,
					MainActivity.class);
		}

	}

	public String getLocationName(final double lattitude, final double longitude) {

		String cityName = "Not Found";
		final Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
		try {

			final List<Address> addresses = gcd.getFromLocation(lattitude,
					longitude, 10);

			for (Address adrs : addresses) {
				if (adrs != null) {

					final String city = adrs.getLocality();
					if (city != null && !city.equals("")) {
						cityName = city;

					} else {

					}
					// // you should also try with addresses.get(0).toSring();

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cityName;

	}
}
