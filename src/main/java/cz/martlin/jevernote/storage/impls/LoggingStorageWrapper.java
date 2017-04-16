package cz.martlin.jevernote.storage.impls;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.WrappingStorage;

public class LoggingStorageWrapper extends WrappingStorage {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final BaseStorage wrapped;

	public LoggingStorageWrapper(BaseStorage wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public BaseStorage getWrapped() {
		return wrapped;
	}

	@Override
	public void initialize(String storageDesc) throws JevernoteException {
		super.initialize(storageDesc);
		
	}
	
	///////////////////////////////////////////////////////////////////////////

	public StorageData list() throws JevernoteException {
		return super.list();
	}

	public List<Package> listPackages() throws JevernoteException {
		return super.listPackages();
	}

	public List<Item> listItems(Package pack) throws JevernoteException {
		return super.listItems(pack);
	}

	///////////////////////////////////////////////////////////////////////////

	public void createPackage(Package pack) throws JevernoteException {
		LOG.debug("Creating package " + pack.getName());
		super.createPackage(pack);
		LOG.info("Created package " + pack.getName());
	}

	public void createItem(Item item) throws JevernoteException {
		LOG.debug("Creating item " + item.getName());
		super.createItem(item);
		LOG.info("Created item " + item.getName());
	}

	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		LOG.debug("Moving package " + oldPack.getName());
		super.movePackage(oldPack, newPack);
		LOG.info("Moved package " + oldPack.getName());
	}

	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		LOG.debug("Moving item " + oldItem.getName());
		super.moveItem(oldItem, newItem);
		LOG.info("Moved item " + oldItem.getName());
	}

	public void updateItem(Item item) throws JevernoteException {
		LOG.debug("Updating item " + item.getName());
		super.updateItem(item);
		LOG.info("Updated item " + item.getName());
	}

	public void removePackage(Package pack) throws JevernoteException {
		LOG.debug("Removing package " + pack.getName());
		super.removePackage(pack);
		LOG.info("Removed package " + pack.getName());
	}

	public void removeItem(Item item) throws JevernoteException {
		LOG.debug("Removing item " + item.getName());
		super.removeItem(item);
		LOG.info("Removed item " + item.getName());
	}

}
