package mobi.esys.adapters;

import java.util.List;

import mobi.esys.constants.Constants;
import mobi.esys.jobshunter.R;
import mobi.esys.specific_data_type.MenuItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<MenuItem> {
	private transient List<MenuItem> menuItems;
	private transient LayoutInflater inflater;
	private transient MenuHolder holder;
	private transient SharedPreferences preferences;

	public MenuAdapter(final List<MenuItem> menuItems, Context context) {
		super(context, R.layout.menu_item_layout, menuItems);
		this.menuItems = menuItems;
		this.inflater = ((LayoutInflater) context.getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		this.preferences = context.getSharedPreferences(Constants.PREF_STRING,
				Context.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public MenuItem getItem(int position) {
		return menuItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return menuItems.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View menuView = convertView;

		if (menuView == null) {
			menuView = this.inflater.inflate(R.layout.menu_item_layout, parent,
					false);
		}

		holder = new MenuHolder();
		holder.menuText = (TextView) menuView.findViewById(R.id.menuItemText);
		holder.menuImage = (ImageView) menuView
				.findViewById(R.id.menuItemImage);

		int width = preferences.getInt("screenWidth", 720);
		holder.menuText.setText(menuItems.get(position).getMenuItemText());
		holder.menuImage.setImageResource(menuItems.get(position)
				.getMenuItemImageResource());
		RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
				width / 6, width / 6);
		imageParams.addRule(RelativeLayout.ALIGN_LEFT);
		imageParams.addRule(RelativeLayout.CENTER_VERTICAL);
		holder.menuImage.setLayoutParams(imageParams);
		menuView.setTag(holder);

		return menuView;
	}

	static class MenuHolder {
		private transient TextView menuText;
		private transient ImageView menuImage;
	}
}
