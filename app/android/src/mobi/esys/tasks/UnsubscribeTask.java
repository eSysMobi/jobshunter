package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.content.Context;
import android.os.AsyncTask;

public class UnsubscribeTask extends AsyncTask<String, Void, Void> {
	private transient JHRequest request;

	public UnsubscribeTask(Context context) {
		request = new JHRequest(context);
	}

	@Override
	protected Void doInBackground(String... params) {
		request.unsubscribe(params[0]);
		return null;
	}

}
