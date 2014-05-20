package mobi.esys.specific_data_type;

import android.app.Fragment;

public class MenuItem {
	private transient String menuItemText;
	private transient int menuItemImageResource;
	private transient Fragment fragment;

	public MenuItem(String menuItemText, int menuItemImageResource,
			Fragment fragment) {
		super();
		this.menuItemText = menuItemText;
		this.menuItemImageResource = menuItemImageResource;
		this.fragment = fragment;
	}

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public String getMenuItemText() {
		return menuItemText;
	}

	public void setMenuItemText(String menuItemText) {
		this.menuItemText = menuItemText;
	}

	public int getMenuItemImageResource() {
		return menuItemImageResource;
	}

	public void setMenuItemImageResource(int menuItemImageResource) {
		this.menuItemImageResource = menuItemImageResource;
	}

	@Override
	public String toString() {
		return "MenuItem [menuItemText=" + menuItemText
				+ ", menuItemImageResource=" + menuItemImageResource
				+ ", activity=" + fragment + "]";
	}

}
