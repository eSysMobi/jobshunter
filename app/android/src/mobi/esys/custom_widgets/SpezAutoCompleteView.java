package mobi.esys.custom_widgets;

import mobi.esys.jobshunter.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

public class SpezAutoCompleteView extends TokenCompleteTextView {
	private transient Context context;

	public SpezAutoCompleteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected Object defaultObject(String arg0) {
		return null;
	}

	@Override
	protected View getViewForObject(Object arg0) {
		String c = (String) arg0;
		LayoutInflater l = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout) l.inflate(R.layout.token_layout,
				(ViewGroup) SpezAutoCompleteView.this.getParent(), false);
		((TextView) view.findViewById(R.id.name)).setText(c);

		return view;
	}
}
