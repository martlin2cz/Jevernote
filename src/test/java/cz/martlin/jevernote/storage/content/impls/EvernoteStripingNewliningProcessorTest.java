package cz.martlin.jevernote.storage.content.impls;

import static org.junit.Assert.*;

import org.junit.Test;

public class EvernoteStripingNewliningProcessorTest {

	private final EvernoteStrippingNewliningProcessor proc = new EvernoteStrippingNewliningProcessor();

	public EvernoteStripingNewliningProcessorTest() {
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testFromEvernote() {
		final String input = "" //
				+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
				+ "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">" //
				+ "<en-note>" //
				+ "<span>" //
				+ "Hello,<br />" //
				+ "World!<br />" //
				+ "</span>" //
				+ "</en-note>"; //

		final String expectedOut = "" //
				+ "<span>" //
				+ "Hello,\n" //
				+ "World!\n" //
				+ "</span>";

		final String actualOut = proc.fromStorage(input);

		assertEquals(expectedOut, actualOut);

	}

	@Test
	public void testToEvernote() {

		final String input = "" //
				+ "<span>" //
				+ "Hello,\n" //
				+ "World!\n" //
				+ "</span>";

		final String expectedOut = "" //
				+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
				+ "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">" //
				+ "<en-note>" //
				+ "<span>" //
				+ "Hello,<br />" //
				+ "World!<br />" //
				+ "</span>" //
				+ "</en-note>"; //

		final String actualOut = proc.toStorage(input);
		
		assertEquals(expectedOut, actualOut);
	}

}
