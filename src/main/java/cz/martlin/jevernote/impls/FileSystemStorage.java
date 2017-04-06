package cz.martlin.jevernote.impls;

import java.util.List;
import java.util.Map;

import cz.martlin.jevernote.core.BaseStorage;
import cz.martlin.jevernote.core.JevernoteException;
import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;

public class FileSystemStorage implements BaseStorage {

	@Override
	public Map<Package, List<Item>> list() throws JevernoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Package> listPackages() throws JevernoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> listItems(Package pack) throws JevernoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createPackage(Package pack) throws JevernoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createItem(Package pack, Item item) throws JevernoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePackage(Package pack) throws JevernoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateItem(Item item, Package pack) throws JevernoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		// TODO Auto-generated method stub

	}

}
