package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class LoginTask extends AsyncTask<Bundle, Void, Bundle> {
	private transient JHRequest jhRequest;

	public LoginTask(Context context) {
		jhRequest = new JHRequest(context);
	}

	@Override
	protected Bundle doInBackground(Bundle... params) {
		return jhRequest.login(params[0].getString("snetwork"),
				params[0].getString("nID"));
	}

}
