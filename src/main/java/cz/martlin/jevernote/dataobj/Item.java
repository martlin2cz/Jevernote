package cz.martlin.jevernote.dataobj;

import java.util.Calendar;

public class Item {

	private String id;
	private String name;
	private String content;
	private Calendar lastModifiedAt;

	public Item(String id, String name, String content, Calendar lastModifiedAt) {
		super();
		this.id = id;
		this.name = name;
		this.content = content;
		this.lastModifiedAt = lastModifiedAt;
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
				+ (lastModifiedAt != null ? lastModifiedAt.getTime() : "-") + "]";
	}

}
