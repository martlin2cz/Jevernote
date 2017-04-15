package cz.martlin.jevernote.storage.base;

public interface ContentProcessor {

	public String toStorage(String content);

	public String fromStorage(String content);

}
