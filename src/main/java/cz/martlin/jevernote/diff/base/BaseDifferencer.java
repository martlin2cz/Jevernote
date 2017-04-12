package cz.martlin.jevernote.diff.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.martlin.jevernote.dataobj.cmp.Change;

public abstract class BaseDifferencer<T> {

	public BaseDifferencer() {
	}

	public List<Change<T>> computeDiff(List<T> source, List<T> target) {
		List<Change<T>> changes = new ArrayList<>();

		Map<String, T> sources = map(source);
		Map<String, T> targets = map(target);
		Set<String> ids = merge(sources.keySet(), targets.keySet());

		for (String id : ids) {
			T sourcePack = sources.get(id);
			T targetPack = targets.get(id);

			List<Change<T>> cmp = compare(sourcePack, targetPack);

			changes.addAll(cmp);
		}

		return changes;
	}	

	public List<Change<T>> compare(T sourceObj, T targetObj) {
		List<Change<T>> changes = new ArrayList<>();

		if (isAdded(sourceObj, targetObj)) {
			changes.add(Change.create(targetObj));
		}

		if (isRemoved(sourceObj, targetObj)) {
			changes.add(Change.delete(sourceObj));
		}

		if (isRenamed(sourceObj, targetObj)) {
			changes.add(Change.rename(sourceObj, targetObj));
		}

		if (isChanged(sourceObj, targetObj)) {
			changes.add(Change.update(sourceObj, targetObj));
		}

		return changes;
	}

	protected boolean isAdded(T sourceObj, T targetObj) {
		return sourceObj == null && targetObj != null;
	}

	protected boolean isRemoved(T sourceObj, T targetObj) {
		return sourceObj != null && targetObj == null;
	}

	protected abstract boolean isRenamed(T sourceObj, T targetObj);

	protected abstract boolean isChanged(T sourceObj, T targetObj);

	///////////////////////////////////////////////////////////////////////////

	private Map<String, T> map(List<T> list) {
		Map<String, T> map = new HashMap<>(list.size());

		for (T obj : list) {
			String id = idOf(obj);

			map.put(id, obj);
		}

		return map;
	}

	protected abstract String idOf(T obj);

	
	public static <E> Set<E> merge(Set<E> set1, Set<E> set2) {
		Set<E> result = new HashSet<>(set1.size() + set2.size());

		result.addAll(set1);
		result.addAll(set2);

		return result;
	}
}
