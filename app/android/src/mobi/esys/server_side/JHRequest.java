package mobi.esys.server_side;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobi.esys.constants.Constants;
import mobi.esys.networking.Request;
import mobi.esys.specific_data_type.Category;
import mobi.esys.specific_data_type.City;
import mobi.esys.specific_data_type.SuperCategory;
import mobi.esys.specific_data_type.Vacancy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class JHRequest {
	private transient Request request;
	private transient SharedPreferences preferences;

	private static final String TAG = "subscr";
	private static final String PREFIX = Constants.API_PREFIX;
	private static final String FORMAT = Constants.FORMAT;
	private static final String REG_USER = Constants.REG_USER;
	private static final String API_KEY = Constants.API_KEY;
	private static final String USER_ID = Constants.USER_ID;
	private static final String PARAM_API_KEY = "&apikey=";
	private static final String PARAM_USER_ID = "?user_id=";
	private static final String SALARY_FROM = "salary_from";
	private static final String SALARY_TO = "salary_to";

	private transient String prefApiKey;
	private transient String prefUserID;

	private transient Context context;

	public JHRequest(Context context) {
		this.request = new Request();
		this.preferences = context.getSharedPreferences(Constants.PREF_STRING,
				Context.MODE_PRIVATE);
		this.prefApiKey = preferences.getString(API_KEY, "");
		this.prefUserID = preferences.getString(USER_ID, "");
		this.context = context;
	}

	public JSONObject getSpec() {
		JSONObject jsonObject = new JSONObject();
		return jsonObject;
	}

	public Bundle reg(String fname, String lname, String snetwork, String nID) {
		String apiKey = "";
		String userID = "";
		Bundle apiBundle = new Bundle();
		JSONObject req = request.doJSONGetRequest(PREFIX + REG_USER + FORMAT
				+ "?last_name=" + lname + "&first_name=" + fname + "&network="
				+ snetwork + "&network_id=" + nID);
		try {
			Log.d("req", req.toString());
			if (req.has("error")
					&& req.getString("error").equals("User exists")) {

				apiBundle = login(snetwork, nID);
			} else {
				apiKey = req.getString("apikey");
				userID = req.getString("id");
				apiBundle.putString(API_KEY, apiKey);
				apiBundle.putString(USER_ID, userID);
			}

		} catch (JSONException e) {
		}

		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(API_KEY, apiBundle.getString(API_KEY));
		editor.putString(USER_ID, apiBundle.getString(USER_ID));
		editor.commit();
		Log.d("reg_pref", preferences.getString(API_KEY, "") + " "
				+ preferences.getString(USER_ID, ""));

		return apiBundle;
	}

	public Bundle login(String networkName, String nID) {
		String apiKey = "";
		String userID = "";
		Bundle apiBundle = new Bundle();
		JSONObject req = request.doJSONGetRequest(PREFIX + REG_USER + FORMAT
				+ "?network=" + networkName + "&network_id=" + nID);
		try {
			apiKey = req.getString("apikey");
			userID = req.getString("id");
		} catch (JSONException e) {
		}
		apiBundle.putString(API_KEY, apiKey);
		apiBundle.putString(USER_ID, userID);
		return apiBundle;
	}

	public List<City> getCities(String findStr) {
		String cities_str = Constants.CITIES;
		Log.d("city", "city task works");
		List<City> cities = new ArrayList<City>();
		Log.d("url", PREFIX + cities_str + FORMAT + "?str=" + findStr);
		JSONObject cityObj = request.doJSONGetRequest(PREFIX + "cities"
				+ FORMAT + "?str=" + findStr);
		try {
			JSONArray cityArray = cityObj.getJSONArray(cities_str);
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
		JSONArray serverArray = request.doJSONArrayGetRequest(PREFIX
				+ "sitecats" + FORMAT);
		if (preferences.getString("cats", "").equals("")
				|| (!Arrays.asList(preferences.getString("cats", "")).equals(
						serverArray.toString()) && !serverArray.toString()
						.equals("[]"))) {
			array = serverArray;
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("cats", array.toString());
			editor.commit();
		} else {
			try {
				array = new JSONArray(preferences.getString("cats", ""));
			} catch (JSONException e) {
			}
		}
		Log.d("cats", array.toString());
		return array;

	}

	public ArrayList<Vacancy> getVacancies(String paramString, String pageNum,
			String sortString) {
		String vacancies_str = Constants.VACANCIES;
		ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();
		JSONObject vacObject = new JSONObject();
		Log.d("vacObj", vacObject.toString());
		if (sortString.isEmpty() || sortString == null || sortString.equals("")) {
			vacObject = request.doJSONGetRequest(PREFIX + vacancies_str
					+ FORMAT + "?" + paramString + "&page=" + pageNum);
		} else {
			vacObject = request.doJSONGetRequest(PREFIX + vacancies_str
					+ FORMAT + "?" + paramString + "&page=" + pageNum
					+ "&sort=" + sortString);
		}
		Log.d("url", PREFIX + vacancies_str + FORMAT + "?" + paramString
				+ "&page=" + pageNum + sortString);

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
				if (currVacObject.has(SALARY_FROM)) {

					if (currVacObject.getInt(SALARY_FROM) == 0
							&& currVacObject.getInt(SALARY_TO) == 0) {
						salary = "Договорная";
					} else if (currVacObject.getInt(SALARY_FROM) == 0
							&& currVacObject.getInt(SALARY_TO) != 0) {
						salary = "до "
								+ String.valueOf(currVacObject
										.getInt(SALARY_TO)) + " руб.";
					} else if (currVacObject.getInt(SALARY_FROM) != 0
							&& currVacObject.getInt(SALARY_TO) == 0) {
						salary = "от "
								+ String.valueOf(currVacObject
										.getInt(SALARY_FROM)) + " руб.";
					} else {
						salary = String.valueOf(currVacObject
								.getInt(SALARY_FROM))
								+ "-"
								+ String.valueOf(currVacObject.get(SALARY_TO))
								+ " руб.";
					}
				}

				if (currVacObject.has("description")) {
					desc = currVacObject.getString("description");
				} else {
					desc = "Нет описания";
				}

				vacancies.add(new Vacancy(currVacObject.getString("site_id"),
						currVacObject.getString("site"), currVacObject
								.getString("name"), currVacObject
								.getString("creation_date"), "", "", "",
						salary, desc, "", morePage, currVacObject
								.getString("city")));

			}
			Log.d("vac", String.valueOf(vacancies.size()));

		} catch (JSONException e) {
		}
		Log.d("vac", String.valueOf(vacancies.size()));
		return vacancies;
	}

	public JSONObject addToFav(String userID, String apiKey, String site,
			String siteID) {
		JSONObject addFavObject = request.doJSONGetRequest(PREFIX
				+ Constants.ADD_BOOKMARK + FORMAT + PARAM_USER_ID + userID
				+ PARAM_API_KEY + apiKey + "&site=" + site + "&site_id="
				+ siteID);
		getFav(userID, apiKey);
		return addFavObject;
	}

	public JSONArray getFav(String userID, String apiKey) {
		JSONArray fav = request.doJSONArrayGetRequest(PREFIX + Constants.FAV
				+ FORMAT + PARAM_USER_ID + userID + PARAM_API_KEY + apiKey);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("favCount", String.valueOf(fav.length()));
		editor.commit();
		return fav;
	}

	public JSONObject deleteFromFav(String userID, String apiKey, String favID) {
		JSONObject deleteFavObject = request.doJSONGetRequest(PREFIX
				+ Constants.FAV_DELETE + FORMAT + PARAM_USER_ID + userID
				+ PARAM_API_KEY + apiKey + "&bookmark_id=" + favID);
		return deleteFavObject;
	}

	public void addGCMNotifications(String GSMResID) {

		Log.d("par",
				preferences.getString(API_KEY, "") + " "
						+ preferences.getString(USER_ID, ""));
		if (!prefApiKey.isEmpty() && !prefUserID.isEmpty()) {
			JSONObject res = request.doJSONGetRequest(PREFIX
					+ Constants.ADD_GCM_ID + FORMAT + PARAM_USER_ID
					+ prefUserID + PARAM_API_KEY + prefApiKey + "&gcmid="
					+ GSMResID);
			Log.d("res", res.toString());
		}

	}

	public void getWorkTypes() {
		String workTypes = preferences.getString("workTypes", "");
		JSONArray workTypeArray = request.doJSONArrayGetRequest(PREFIX
				+ "worktypes" + FORMAT);
		if (!workTypes.equals(workTypeArray.toString())) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("workTypes", workTypeArray.toString());
			editor.commit();
		}
	}

	public void subscribe(String subscrParams) {
		String subscribe_str = Constants.SUBSCRIBE;
		JSONObject jsonObject = request.doJSONGetRequest(PREFIX + subscribe_str
				+ FORMAT + PARAM_USER_ID + prefUserID + PARAM_API_KEY
				+ prefApiKey + subscrParams);

		Log.d("url", PREFIX + subscribe_str + FORMAT + PARAM_USER_ID
				+ prefUserID + PARAM_API_KEY + prefApiKey + subscrParams);

		Log.d(TAG, jsonObject.toString());
	}

	public void getSubscribes() {
		String subs = preferences.getString("subs", "");
		String subsTypes = preferences.getString("subsTypes", "");

		JSONArray subsTypesArray = request.doJSONArrayGetRequest(PREFIX
				+ "subscription_types" + FORMAT);

		JSONArray subsArray = request.doJSONArrayGetRequest(PREFIX
				+ Constants.ALL_SUBS + FORMAT + PARAM_USER_ID + prefUserID
				+ PARAM_API_KEY + prefApiKey);

		Log.d("url", PREFIX + Constants.ALL_SUBS + FORMAT + PARAM_USER_ID
				+ prefUserID + PARAM_API_KEY + prefApiKey);

		SharedPreferences.Editor editor = preferences.edit();
		if (!subs.equals(subsArray.toString())) {
			editor.putString("subs", subsArray.toString());
		}
		if (!subsTypes.equals(subsArray.toString())) {
			editor.putString("subsTypes", subsTypesArray.toString());
		}
		editor.commit();

	}

	public void unsubscribe(String subsID) {
		request.doJSONGetRequest(PREFIX + Constants.UNSUBS + FORMAT
				+ PARAM_USER_ID + prefUserID + PARAM_API_KEY + prefApiKey
				+ subsID);
	}

	public List<String> getCityNameByID(String[] citiesIds) {
		List<String> cities = new ArrayList<String>();
		for (int i = 0; i < citiesIds.length; i++) {
			JSONArray getJSONArray = request.doJSONArrayGetRequest(PREFIX
					+ "city_by_id" + FORMAT + "?city_id=" + citiesIds[i]);
			try {
				cities.add(getJSONArray.getJSONObject(0).getString("name"));
			} catch (JSONException e) {
			}
		}

		return cities;
	}

	// {"response":[{"last_name":"Черкасов","id":73275172,"first_name":"Артём"}]}

	public void getVKUser(String access_token) {
		JSONArray vkuserObj;
		try {
			vkuserObj = request.doJSONGetRequest(
					"https://api.vk.com/method/users.get?v=5.21&access_token="
							+ access_token).getJSONArray("response");
			JSONObject response = vkuserObj.getJSONObject(0);
			reg(response.getString("first_name"),
					response.getString("last_name"), "v",
					response.getString("id"));
		} catch (JSONException e) {
		}

		((Activity) context).finish();
		context.startActivity(((Activity) context).getIntent());
	}
}
