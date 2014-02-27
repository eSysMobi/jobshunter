package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.os.AsyncTask;
import android.os.Bundle;

public class DeleteFavTask extends AsyncTask<Bundle, Void, Void> {
	private transient JHRequest jhRequest;

	public DeleteFavTask() {
		jhRequest = new JHRequest();
	}

	@Override
	protected Void doInBackground(Bundle... params) {
		jhRequest.deleteFromFav(params[0].getString("userID"),
				params[0].getString("apiKey"), params[0].getString("favID"));
		return null;
	}

}