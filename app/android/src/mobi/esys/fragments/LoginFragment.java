package mobi.esys.fragments;

import mobi.esys.jobshunter.R;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	// private static final String VK_TOKEN = "4187349";
	// private static final String VK_AT = "VK_ACCESS_TOKEN";
	private transient View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.login_fragment, container, false);
		// String apiKey = getActivity().getSharedPreferences(
		// Constants.PREF_STRING, Context.MODE_PRIVATE).getString(
		// Constants.API_KEY, "");

		// Log.d("hash",
		// Arrays.asList(
		// VKUtil.getCertificateFingerprint(getActivity(),
		// getActivity().getPackageName())).toString()
		// .replace("[", "").replace("]", ""));

		// try {
		// PackageInfo info = getActivity().getPackageManager()
		// .getPackageInfo("mobi.esys.jobshunter",
		// PackageManager.GET_SIGNATURES);
		// for (Signature signature : info.signatures) {
		// MessageDigest md = MessageDigest.getInstance("SHA");
		// md.update(signature.toByteArray());
		// Log.d("KeyHash:",
		// Base64.encodeToString(md.digest(), Base64.DEFAULT));
		// }
		// } catch (NameNotFoundException e) {
		//
		// } catch (NoSuchAlgorithmException e) {
		//
		// }

		// ImageButton fbBtn = (ImageButton) view.findViewById(R.id.fbBtn);
		// fbBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Fragment webFragment = new WebViewFragment();
		// Bundle webLoginParams = new Bundle();
		// webLoginParams.putString("provider", "fb");
		// webFragment.setArguments(webLoginParams);
		// getFragmentManager().beginTransaction()
		// .replace(R.id.frmCont, webFragment, "web_login")
		// .addToBackStack(null).commit();
		// }
		// });
		// fbBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// provider = "fb";
		// Session.openActiveSession(getActivity(), true,
		// new Session.StatusCallback() {
		//
		// @Override
		// public void call(Session session,
		// SessionState state, Exception exception) {
		// if (session.isOpened()) {
		//
		// // make request to the /me API
		// Request.newMeRequest(session,
		// new Request.GraphUserCallback() {
		//
		// @Override
		// public void onCompleted(
		// GraphUser user,
		// Response response) {
		// if (user != null) {
		// Log.d("user",
		// user.getName());
		// }
		//
		// }
		//
		// // callback after Graph API
		// // response with user object
		//
		// }).executeAsync();
		// }
		// }
		//
		// });
		// }
		//
		// });

		ImageButton vkBtn = (ImageButton) view.findViewById(R.id.vkBtn);
		vkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isNetworkConnected()) {
					Fragment webFragment = new WebViewFragment();
					Bundle webLoginParams = new Bundle();
					webLoginParams.putString("provider", "vk");
					webFragment.setArguments(webLoginParams);
					getFragmentManager().beginTransaction()
							.replace(R.id.frmCont, webFragment, "web_login")
							.addToBackStack(null).commit();
				} else {
					Toast.makeText(
							getActivity(),
							"Нет подключения к интерненту.Невозможно войти в учетную запись",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		// vkBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// provider = "vk";
		// VKSdk.authorize(scopes, true, true);
		// };
		//
		// });

		return view;
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	// Toast.makeText(getActivity(), "Resume", Toast.LENGTH_SHORT).show();
	// VKUIHelper.onResume(getActivity());
	//
	// }
	//
	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// Toast.makeText(getActivity(), "Destroy", Toast.LENGTH_SHORT).show();
	// VKUIHelper.onDestroy(getActivity());
	//
	// }
	//
	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// Toast.makeText(getActivity(), "Activity res", Toast.LENGTH_SHORT)
	// .show();
	//
	// if (provider.equals("fb")) {
	// Session.getActiveSession().onActivityResult(getActivity(),
	// requestCode, resultCode, data);
	// } else {
	// VKUIHelper.onActivityResult(requestCode, resultCode, data);
	// }
	// }

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

}
