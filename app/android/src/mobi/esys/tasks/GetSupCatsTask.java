package mobi.esys.tasks;

import java.util.List;

import mobi.esys.server_side.JHRequest;
import mobi.esys.specific_data_type.SuperCategory;
import android.os.AsyncTask;

public class GetSupCatsTask extends AsyncTask<Void, Void, List<SuperCategory>> {
	private transient JHRequest jhRequest;

	public GetSupCatsTask() {
		jhRequest = new JHRequest();
	}

	@Override
	protected List<SuperCategory> doInBackground(Void... params) {
		return jhRequest.getCats();
	}

}
