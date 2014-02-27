package mobi.esys.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class Request {
	private InputStream inputStream;

	public JSONObject doJSONGetRequest(String requestURL) {
		InputStream inputStream = new InputStream() { // NOPMD by Àðò¸ì on
														// 03.06.13 13:02

			@Override
			public int read() throws IOException {
				return 0;
			}
		};

		String result = "";
		JSONObject jsonObject = new JSONObject();

		try {
			final HttpClient httpClient = new DefaultHttpClient();
			final HttpGet httpGet = new HttpGet(requestURL);

			final HttpResponse httpResponse = httpClient.execute(httpGet);
			final HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();

			final BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			final StringBuilder stringBuilder = new StringBuilder();
			String line = "";

			while (line != null) {
				line = bufferedReader.readLine();
				stringBuilder.append(line + "\n");
			}

			inputStream.close();

			result = stringBuilder.toString();

			jsonObject = new JSONObject(result);
		} catch (Exception Exception) {
		}
		return jsonObject;
	}

	public JSONArray doJSONArrayGetRequest(String requestURL) {
		inputStream = new InputStream() { // NOPMD by Àðò¸ì on
											// 03.06.13 13:02

			@Override
			public int read() throws IOException {
				return 0;
			}
		};

		String result = "";
		JSONArray jsonObject = new JSONArray();

		try {
			final HttpClient httpClient = new DefaultHttpClient();
			final HttpGet httpGet = new HttpGet(requestURL);

			final HttpResponse httpResponse = httpClient.execute(httpGet);
			final HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();

			final BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			final StringBuilder stringBuilder = new StringBuilder();
			String line = "";

			while (line != null) {
				line = bufferedReader.readLine();
				stringBuilder.append(line + "\n");
			}

			inputStream.close();

			result = stringBuilder.toString();

			jsonObject = new JSONArray(result);
		} catch (Exception Exception) {
		}
		return jsonObject;
	}
}
