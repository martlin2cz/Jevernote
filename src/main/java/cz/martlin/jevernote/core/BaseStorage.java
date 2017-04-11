package cz.martlin.jevernote.core;

import java.util.List;
import java.util.Map;

import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;
import cz.martlin.jevernote.misc.JevernoteException;

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
	 * Creates package (assuming package does not exist).
	 * 
	 * @param pack
	 *
	 * @throws JevernoteException
	 */
	void createPackage(Package pack) throws JevernoteException;

	/**
	 * Creates item in given package (assuming package yet exists, but the item
	 * not).
	 * 
	 * @param item
	 * 
	 * @throws JevernoteException
	 */
	void createItem(Item item) throws JevernoteException;

	/**
	 * Renames (in fact just renames) oldPack to newPack (assuming old package
	 * exists and the new one not).
	 * 
	 * @param oldPack
	 * 
	 * @param newPack
	 * 
	 * @throws JevernoteException
	 */
	void movePackage(Package oldPack, Package newPack) throws JevernoteException;

	/**
	 * Renames/moves (moves into another package, renames, or both) oldItem to
	 * newItem (assuming old item exists and new one not (but new package
	 * must)).
	 * 
	 * @param oldItem
	 * @param newItem
	 * @param pack
	 * 
	 * @throws JevernoteException
	 */
	void moveItem(Item oldItem, Item newItem) throws JevernoteException;

	/**
	 * Updates item's content (item must exist).
	 * 
	 * @param item
	 * @param pack
	 * @throws JevernoteException
	 */
	void updateItem(Item item) throws JevernoteException;

	/**
	 * Removes package (package must exist, but empty).
	 * 
	 * @param pack
	 * @throws JevernoteException
	 */
	void removePackage(Package pack) throws JevernoteException;

	/**
	 * Removes item (item must exist).
	 * 
	 * @param item
	 * @throws JevernoteException
	 */
	void removeItem(Item item) throws JevernoteException;

}