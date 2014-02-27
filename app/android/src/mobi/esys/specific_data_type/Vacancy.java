package mobi.esys.specific_data_type;

import android.os.Parcel;
import android.os.Parcelable;

public class Vacancy implements Parcelable {
	private transient String vacancyID;
	private transient String vacancyProvider;
	private transient String vacancyName;
	private transient String vacancyTel;
	private transient String vacancyMail;
	private transient String vacancyDemand;
	private transient String vacancySalary;
	private transient String vacancyDescription;
	private transient String favVacID;
	private transient String haveNextPage;

	public Vacancy(String vacancyID, String vacancyProvider,
			String vacancyName, String vacancyTel, String vacancyMail,
			String vacancyDemand, String vacancySalary,
			String vacancyDescription, String favVacID, String haveNextPage) {
		super();
		this.vacancyID = vacancyID;
		this.vacancyProvider = vacancyProvider;
		this.vacancyName = vacancyName;
		this.vacancyTel = vacancyTel;
		this.vacancyMail = vacancyMail;
		this.vacancyDemand = vacancyDemand;
		this.vacancySalary = vacancySalary;
		this.vacancyDescription = vacancyDescription;
		this.favVacID = favVacID;
		this.haveNextPage = haveNextPage;
	}

	public String getFavVacID() {
		return favVacID;
	}

	public void setFavVacID(String favVacID) {
		this.favVacID = favVacID;
	}

	public String getVacancyProvider() {
		return vacancyProvider;
	}

	public void setVacancyProvider(String vacancyProvider) {
		this.vacancyProvider = vacancyProvider;
	}

	public String getVacancyName() {
		return vacancyName;
	}

	public void setVacancyName(String vacancyName) {
		this.vacancyName = vacancyName;
	}

	public String getVacancyTel() {
		return vacancyTel;
	}

	public void setVacancyTel(String vacancyTel) {
		this.vacancyTel = vacancyTel;
	}

	public String getVacancyMail() {
		return vacancyMail;
	}

	public void setVacancyMail(String vacancyMail) {
		this.vacancyMail = vacancyMail;
	}

	public String getVacancyDemand() {
		return vacancyDemand;
	}

	public void setVacancyDemand(String vacancyDemand) {
		this.vacancyDemand = vacancyDemand;
	}

	public String getVacancySalary() {
		return vacancySalary;
	}

	public void setVacancySalary(String vacancySalary) {
		this.vacancySalary = vacancySalary;
	}

	public String getVacancyDescription() {
		return vacancyDescription;
	}

	public void setVacancyDescription(String vacancyDescription) {
		this.vacancyDescription = vacancyDescription;
	}

	@Override
	public String toString() {
		return "Vacancy [vacancyID=" + vacancyID + ", vacancyProvider="
				+ vacancyProvider + ", vacancyName=" + vacancyName
				+ ", vacancyTel=" + vacancyTel + ", vacancyMail=" + vacancyMail
				+ ", vacancyDemand=" + vacancyDemand + ", vacancySalary="
				+ vacancySalary + ", vacancyDescription=" + vacancyDescription
				+ ", favVacID=" + favVacID + ", haveNextPage=" + haveNextPage
				+ "]";
	}

	private Vacancy(Parcel in) {
		vacancyID = in.readString();
		vacancyProvider = in.readString();
		vacancyName = in.readString();
		vacancyTel = in.readString();
		vacancyMail = in.readString();
		vacancyDemand = in.readString();
		vacancySalary = in.readString();
		vacancyDescription = in.readString();
		favVacID = in.readString();
		haveNextPage = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(vacancyID);
		arg0.writeString(vacancyProvider);
		arg0.writeString(vacancyName);
		arg0.writeString(vacancyTel);
		arg0.writeString(vacancyMail);
		arg0.writeString(vacancyDemand);
		arg0.writeString(vacancySalary);
		arg0.writeString(vacancyDescription);
		arg0.writeString(favVacID);
		arg0.writeString(haveNextPage);

	}

	public static final Parcelable.Creator<Vacancy> CREATOR = new Parcelable.Creator<Vacancy>() {
		public Vacancy createFromParcel(Parcel in) {
			return new Vacancy(in);
		}

		public Vacancy[] newArray(int size) {
			return new Vacancy[size];
		}
	};

	public String getVacancyID() {
		return vacancyID;
	}

	public void setVacancyID(String vacancyID) {
		this.vacancyID = vacancyID;
	}

	public String getHaveNextPage() {
		return haveNextPage;
	}

	public void setHaveNextPage(String haveNextPage) {
		this.haveNextPage = haveNextPage;
	}

}
