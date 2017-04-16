package cz.martlin.jevernote.storage.content.impls;

import cz.martlin.jevernote.storage.content.base.ContentProcessor;

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
