package cz.martlin.jevernote.storage.impls;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;

public class LoggingStorageWrapper implements BaseStorage {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final BaseStorage wrapped;

	public LoggingStorageWrapper(BaseStorage wrapped) {
		this.wrapped = wrapped;

	}

	public StorageData list() throws JevernoteException {
		return wrapped.list();
	}

	public List<Package> listPackages() throws JevernoteException {
		return wrapped.listPackages();
	}

	public List<Item> listItems(Package pack) throws JevernoteException {
		return wrapped.listItems(pack);
	}

	public void createPackage(Package pack) throws JevernoteException {
		LOG.debug("Creating package " + pack.getName());
		wrapped.createPackage(pack);
		LOG.info("Created package " + pack.getName());
	}

	public void createItem(Item item) throws JevernoteException {
		LOG.debug("Creating item " + item.getName());
		wrapped.createItem(item);
		LOG.info("Created item " + item.getName());
	}

	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		LOG.debug("Moving package " + oldPack.getName());
		wrapped.movePackage(oldPack, newPack);
		LOG.info("Moved package " + oldPack.getName());
	}

	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		LOG.debug("Moving item " + oldItem.getName());
		wrapped.moveItem(oldItem, newItem);
		LOG.info("Moved item " + oldItem.getName());
	}

	public void updateItem(Item item) throws JevernoteException {
		LOG.debug("Updating item " + item.getName());
		wrapped.updateItem(item);
		LOG.info("Updated item " + item.getName());
	}

	public void removePackage(Package pack) throws JevernoteException {
		LOG.debug("Removing package " + pack.getName());
		wrapped.removePackage(pack);
		LOG.info("Removed package " + pack.getName());
	}

	public void removeItem(Item item) throws JevernoteException {
		LOG.debug("Removing item " + item.getName());
		wrapped.removeItem(item);
		LOG.info("Removed item " + item.getName());
	}

}
