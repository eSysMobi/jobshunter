package mobi.esys.specific_data_type;

public class Category {
	private transient String name;
	private transient String id;
	private transient String provider;

	public Category(String name, String id, String provider) {
		super();
		this.name = name;
		this.id = id;
		this.provider = provider;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return "Category [name=" + name + ", id=" + id + ", provider="
				+ provider + "]";
	}

}
