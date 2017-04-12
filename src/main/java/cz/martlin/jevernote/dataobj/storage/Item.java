package cz.martlin.jevernote.dataobj.storage;

import java.io.Serializable;
import java.util.Calendar;

public class Item implements Serializable, Cloneable {

	private static final long serialVersionUID = 1884785802441695009L;

	private Package pack;
	private String id;
	private String name;
	private String content;
	private Calendar lastModifiedAt;

	public Item(Package pack, String id, String name, String content, Calendar lastModifiedAt) {
		super();
		this.pack = pack;
		this.id = id;
		this.name = name;
		this.content = content;
		this.lastModifiedAt = lastModifiedAt;
	}

	public Package getPack() {
		return pack;
	}

	public void setPack(Package pack) {
		this.pack = pack;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Calendar getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Calendar lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
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
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", content=" + content + ", lastModifiedAt="
				+ (lastModifiedAt != null ? lastModifiedAt.getTime() : "-") + ", pack=" + pack + "]";
	}

	public Item copy() {
		Package pack = this.getPack().copy();
		String id = this.getId();
		String name = this.getName();
		String content = this.getContent();
		Calendar lastModifiedAt = this.getLastModifiedAt();

		return new Item(pack, id, name, content, lastModifiedAt);

	}

	@Override
	protected Object clone() {
		return copy();
	}

}
