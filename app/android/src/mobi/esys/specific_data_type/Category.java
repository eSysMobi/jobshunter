package mobi.esys.specific_data_type;

import java.io.Serializable;

public class Category implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2841196599146353679L;
	private transient String name;
	private transient String id;

	public Category(String name, String id) {
		super();
		this.name = name;
		this.id = id;
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

	@Override
	public String toString() {
		return "Category [name=" + name + ", id=" + id;
	}

}
