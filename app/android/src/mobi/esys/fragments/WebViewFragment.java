package mobi.esys.fragments;

import mobi.esys.jobshunter.R;
import mobi.esys.tasks.GetVKUserInfo;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends Fragment {
	private transient View view;
	private transient String provider;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.webviewlogin_fragment, container,
				false);
		provider = getArguments().getString("provider");
		WebView loginWebView = (WebView) view.findViewById(R.id.webView1);
		if (provider.equals("vk")) {
			loginWebView
					.loadUrl("https://oauth.vk.com/authorize?client_id=4187349&redirect_uri=https://oauth.vk.com/blank.html&display=mobile&v=5.21&response_type=token&revoke=1");
		}

		else {
			loginWebView
					.loadUrl("http://www.facebook.com/dialog/oauth/?client_id=113455642165175&redirect_uri=https://www.facebook.com/connect/login_success.html");

		}

		loginWebView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				Log.d("url", url);
				String access_token = "";
				if (provider.equals("vk")) {
					if (url.contains("access_token")) {
						access_token = url.substring(url.indexOf("=") + 1,
								url.indexOf("&"));
						if (!access_token.equals("")) {
							GetVKUserInfo getVKUserInfo = new GetVKUserInfo(
									getActivity());
							getVKUserInfo.execute(access_token);
						}

					}
				} else {
					if (url.startsWith("https://www.facebook.com/connect/login_success.html")) {
						view.stopLoading();
						view.loadUrl("https://graph.facebook.com/oauth/access_token?client_id=280975482050591&redirect_uri=https://www.facebook.com/connect/login_success.html/&client_secret=7bcfcc59d607b6babccdf3a4190b5893&code="
								+ url.substring(url.indexOf("=") + 1));
					}
				}
			}
		});
		return view;
	}
}
