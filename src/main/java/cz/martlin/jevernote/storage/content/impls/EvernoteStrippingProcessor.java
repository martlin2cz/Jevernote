package cz.martlin.jevernote.storage.content.impls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.martlin.jevernote.storage.content.base.ContentProcessor;

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
		final String noteStartTagRegex = "<en-note([^>]*)>";
		final String noteEndTag = "</en-note>";

		Pattern startTagPattern = Pattern.compile(noteStartTagRegex);
		Matcher startTagMatcher = startTagPattern.matcher(content);
		
		startTagMatcher.find();
		int startCut = startTagMatcher.end();

		int endIndex = content.lastIndexOf(noteEndTag);
		int endCut = endIndex;

		return content.substring(startCut, endCut);
	}

}
