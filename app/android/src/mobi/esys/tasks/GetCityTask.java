package mobi.esys.tasks;

import java.util.List;

import mobi.esys.server_side.JHRequest;
import mobi.esys.specific_data_type.City;
import android.os.AsyncTask;

public class GetCityTask extends AsyncTask<String, Void, List<City>> {
	private transient JHRequest jhRequest;

	public GetCityTask() {
		jhRequest = new JHRequest();
	}

	@Override
	protected List<City> doInBackground(String... params) {
		return jhRequest.getCities(params[0]);
	}

}
