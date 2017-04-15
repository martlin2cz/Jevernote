package cz.martlin.jevernote.storage.base;

import java.util.List;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;

public abstract class WrappingStorage implements BaseStorage {

	public WrappingStorage() {
	}

	public abstract BaseStorage getWrapped();

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void install() throws JevernoteException {
		// nothing by default
	}

	@Override
	public void uninstall() throws JevernoteException {
		// nothing by default
	}

	@Override
	public void initialize() throws JevernoteException {
		// nothing by default
	}

	@Override
	public void finish() throws JevernoteException {
		// nothing by default
	}

	///////////////////////////////////////////////////////////////////////////

	public StorageData list() throws JevernoteException {
		return getWrapped().list();
	}

	public List<Package> listPackages() throws JevernoteException {
		return getWrapped().listPackages();
	}

	public List<Item> listItems(Package pack) throws JevernoteException {
		return getWrapped().listItems(pack);
	}

	public void createPackage(Package pack) throws JevernoteException {
		getWrapped().createPackage(pack);
	}

	public void createItem(Item item) throws JevernoteException {
		getWrapped().createItem(item);
	}

	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		getWrapped().movePackage(oldPack, newPack);
	}

	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		getWrapped().moveItem(oldItem, newItem);
	}

	public void updateItem(Item item) throws JevernoteException {
		getWrapped().updateItem(item);
	}

	public void removePackage(Package pack) throws JevernoteException {
		getWrapped().removePackage(pack);
	}

	public void removeItem(Item item) throws JevernoteException {
		getWrapped().removeItem(item);
	}

}
