package mobi.esys.tasks;

import java.util.ArrayList;

import mobi.esys.constants.Constants;
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

	private static final String SALARY_TO = "salary_to";
	private static final String SALARY_FROM = "salary_from";

	public GetFavTask(Context context) {
		jhRequest = new JHRequest(context);
		this.context = context;
	}

	@Override
	protected ArrayList<Vacancy> doInBackground(Void... params) {
		ArrayList<Vacancy> favVacancies = new ArrayList<Vacancy>();
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.PREF_STRING, Context.MODE_PRIVATE);

		JSONArray array = jhRequest.getFav(
				preferences.getString(Constants.USER_ID, ""),
				preferences.getString(Constants.API_KEY, ""));
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject currFavObject = array.getJSONObject(i);
				String salary = "";
				String desc = "";
				if (currFavObject.has(SALARY_FROM)) {
					if (currFavObject.getString(SALARY_FROM) != null
							&& currFavObject.getString(SALARY_FROM) != null) {
						salary = currFavObject.getString(SALARY_FROM) + " - "
								+ currFavObject.getString(SALARY_TO);
					} else if (currFavObject.getString(SALARY_FROM).equals(
							"null")
							|| currFavObject.getString(SALARY_FROM).equals("0")) {
						salary = "до " + currFavObject.getString(SALARY_TO);
					}

					else if (currFavObject.getString(SALARY_TO).equals("null")
							|| currFavObject.getString(SALARY_TO).equals("0")) {
						salary = "от " + currFavObject.getString(SALARY_FROM);
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
								.getString("name"), currFavObject
								.getString("creation_date"), "", "", "", salary
								+ " руб.", desc, currFavObject.getString("id"),
						"true", currFavObject.getString("city")));
			}

		} catch (JSONException e) {
		}

		return favVacancies;
	}
}
