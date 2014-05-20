package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.content.Context;
import android.os.AsyncTask;

public class GetVKUserInfo extends AsyncTask<String, Void, Void> {
	private transient JHRequest jhRequest;

	public GetVKUserInfo(Context context) {
		jhRequest = new JHRequest(context);
	}

	@Override
	protected Void doInBackground(String... params) {
		jhRequest.getVKUser(params[0]);
		return null;
	}

}
