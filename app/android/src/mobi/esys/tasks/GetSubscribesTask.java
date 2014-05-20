package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.content.Context;
import android.os.AsyncTask;

public class GetSubscribesTask extends AsyncTask<Void, Void, Void> {
	private transient JHRequest jhRequest;

	public GetSubscribesTask(Context context) {
		jhRequest = new JHRequest(context);
	}

	@Override
	protected Void doInBackground(Void... params) {
		jhRequest.getSubscribes();
		return null;
	}

}
