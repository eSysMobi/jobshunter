package mobi.esys.specific_data_type;

import java.util.List;

public class SuperCategory {
	private transient String name;
	private transient String id;
	private transient List<Category> subCategories;

	public SuperCategory(String name, String id, List<Category> subCategories) {
		super();
		this.name = name;
		this.id = id;
		this.subCategories = subCategories;
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

	public List<Category> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<Category> subCategories) {
		this.subCategories = subCategories;
	}

	@Override
	public String toString() {
		return "SuperCategory [name=" + name + ", id=" + id
				+ ", subCategories=" + subCategories + "]";
	}

}
