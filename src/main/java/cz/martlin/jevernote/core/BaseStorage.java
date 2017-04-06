package cz.martlin.jevernote.core;

import java.util.List;
import java.util.Map;

import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;

/**
 * Base storage implementation. Specifies loading, creating, updating and
 * removing of packages and items.
 * 
 * @author martin
 *
 */
public interface BaseStorage {

	/**
	 * Lists all items in all packages.
	 * 
	 * @return
	 * @throws JevernoteException
	 */
	Map<Package, List<Item>> list() throws JevernoteException;

	/**
	 * Lists all packages.
	 * 
	 * @return
	 * @throws JevernoteException
	 */
	List<Package> listPackages() throws JevernoteException;

	/**
	 * Lists items in given package.
	 * 
	 * @param pack
	 * @return
	 * @throws JevernoteException
	 */
	List<Item> listItems(Package pack) throws JevernoteException;

	/**
	 * Creates package.
	 * 
	 * @param pack
	 *
	 * @throws JevernoteException
	 */
	void createPackage(Package pack) throws JevernoteException;

	/**
	 * Creates item in given package.
	 * 
	 * @param item
	 * 
	 * @throws JevernoteException
	 */
	void createItem(Package pack, Item item) throws JevernoteException;

	/**
	 * Updates package.
	 * 
	 * @param pack
	 * @throws JevernoteException
	 */
	void updatePackage(Package pack) throws JevernoteException;

	/**
	 * Updates item (and optionally moves into another package if specified).
	 * 
	 * @param item
	 * @param pack
	 *            TODO
	 * @throws JevernoteException
	 */
	void updateItem(Item item, Package pack) throws JevernoteException;

	/**
	 * Removes package.
	 * 
	 * @param pack
	 * @throws JevernoteException
	 */
	void removePackage(Package pack) throws JevernoteException;

	/**
	 * Removes item.
	 * 
	 * @param item
	 * @throws JevernoteException
	 */
	void removeItem(Item item) throws JevernoteException;

}