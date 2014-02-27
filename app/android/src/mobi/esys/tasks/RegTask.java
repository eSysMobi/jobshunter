package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.os.AsyncTask;
import android.os.Bundle;

public class RegTask extends AsyncTask<Bundle, Void, Bundle> {
	JHRequest jhRequest;

	public RegTask() {
		jhRequest = new JHRequest();
	}

	@Override
	protected Bundle doInBackground(Bundle... params) {
		return jhRequest.reg(params[0].getString("fname"),
				params[0].getString("lname"), params[0].getString("snetwork"),
				params[0].getString("nID"));

	}
}
