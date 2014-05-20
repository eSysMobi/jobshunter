package mobi.esys.tasks;

import java.util.ArrayList;

import mobi.esys.fragments.ConditionFragment;
import mobi.esys.server_side.JHRequest;
import mobi.esys.specific_data_type.Vacancy;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class GetVacTask extends AsyncTask<Bundle, Void, ArrayList<Vacancy>> {
	private transient JHRequest jhRequest;
	private transient Fragment fragment;

	public GetVacTask(Context context, Fragment fragment) {
		jhRequest = new JHRequest(context);
		this.fragment = fragment;
	}

	@Override
	protected ArrayList<Vacancy> doInBackground(Bundle... params) {
		return jhRequest.getVacancies(params[0].getString("params"),
				params[0].getString("page"), params[0].getString("sort"));

	}

	@Override
	protected void onPostExecute(ArrayList<Vacancy> result) {
		super.onPostExecute(result);
		if (fragment instanceof ConditionFragment) {
			((ConditionFragment) fragment).dismissDialog();
		}
	}

}
