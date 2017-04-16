package cz.martlin.jevernote.storage.content.base;

public interface ContentProcessor {

	public String toStorage(String content);

	public String fromStorage(String content);

}
