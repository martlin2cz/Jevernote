package cz.martlin.jevernote.storage.content.impls;

import java.util.List;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.RequiresLoad;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.StorageRequiringLoad;
import cz.martlin.jevernote.storage.base.WrappingStorage;

public class ReadOnlyStorage //
		extends WrappingStorage //
		implements RequiresLoad {

	private final BaseStorage source;
	private boolean loaded;

	public ReadOnlyStorage(BaseStorage source) {
		this.source = source;
	}

	
	@Override
	public BaseStorage getWrapped() {
		return source;
	}

	@Override
	public void load() throws Exception {
		StorageRequiringLoad.checkAndLoad(source);
		loaded = true;
	}

	@Override
	public void store() throws Exception {
		StorageRequiringLoad.checkAndStore(source);
		loaded = false;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void initialize(String storageDesc) throws JevernoteException {
		// nop
	}

	@Override
	public StorageData list() throws JevernoteException {
		return source.list();
	}

	@Override
	public List<Package> listPackages() throws JevernoteException {
		return source.listPackages();
	}

	@Override
	public List<Item> listItems(Package pack) throws JevernoteException {
		return source.listItems(pack);
	}

	//TODO at least print? LOG or stdout? or what?!
	
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

}
