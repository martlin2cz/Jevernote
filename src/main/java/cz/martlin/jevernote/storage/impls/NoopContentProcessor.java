package cz.martlin.jevernote.storage.impls;

import cz.martlin.jevernote.storage.base.ContentProcessor;

public class NoopContentProcessor implements ContentProcessor {

	public NoopContentProcessor() {
	}

	@Override
	public String toStorage(String content) {
		return content;
	}

	@Override
	public String fromStorage(String content) {
		return content;
	}

}
