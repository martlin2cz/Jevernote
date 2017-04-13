package cz.martlin.jevernote.perf.base;

import java.util.Calendar;
import java.util.List;

import cz.martlin.jevernote.dataobj.cmp.Change;
import cz.martlin.jevernote.dataobj.cmp.Change.ChangeType;
import cz.martlin.jevernote.dataobj.cmp.StoragesDifference;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;

public abstract class BaseDifferencesPerformer {

	protected final BaseStorage source;
	protected final BaseStorage target;

	public BaseDifferencesPerformer(BaseStorage source, BaseStorage target) {
		this.source = source;
		this.target = target;
	}

	///////////////////////////////////////////////////////////////////////////

	public void performDifferences(StoragesDifference difference) throws JevernoteException {
		List<Change<Package>> packageDifferences = difference.getPackageDifferences();
		List<Change<Item>> itemsDifferences = difference.getItemsDifferences();

		performPackageChanges(ChangeType.CREATE, packageDifferences);

		performItemsChanges(ChangeType.CREATE, itemsDifferences);
		performItemsChanges(ChangeType.RENAME, itemsDifferences);
		performItemsChanges(ChangeType.UPDATE, itemsDifferences);
		performItemsChanges(ChangeType.DELETE, itemsDifferences);

		performPackageChanges(ChangeType.RENAME, packageDifferences);
		performPackageChanges(ChangeType.DELETE, packageDifferences);

	}

	///////////////////////////////////////////////////////////////////////////

	private void performPackageChanges(ChangeType type, List<Change<Package>> packageDifferences)
			throws JevernoteException {
		try {
			packageDifferences //
					.stream() //
					.filter((c) -> c.is(type)) //
					.forEach((c) -> { //
						try {
							performPackageChange(c);
						} catch (JevernoteException e) {
							throw new RuntimeException(e);
						}
					});
		} catch (RuntimeException e) {
			throw new JevernoteException(e);
		}
	}

	private void performItemsChanges(ChangeType type, List<Change<Item>> itemsDifferences) throws JevernoteException {
		try {
			itemsDifferences //
					.stream() //
					.filter((c) -> c.is(type)) //
					.forEach((c) -> { //
						try {
							performItemChange(c);
						} catch (JevernoteException e) {
							throw new RuntimeException(e);
						}
					});
		} catch (RuntimeException e) {
			throw new JevernoteException(e);
		}
	}

	private void performItemChange(Change<Item> change) throws JevernoteException {
		switch (change.getType()) {
		case CREATE:
			performCreateItem(change);
			break;
		case DELETE:
			performRemoveItem(change);
			break;
		case RENAME:
			performRenameItem(change);
			break;
		case UPDATE:
			performUpdateItem(change);
			break;
		default:
			throw new IllegalArgumentException("Unknown change type " + change.getType());
		}
	}

	private void performPackageChange(Change<Package> change) throws JevernoteException {
		switch (change.getType()) {
		case CREATE:
			performCreatePackage(change);
			break;
		case DELETE:
			performDeletePackage(change);
			break;
		case RENAME:
			performRenamePackage(change);
			break;
		case UPDATE:
			throw new UnsupportedOperationException("update of package");
		default:
			throw new IllegalArgumentException("Unknown change type " + change.getType());
		}
	}

	///////////////////////////////////////////////////////////////////////////

	protected abstract void performCreatePackage(Change<Package> change) throws JevernoteException;

	protected abstract void performRenamePackage(Change<Package> change) throws JevernoteException;

	protected abstract void performDeletePackage(Change<Package> change) throws JevernoteException;

	protected abstract void performCreateItem(Change<Item> change) throws JevernoteException;

	protected abstract void performRenameItem(Change<Item> change) throws JevernoteException;

	protected abstract void performUpdateItem(Change<Item> change) throws JevernoteException;

	protected abstract void performRemoveItem(Change<Item> change) throws JevernoteException;

	///////////////////////////////////////////////////////////////////////////

	public static boolean isToNewer(Change<Item> change) {
		Calendar oldDate = change.getFirst().getLastModifiedAt();
		Calendar newDate = change.getFirst().getLastModifiedAt();

		return oldDate.compareTo(newDate) < 0; // TODO TESTME
	}

}
