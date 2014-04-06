package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.content.Context;
import android.os.AsyncTask;

public class RegGCMTask extends AsyncTask<String, Void, Void> {
	private transient JHRequest jhRequest;

	public RegGCMTask(Context context) {
		jhRequest = new JHRequest(context);
	}

	@Override
	protected Void doInBackground(String... params) {
		jhRequest.addGCMNotifications(params[0]);
		return null;
	}

}
