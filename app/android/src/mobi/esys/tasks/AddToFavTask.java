package mobi.esys.tasks;

import mobi.esys.constants.Constants;
import mobi.esys.server_side.JHRequest;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class AddToFavTask extends AsyncTask<Bundle, Void, JSONObject> {
	private transient JHRequest jhRequest;

	public AddToFavTask(Context context) {
		jhRequest = new JHRequest(context);
	}

	@Override
	protected JSONObject doInBackground(Bundle... params) {
		return jhRequest.addToFav(params[0].getString(Constants.USER_ID),
				params[0].getString(Constants.API_KEY),
				params[0].getString("site"), params[0].getString("siteID"));

	}
}
