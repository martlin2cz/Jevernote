package cz.martlin.jevernote.storage.impls;

import java.util.List;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.RequiresLoad;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.WrappingStorage;

public class ReadOnlyStorage //
		extends WrappingStorage //
		implements RequiresLoad {

	public ReadOnlyStorage(BaseStorage source) {
		super(source);
	}

	@Override
	public void initialize(String storageDesc) throws JevernoteException {
		// nop
	}

	@Override
	public StorageData list() throws JevernoteException {
		return super.list();
	}

	@Override
	public List<Package> listPackages() throws JevernoteException {
		return super.listPackages();
	}

	@Override
	public List<Item> listItems(Package pack) throws JevernoteException {
		return super.listItems(pack);
	}

	// TODO at least print? LOG or stdout? or what?!

	@Override
	public void createPackage(Package pack) throws JevernoteException {
		// nop
	}

	@Override
	public void createItem(Item item) throws JevernoteException {
		// nop
	}

	@Override
	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		// nop
	}

	@Override
	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		// nop
	}

	@Override
	public void updateItem(Item item) throws JevernoteException {
		// nop
	}

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		// nop
	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		// nop
	}

	@Override
	public void backupPackage(Package pack) throws JevernoteException {
		// nop
	}

	@Override
	public void backupItem(Item item) throws JevernoteException {
		// nop
	}

}
