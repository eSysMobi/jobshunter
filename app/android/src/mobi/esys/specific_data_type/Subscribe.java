package mobi.esys.specific_data_type;

public class Subscribe {
	private transient String subID;
	private transient String subName;
	private transient String specID;

	public Subscribe(String subID, String subName, String specID) {
		super();
		this.subID = subID;
		this.subName = subName;
		this.specID = specID;
	}

	public String getSubID() {
		return subID;
	}

	public void setSubID(String subID) {
		this.subID = subID;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public String getSpecID() {
		return specID;
	}

	public void setSpecID(String specID) {
		this.specID = specID;
	}

	@Override
	public String toString() {
		return "Subscribe [subID=" + subID + ", subName=" + subName
				+ ", specID=" + specID + ", getSubID()=" + getSubID()
				+ ", getSubName()=" + getSubName() + ", getSpecID()="
				+ getSpecID() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

}
