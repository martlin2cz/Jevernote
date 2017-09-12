package cz.martlin.jevernote.storage.base;

import java.util.List;

import cz.martlin.jevernote.dataobj.cmp.Change;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;

public abstract class WrappingStorage implements BaseStorage {

	private final BaseStorage wrapped;

	public WrappingStorage(BaseStorage wrapped) {
		this.wrapped = wrapped;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void initialize(String storageDesc) throws JevernoteException {
		wrapped.initialize(storageDesc);
	}

	@Override
	public StorageData list() throws JevernoteException {
		return wrapped.list();
	}

	@Override
	public List<Package> listPackages() throws JevernoteException {
		return wrapped.listPackages();
	}

	@Override
	public List<Item> listItems(Package pack) throws JevernoteException {
		return wrapped.listItems(pack);
	}

	@Override
	public void createPackage(Package pack) throws JevernoteException {
		wrapped.createPackage(pack);
	}

	@Override
	public void createItem(Item item) throws JevernoteException {
		wrapped.createItem(item);
	}

	@Override
	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		wrapped.movePackage(oldPack, newPack);
	}

	@Override
	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		wrapped.moveItem(oldItem, newItem);
	}

	@Override
	public void updateItem(Item item) throws JevernoteException {
		wrapped.updateItem(item);
	}

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		wrapped.removePackage(pack);
	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		wrapped.removeItem(item);
	}

	@Override
	public void backupPackage(Package pack) throws JevernoteException {
		wrapped.backupPackage(pack);
	}

	@Override
	public void backupItem(Item item) throws JevernoteException {
		wrapped.backupItem(item);
	}

	@Override
	public void donePackChangeOnAnother(Change<Package> change) throws JevernoteException {
		wrapped.donePackChangeOnAnother(change);
	}

	@Override
	public void doneItemChangeOnAnother(Change<Item> change) throws JevernoteException {
		wrapped.doneItemChangeOnAnother(change);
	}

	@Override
	public String toString() {
		return "WrappingStorage [wrapped=" + wrapped + "]";
	}

	///////////////////////////////////////////////////////////////////////////

	
	
}
