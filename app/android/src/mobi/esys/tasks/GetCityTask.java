package mobi.esys.tasks;

import java.util.List;

import mobi.esys.server_side.JHRequest;
import mobi.esys.specific_data_type.City;
import android.content.Context;
import android.os.AsyncTask;

public class GetCityTask extends AsyncTask<String, Void, List<City>> {
	private transient JHRequest jhRequest;

	public GetCityTask(Context context) {
		jhRequest = new JHRequest(context);
	}

	@Override
	protected List<City> doInBackground(String... params) {
		return jhRequest.getCities(params[0]);
	}

}
