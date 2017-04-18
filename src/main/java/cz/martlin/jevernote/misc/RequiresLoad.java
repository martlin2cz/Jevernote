package cz.martlin.jevernote.misc;

/**
 * Represents (abstract) something, which needs to be initialized before each
 * use. Also, specifies that before first use (persintently) has to (or just
 * simply should) be installed.
 * 
 * @author martin
 *
 * @param <T>
 */
public interface RequiresLoad<T> {

	/**
	 * Returns true whether is this object installed.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isInstalled() throws Exception;

	/**
	 * Does the instalation based on given instal data. Also, after finish of
	 * this method object MUST be loaded (same as after {@link #load()}).
	 * 
	 * @param installData
	 * @throws Exception
	 */
	public void installAndLoad(T installData) throws Exception;

	/**
	 * Does the particullar load. This load makes object usable and after work
	 * is completed, should be finished by calling {@link #store()}. This method
	 * should do some configuration loading or resources allocation.
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception;

	/**
	 * Does some "store", the opposite of {@link #load()}. For example, some
	 * storing of loaded data/config or disconnection.
	 * 
	 * @throws Exception
	 */
	public void store() throws Exception;

	/**
	 * Returns true, if this service have been yet loaded (was invoked method
	 * {@link #load()}, but not yet {@link #store()}).
	 * 
	 * @return
	 */
	public boolean isLoaded();

}
