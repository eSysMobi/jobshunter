package mobi.esys.tasks;

import java.util.List;

import mobi.esys.server_side.JHRequest;
import mobi.esys.specific_data_type.SuperCategory;
import android.content.Context;
import android.os.AsyncTask;

public class GetSupCatsTask extends
		AsyncTask<String, Void, List<SuperCategory>> {
	private transient JHRequest jhRequest;

	public GetSupCatsTask(Context context) {
		jhRequest = new JHRequest(context);
	}

	@Override
	protected List<SuperCategory> doInBackground(String... params) {
		return jhRequest.JSONCatsToObjectCats(params[0]);
	}

}
