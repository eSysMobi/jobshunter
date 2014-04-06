package mobi.esys.tasks;

import java.util.ArrayList;

import mobi.esys.server_side.JHRequest;
import mobi.esys.specific_data_type.Vacancy;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class GetVacTask extends AsyncTask<Bundle, Void, ArrayList<Vacancy>> {
	private transient JHRequest jhRequest;
	private transient Context context;

	public GetVacTask(Context context) {
		jhRequest = new JHRequest(context);
		this.context = context;
	}

	@Override
	protected ArrayList<Vacancy> doInBackground(Bundle... params) {
		return jhRequest.getVacancies(params[0].getString("params"),
				params[0].getString("page"));

	}

	@Override
	protected void onPostExecute(ArrayList<Vacancy> result) {
		super.onPostExecute(result);
	}

}
