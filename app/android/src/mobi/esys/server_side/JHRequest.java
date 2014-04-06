package mobi.esys.server_side;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobi.esys.networking.Request;
import mobi.esys.specific_data_type.Category;
import mobi.esys.specific_data_type.City;
import mobi.esys.specific_data_type.SuperCategory;
import mobi.esys.specific_data_type.Vacancy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class JHRequest {
	private transient Request request;
	private transient Context context;
	private transient SharedPreferences preferences;

	private static final String TAG = "subscr";

	public JHRequest(Context context) {
		this.request = new Request();
		this.context = context;
		this.preferences = context.getSharedPreferences("JHPref",
				Context.MODE_PRIVATE);
	}

	public JSONObject getSpec() {
		JSONObject jsonObject = new JSONObject();
		return jsonObject;
	}

	public Bundle reg(String fname, String lname, String snetwork, String nID) {
		String apiKey = "";
		String userID = "";
		Bundle apiBundle = new Bundle();
		JSONObject req = request
				.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/register_user/format/json?last_name="
						+ lname
						+ "&first_name="
						+ fname
						+ "&network="
						+ snetwork + "&network_id=" + nID);
		try {
			Log.d("req", req.toString());
			if (req.has("error")
					&& req.getString("error").equals("User exists")) {

				apiBundle = login(snetwork, nID);
			} else {
				apiKey = req.getString("apikey");
				userID = req.getString("id");
				apiBundle.putString("apiKey", apiKey);
				apiBundle.putString("userID", userID);
			}
		} catch (JSONException e) {
		}
		return apiBundle;
	}

	public Bundle login(String networkName, String nID) {
		String apiKey = "";
		String userID = "";
		Bundle apiBundle = new Bundle();
		JSONObject req = request
				.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/register_user/format/json?network="
						+ networkName + "&network_id=" + nID);
		try {
			apiKey = req.getString("apikey");
			userID = req.getString("id");
		} catch (JSONException e) {
		}
		apiBundle.putString("apiKey", apiKey);
		apiBundle.putString("userID", userID);
		return apiBundle;
	}

	public List<City> getCities(String findStr) {
		Log.d("city", "city task works");
		List<City> cities = new ArrayList<City>();
		Log.d("url",
				"http://jobshunter.mobi/app/index.php/api/cities/format/json?str="
						+ findStr);
		JSONObject cityObj = request
				.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/cities/format/json?str="
						+ findStr);
		try {
			JSONArray cityArray = cityObj.getJSONArray("cities");
			for (int i = 0; i < cityArray.length(); i++) {
				JSONObject currCity = cityArray.getJSONObject(i);
				cities.add(new City(currCity.getString("id"), currCity
						.getString("name")));
			}
		} catch (JSONException e) {
		}

		Log.d("city", cities.toString());
		return cities;
	}

	public List<SuperCategory> JSONCatsToObjectCats(String array) {
		List<SuperCategory> categories = new ArrayList<SuperCategory>();
		JSONArray catsArray = new JSONArray();
		try {
			catsArray = new JSONArray(array);
		} catch (JSONException e1) {
		}
		for (int i = 0; i < catsArray.length(); i++) {
			try {

				JSONObject currCat = catsArray.getJSONObject(i);
				if (currCat.getString("parent_id").equals("0")) {
					List<Category> subCategories = new ArrayList<Category>();

					for (int j = 0; j < catsArray.length(); j++) {

						JSONObject currSubCat = catsArray.getJSONObject(j);
						if (currSubCat.getString("parent_id").equals(
								currCat.getString("id")))
							subCategories.add(new Category(currSubCat
									.getString("name"), currSubCat
									.getString("id")));
					}

					categories.add(new SuperCategory(currCat.getString("name"),
							currCat.getString("id"), subCategories));
				}
			} catch (JSONException e) {
			}

		}
		Log.d("cats", categories.toString());
		return categories;
	}

	public JSONArray getCats() {
		Log.d("cats", "run getCats");
		JSONArray array = new JSONArray();
		JSONArray serverArray = request
				.doJSONArrayGetRequest("http://jobshunter.mobi/app/index.php/api/sitecats/format/json");
		if (context.getSharedPreferences("JHPref", Context.MODE_PRIVATE)
				.getString("cats", "").equals("")
				|| (!Arrays.asList(
						context.getSharedPreferences("JHPref",
								Context.MODE_PRIVATE).getString("cats", ""))
						.equals(serverArray.toString()) && !serverArray
						.toString().equals("[]"))) {
			array = serverArray;
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("cats", array.toString());
			editor.commit();
		} else {
			try {
				array = new JSONArray(context.getSharedPreferences("JHPref",
						Context.MODE_PRIVATE).getString("cats", ""));
			} catch (JSONException e) {
			}
		}
		Log.d("cats", array.toString());
		return array;

	}

	public ArrayList<Vacancy> getVacancies(String paramString, String pageNum) {
		ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();
		JSONObject vacObject = request
				.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/vacancies2/format/json?"
						+ paramString + "&page=" + pageNum);
		Log.d("url",
				"http://jobshunter.mobi/app/index.php/api/vacancies2/format/json?"
						+ paramString + "&page=" + pageNum);

		try {
			boolean sj_more = vacObject.getBoolean("sj_more");
			boolean hh_more = vacObject.getBoolean("hh_more");

			String morePage = "true";

			if (!sj_more && !hh_more) {
				morePage = "false";
			} else {
				morePage = "true";
			}

			JSONArray vacArray = vacObject.getJSONArray("items");

			for (int i = 0; i < vacArray.length(); i++) {

				JSONObject currVacObject = vacArray.getJSONObject(i);
				String salary = "";
				String desc = "";
				if (currVacObject.has("salary_from")) {
					if (currVacObject.getString("salary_from") != null
							&& currVacObject.getString("salary_from") != null) {
						salary = currVacObject.getString("salary_from") + " - "
								+ currVacObject.getString("salary_to");
					} else if (currVacObject.getString("salary_from").equals(
							"null")
							|| currVacObject.getString("salary_from").equals(
									"0")) {
						salary = "до " + currVacObject.getString("salary_to");
					}

					else if (currVacObject.getString("salary_to")
							.equals("null")
							|| currVacObject.getString("salary_to").equals("0")) {
						salary = "от " + currVacObject.getString("salary_from");
					}
				}

				if (currVacObject.has("description")) {
					desc = currVacObject.getString("description");
				} else {
					desc = "Нет описания";
				}

				vacancies.add(new Vacancy(currVacObject.getString("site_id"),
						currVacObject.getString("site"), currVacObject
								.getString("name"), "", "", "", salary
								+ " руб.", desc, "", morePage));

			}
			Log.d("vac", String.valueOf(vacancies.size()));

		} catch (JSONException e) {
		}
		Log.d("vac", String.valueOf(vacancies.size()));
		return vacancies;
	}

	public JSONObject addToFav(String userID, String apiKey, String site,
			String siteID) {
		JSONObject addFavObject = request
				.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/addbookmark/format/json?user_id="
						+ userID
						+ "&apikey="
						+ apiKey
						+ "&site="
						+ site
						+ "&site_id=" + siteID);
		return addFavObject;
	}

	public JSONArray getFav(String userID, String apiKey) {
		JSONArray fav = request
				.doJSONArrayGetRequest("http://jobshunter.mobi/app/index.php/api/bookmarks/format/json?user_id="
						+ userID + "&apikey=" + apiKey);
		return fav;
	}

	public JSONObject deleteFromFav(String userID, String apiKey, String favID) {
		JSONObject deleteFavObject = request
				.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/bookmarkdelete/format/json?user_id="
						+ userID
						+ "&apikey="
						+ apiKey
						+ "&bookmark_id="
						+ favID);
		return deleteFavObject;
	}

	public void addGCMNotifications(String GSMResID) {

		String apiKey = preferences.getString("apiKey", "");
		String userID = preferences.getString("userID", "");
		Log.d("par",
				preferences.getString("apiKey", "") + " "
						+ preferences.getString("userID", ""));
		if (!apiKey.isEmpty() && !userID.isEmpty()) {
			JSONObject res = request
					.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/add_gcmid/format/json?user_id="
							+ userID
							+ "&apikey="
							+ apiKey
							+ "&gcmid="
							+ GSMResID);
			Log.d("res", res.toString());
		}

	}

	public void getWorkTypes() {
		String workTypes = preferences.getString("workTypes", "");
		JSONArray workTypeArray = request
				.doJSONArrayGetRequest("http://jobshunter.mobi/app/index.php/api/worktypes/format/json");
		if (!workTypes.equals(workTypeArray.toString())) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("workTypes", workTypeArray.toString());
			editor.commit();
		}
	}

	public void subscribe(String subscrParams) {
		String apiKey = preferences.getString("apiKey", "");
		String userID = preferences.getString("userID", "");

		JSONObject jsonObject = request
				.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/subscribe/format/json?user_id="
						+ userID + "&apikey=" + apiKey + subscrParams);

		Log.d("url",
				"http://jobshunter.mobi/app/index.php/api/subscribe/format/json?user_id="
						+ userID + "&apikey=" + apiKey + subscrParams);

		Log.d(TAG, jsonObject.toString());
	}

	public void getSubscribes() {
		String apiKey = preferences.getString("apiKey", "");
		String userID = preferences.getString("userID", "");
		String subs = preferences.getString("subs", "");
		String subsTypes = preferences.getString("subsTypes", "");

		JSONArray subsTypesArray = request
				.doJSONArrayGetRequest("http://jobshunter.mobi/app/index.php/api/subscription_types/format/json");

		JSONArray subsArray = request
				.doJSONArrayGetRequest("http://jobshunter.mobi/app/index.php/api/all_subscriptions/format/json?user_id="
						+ userID + "&apikey=" + apiKey);

		SharedPreferences.Editor editor = preferences.edit();
		if (!subs.equals(subsArray.toString())) {
			editor.putString("subs", subsArray.toString());
		}
		if (!subsTypes.equals(subsArray.toString())) {
			editor.putString("subsTypes", subsTypesArray.toString());
		}
		editor.commit();

	}
}
