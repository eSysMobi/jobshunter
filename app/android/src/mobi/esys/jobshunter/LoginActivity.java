package mobi.esys.jobshunter;

import java.util.concurrent.ExecutionException;

import mobi.esys.tasks.RegTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;

public class LoginActivity extends Activity {
	private transient String[] scopes = { VKScope.FRIENDS };
	private transient String provider = "v";
	VKSdkListener sdkListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String apiKey = getSharedPreferences("JHPref", MODE_PRIVATE).getString(
				"apiKey", "");
		if (apiKey.equals("")) {

			setContentView(R.layout.activity_login);

			ImageButton fbBtn = (ImageButton) findViewById(R.id.fbBtn);
			fbBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					provider = "fb";
					// Session.openActiveSession(LoginActivity.this, true,
					// new Session.StatusCallback() {
					//
					// @Override
					// public void call(Session session,
					// SessionState state, Exception exception) {
					// Toast.makeText(
					// LoginActivity.this,
					// state.toString()
					// + " "
					// + String.valueOf(session
					// .isOpened()),
					// Toast.LENGTH_SHORT).show();
					//
					// if (session.isOpened()) {
					// Request.newMeRequest(
					// session,
					// new Request.GraphUserCallback() {
					//
					// @Override
					// public void onCompleted(
					// GraphUser user,
					// Response response) {
					// finish();
					// }
					// });
					// }
					// }
					// });
				}
			});

			ImageButton vkBtn = (ImageButton) findViewById(R.id.vkBtn);
			vkBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sdkListener = new VKSdkListener() {

						@Override
						public void onTokenExpired(VKAccessToken expiredToken) {
							VKSdk.authorize(scopes, true, false);
						}

						@Override
						public void onCaptchaError(VKError captchaError) {
							new VKCaptchaDialog(captchaError).show();
						}

						@Override
						public void onAccessDenied(VKError authorizationError) {
							new AlertDialog.Builder(LoginActivity.this)
									.setMessage(authorizationError.errorMessage)
									.show();
						}

						@Override
						public void onReceiveNewToken(VKAccessToken newToken) {
							super.onReceiveNewToken(newToken);
							VKRequest request = VKApi.users().get();
							request.executeWithListener(new VKRequestListener() {
								@Override
								public void onComplete(VKResponse response) {
									super.onComplete(response);
									String fname = "";
									String lname = "";
									String snetwork = "v";
									String nID = "";

									try {
										JSONObject user = response.json
												.getJSONArray("response")
												.getJSONObject(0);
										fname = user.getString("first_name");
										lname = user.getString("last_name");
										nID = user.getString("id");
									} catch (JSONException e) {
									}
									Bundle regBundle = new Bundle();
									regBundle.putString("fname", fname);
									regBundle.putString("lname", lname);
									regBundle.putString("snetwork", snetwork);
									regBundle.putString("nID", nID);

									RegTask regTask = new RegTask();
									regTask.execute(regBundle);

									try {
										SharedPreferences preferences = getSharedPreferences(
												"JHPref", MODE_PRIVATE);
										SharedPreferences.Editor editor = preferences
												.edit();
										editor.putString("apiKey", regTask
												.get().getString("apiKey"));
										editor.putString("userID", regTask
												.get().getString("userID"));

										editor.commit();
									} catch (InterruptedException e) {
									} catch (ExecutionException e) {
									}
									finish();
									startActivity(new Intent(
											LoginActivity.this,
											MainActivity.class));
								}
							});

						}
					};
					provider = "vk";
					VKSdk.initialize(sdkListener, "4187349", VKAccessToken
							.tokenFromSharedPreferences(LoginActivity.this,
									"VK_ACCESS_TOKEN"));
					VKSdk.authorize(scopes, true, false);

				}
			});
		} else {
			finish();
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (provider.equals("fb")) {
			// Session.getActiveSession().onActivityResult(LoginActivity.this,
			// requestCode, resultCode, data);
		} else {
			VKUIHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		VKUIHelper.onResume(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VKUIHelper.onDestroy(this);
	}
}
