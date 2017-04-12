package cz.martlin.jevernote.dataobj.cmp;

import java.util.List;

import cz.martlin.jevernote.dataobj.storage.*;
import cz.martlin.jevernote.dataobj.storage.Package;

public class StoragesDifference {

	private final List<Change<Package>> packageDifferences;
	private final List<Change<Item>> itemsDifferences;

	public StoragesDifference(List<Change<Package>> packageDifferences, List<Change<Item>> itemsDifferences) {
		super();
		this.packageDifferences = packageDifferences;
		this.itemsDifferences = itemsDifferences;
	}

	public List<Change<Package>> getPackageDifferences() {
		return packageDifferences;
	}

	public List<Change<Item>> getItemsDifferences() {
		return itemsDifferences;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemsDifferences == null) ? 0 : itemsDifferences.hashCode());
		result = prime * result + ((packageDifferences == null) ? 0 : packageDifferences.hashCode());
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
		StoragesDifference other = (StoragesDifference) obj;
		if (itemsDifferences == null) {
			if (other.itemsDifferences != null)
				return false;
		} else if (!itemsDifferences.equals(other.itemsDifferences))
			return false;
		if (packageDifferences == null) {
			if (other.packageDifferences != null)
				return false;
		} else if (!packageDifferences.equals(other.packageDifferences))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StoragesDifferences [packageDifferences=" + packageDifferences + ", itemsDifferences="
				+ itemsDifferences + "]";
	}

}
