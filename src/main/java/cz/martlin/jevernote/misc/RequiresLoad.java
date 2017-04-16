package cz.martlin.jevernote.misc;

public interface RequiresLoad {

	public void load() throws Exception;
	public void store() throws Exception;
	
	public boolean isLoaded();
	
}
