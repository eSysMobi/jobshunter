package mobi.esys.tasks;

import mobi.esys.server_side.JHRequest;
import android.content.Context;
import android.os.AsyncTask;

public class GetWorksTypesTask extends AsyncTask<Void, Void, Void> {
	private transient JHRequest request;

	public GetWorksTypesTask(Context context) {
		request = new JHRequest(context);
	}

	@Override
	protected Void doInBackground(Void... params) {
		request.getWorkTypes();
		return null;
	}

}
