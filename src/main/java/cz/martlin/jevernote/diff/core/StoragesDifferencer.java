package cz.martlin.jevernote.diff.core;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.martlin.jevernote.dataobj.cmp.Change;
import cz.martlin.jevernote.dataobj.cmp.Change.ChangeType;
import cz.martlin.jevernote.dataobj.cmp.StoragesDifference;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.diff.impl.ItemsDifferencer;
import cz.martlin.jevernote.diff.impl.PackagesDifferencer;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;

public class StoragesDifferencer {

	public StoragesDifferencer() {
		super();
	}

	///////////////////////////////////////////////////////////////////////////

	public StoragesDifference compute(BaseStorage source, BaseStorage target) throws JevernoteException {
		StorageData sourceData;
		try {
			sourceData = source.list();
		} catch (JevernoteException e) {
			throw new JevernoteException("Cannot load data of source (in fact target) storage", e);	//TODO UX naming: this is target!
		}

		StorageData targetData;
		try {
			targetData = target.list();
		} catch (JevernoteException e) {
			throw new JevernoteException("Cannot load data of target (in fact source)  storage", e); //TODO UX naming: this is source!
		}

		return compute(sourceData, targetData);

	}

	protected StoragesDifference compute(StorageData sourceData, StorageData targetData) {
		List<Package> sourcePacks = sourceData.getPackages();
		List<Package> targetPacks = targetData.getPackages();
		List<Item> sourceItems = sourceData.getItems();
		List<Item> targetItems = targetData.getItems();

		PackagesDifferencer packDiff = new PackagesDifferencer();
		List<Change<Package>> packageDifferences = packDiff.computeDiff(sourcePacks, targetPacks);

		Map<Package, Package> renamedPackages = inferRenamedPackages(packageDifferences);

		ItemsDifferencer itemDiff = new ItemsDifferencer(renamedPackages);
		List<Change<Item>> itemsDifferences = itemDiff.computeDiff(sourceItems, targetItems);

		return new StoragesDifference(packageDifferences, itemsDifferences);
	}

	///////////////////////////////////////////////////////////////////////////

	private Map<Package, Package> inferRenamedPackages(List<Change<Package>> packageDifferences) {
		return packageDifferences //
				.stream() //
				.filter((c) -> c.is(ChangeType.RENAME)) //
				.collect(Collectors.toMap( //
						(c) -> c.getFirst(), //
						(c) -> c.getSecond()));
	}

}
