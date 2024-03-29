package mobi.esys.tasks;

import mobi.esys.constants.Constants;
import mobi.esys.server_side.JHRequest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class DeleteFavTask extends AsyncTask<Bundle, Void, Void> {
	private transient JHRequest jhRequest;

	public DeleteFavTask(Context context) {
		jhRequest = new JHRequest(context);
	}

	@Override
	protected Void doInBackground(Bundle... params) {
		jhRequest.deleteFromFav(params[0].getString(Constants.USER_ID),
				params[0].getString(Constants.API_KEY),
				params[0].getString("favID"));
		return null;
	}

}
