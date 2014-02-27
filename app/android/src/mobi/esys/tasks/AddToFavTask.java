package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;

public class AddToFavTask extends AsyncTask<Bundle, Void, JSONObject> {
	private transient JHRequest jhRequest;

	public AddToFavTask() {
		jhRequest = new JHRequest();
	}

	@Override
	protected JSONObject doInBackground(Bundle... params) {
		return jhRequest.addToFav(params[0].getString("userID"),
				params[0].getString("apiKey"), params[0].getString("site"),
				params[0].getString("siteID"));

	}
}
