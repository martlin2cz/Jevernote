package cz.martlin.jevernote.storage.impls;

import cz.martlin.jevernote.storage.base.ContentProcessor;

public class EvernoteStrippingProcessor implements ContentProcessor {

	public EvernoteStrippingProcessor() {
	}

	@Override
	public String toStorage(String content) {
		return //
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
				+ "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">" //
				+ "<en-note>" //
				+ content //
				+ "</en-note>"; //
	}

	@Override
	public String fromStorage(String content) {
		final String noteStartTag = "<en-note>";
		final String noteEndTag = "</en-note>";

		int startIndex = content.indexOf(noteStartTag);
		int startCut = startIndex + noteStartTag.length();

		int endIndex = content.lastIndexOf(noteEndTag);
		int endCut = endIndex;

		return content.substring(startCut, endCut);
	}

}
