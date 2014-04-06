package mobi.esys.specific_data_type;

import java.util.List;

public class SupSubs {
	private transient String subsID;
	private transient List<Subscribe> subscribes;

	public SupSubs(String subsID, List<Subscribe> subscribes) {
		super();
		this.subsID = subsID;
		this.subscribes = subscribes;
	}

	public String getSubsID() {
		return subsID;
	}

	public void setSubsID(String subsID) {
		this.subsID = subsID;
	}

	public void addSubs(Subscribe subscribe) {
		this.subscribes.add(subscribe);
	}

	public List<Subscribe> getSubscribes() {
		return subscribes;
	}

	public void setSubscribes(List<Subscribe> subscribes) {
		this.subscribes = subscribes;
	}

	@Override
	public String toString() {
		return "SupSubs [subsID=" + subsID + ", subscribes=" + subscribes + "]";
	}

}
