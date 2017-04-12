package cz.martlin.jevernote.dataobj.storage;

import java.io.Serializable;

public class Package implements Serializable, Cloneable {

	private static final long serialVersionUID = 4296043013265462681L;

	private String id;
	private String name;

	public Package(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Package other = (Package) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Package [id=" + id + ", name=" + name + "]";
	}

	public Package copy() {
		String id = this.getId();
		String name = this.getName();

		return new Package(id, name);
	}

	@Override
	protected Object clone() {
		return copy();
	}

}
