package mobi.esys.specific_data_type;

public class City {
	private transient String cityID;
	private transient String cityName;

	public City(String cityID, String cityName) {
		super();
		this.cityID = cityID;
		this.cityName = cityName;
	}

	public String getCityID() {
		return cityID;
	}

	public void setCityID(String cityID) {
		this.cityID = cityID;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public String toString() {
		return "City [cityID=" + cityID + ", cityName=" + cityName + "]";
	}

}
