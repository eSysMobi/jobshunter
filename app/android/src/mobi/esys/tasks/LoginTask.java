package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.os.AsyncTask;
import android.os.Bundle;

public class LoginTask extends AsyncTask<Bundle, Void, Bundle> {
	private transient JHRequest jhRequest;

	public LoginTask() {
		jhRequest = new JHRequest();
	}

	@Override
	protected Bundle doInBackground(Bundle... params) {
		return jhRequest.login(params[0].getString("snetwork"),
				params[0].getString("nID"));
	}

}
