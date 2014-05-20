package mobi.esys.tasks;

import java.util.List;

import mobi.esys.server_side.JHRequest;
import android.content.Context;
import android.os.AsyncTask;

public class GetCityNameTask extends AsyncTask<String[], Void, List<String>> {
	private transient JHRequest jhRequest;

	public GetCityNameTask(Context context) {
		this.jhRequest = new JHRequest(context);
	}

	@Override
	protected List<String> doInBackground(String[]... params) {
		return jhRequest.getCityNameByID(params[0]);
	}

}
