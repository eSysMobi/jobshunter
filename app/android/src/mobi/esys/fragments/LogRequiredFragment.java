package mobi.esys.fragments;

import mobi.esys.jobshunter.MainActivity;
import mobi.esys.jobshunter.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class LogRequiredFragment extends Fragment {
	private transient View view;
	private transient Fragment loginFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.logreg_fragment, container, false);
		loginFragment = new LoginFragment();
		Button logReqLoginBtn = (Button) view.findViewById(R.id.loginRegBtn);
		logReqLoginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (((MainActivity) getActivity()).getSlidingMenu()
						.isActivated()) {
					((MainActivity) getActivity()).getSlidingMenu().toggle();
				}

				((MainActivity) getActivity()).getFragmentManager()
						.beginTransaction()
						.replace(R.id.frmCont, loginFragment, "jobsTag")
						.commit();

				((MainActivity) getActivity()).setFragmentLabel("Войти");
				((MainActivity) getActivity()).hideFilter();
			}
		});
		return view;
	}
}
