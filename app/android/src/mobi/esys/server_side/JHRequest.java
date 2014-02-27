package mobi.esys.server_side;

import java.util.ArrayList;
import java.util.List;

import mobi.esys.networking.Request;
import mobi.esys.specific_data_type.Category;
import mobi.esys.specific_data_type.City;
import mobi.esys.specific_data_type.SuperCategory;
import mobi.esys.specific_data_type.Vacancy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

public class JHRequest {
	private transient Request request;

	public JHRequest() {
		request = new Request();
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
				// apiKey = login(snetwork, nID);
				// apiBundle.putString("apiKey", apiKey);

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
		List<City> cities = new ArrayList<City>();
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
		return cities;
	}

	public List<SuperCategory> getCats() {
		List<SuperCategory> categories = new ArrayList<SuperCategory>();
		JSONArray sjArray = request
				.doJSONArrayGetRequest("https://api.superjob.ru/1.0/catalogues?all=1");
		JSONArray hhArray = request
				.doJSONArrayGetRequest("https://api.hh.ru/specializations");

		try {
			for (int i = 0; i < sjArray.length(); i++) {
				JSONObject currSJCat;

				currSJCat = sjArray.getJSONObject(i);

				JSONArray subsArray = sjArray.getJSONObject(i).getJSONArray(
						"positions");
				List<Category> subCats = new ArrayList<Category>();
				for (int j = 0; j < subsArray.length(); j++) {
					JSONObject currSubCats = subsArray.getJSONObject(j);
					subCats.add(new Category(
							currSubCats.getString("title_rus"), currSubCats
									.getString("key"), "sj"));
				}
				categories.add(new SuperCategory(currSJCat
						.getString("title_rus"), currSJCat.getString("key"),
						subCats));
			}

			for (int i = 0; i < hhArray.length(); i++) {
				JSONObject currentHHCat = hhArray.getJSONObject(i);
				List<Category> hhSubCats = new ArrayList<Category>();

				JSONArray subArray = currentHHCat
						.getJSONArray("specializations");
				for (int j = 0; j < subArray.length(); j++) {
					JSONObject currSubHHObj = subArray.getJSONObject(j);
					hhSubCats.add(new Category(currSubHHObj.getString("name"),
							currSubHHObj.getString("id"), "hh"));
				}
				categories.add(new SuperCategory(
						currentHHCat.getString("name"), currentHHCat
								.getString("id"), hhSubCats));
			}

		} catch (JSONException e) {
		}
		Log.d("cats", categories.toString());
		return categories;
	}

	public ArrayList<Vacancy> getVacancies(String paramString, String pageNum) {
		ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();
		JSONObject vacObject = request
				.doJSONGetRequest("http://jobshunter.mobi/app/index.php/api/vacancies/format/json?"
						+ paramString + "&page=" + pageNum);
		Log.d("url",
				"http://jobshunter.mobi/app/index.php/api/vacancies/format/json?"
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
}
