package mobi.esys.tasks;

import java.util.ArrayList;

import mobi.esys.server_side.JHRequest;
import mobi.esys.specific_data_type.Vacancy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class GetFavTask extends AsyncTask<Void, Void, ArrayList<Vacancy>> {
	private transient JHRequest jhRequest;
	private transient Context context;

	public GetFavTask(Context context) {
		jhRequest = new JHRequest();
		this.context = context;
	}

	@Override
	protected ArrayList<Vacancy> doInBackground(Void... params) {
		ArrayList<Vacancy> favVacancies = new ArrayList<Vacancy>();
		SharedPreferences preferences = context.getSharedPreferences("JHPref",
				Context.MODE_PRIVATE);

		JSONArray array = jhRequest.getFav(preferences.getString("userID", ""),
				preferences.getString("apiKey", ""));
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject currFavObject = array.getJSONObject(i);
				String salary = "";
				String desc = "";
				if (currFavObject.has("salary_from")) {
					if (currFavObject.getString("salary_from") != null
							&& currFavObject.getString("salary_from") != null) {
						salary = currFavObject.getString("salary_from") + " - "
								+ currFavObject.getString("salary_to");
					} else if (currFavObject.getString("salary_from").equals(
							"null")
							|| currFavObject.getString("salary_from").equals(
									"0")) {
						salary = "до " + currFavObject.getString("salary_to");
					}

					else if (currFavObject.getString("salary_to")
							.equals("null")
							|| currFavObject.getString("salary_to").equals("0")) {
						salary = "от " + currFavObject.getString("salary_from");
					}
				}

				if (currFavObject.has("description")) {
					desc = currFavObject.getString("description");
				} else {
					desc = "Нет описания";
				}

				favVacancies.add(new Vacancy(
						currFavObject.getString("site_id"), currFavObject
								.getString("site"), currFavObject
								.getString("name"), "", "", "", salary
								+ " руб.", desc, currFavObject.getString("id"),
						"true"));
			}

		} catch (JSONException e) {
		}

		return favVacancies;
	}
}
