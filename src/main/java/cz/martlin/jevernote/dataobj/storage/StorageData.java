package cz.martlin.jevernote.dataobj.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorageData implements Serializable {

	private static final long serialVersionUID = 3517362912494355605L;

	private final Map<Package, List<Item>> data;

	public StorageData(Map<Package, List<Item>> data) {
		super();
		this.data = data;
	}

	public Map<Package, List<Item>> getData() {
		return data;
	}

	public List<Package> getPackages() {
		return new ArrayList<>(data.keySet());
	}

	public List<Item> getItems() {
		List<Item> items = new ArrayList<>();

		data.values().forEach((l) -> items.addAll(l));

		return items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		StorageData other = (StorageData) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StorageDate [data=" + data + "]";
	}

}
