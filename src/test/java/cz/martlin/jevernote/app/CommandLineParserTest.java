package cz.martlin.jevernote.app;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.martlin.jevernote.dataobj.misc.CommandLineData;

public class CommandLineParserTest {

	private final CommandLineParser parser = new CommandLineParser();

	@Test
	public void testClone() {

		String[] input = new String[] { "--verbose", "--debug", "clone", "12345" };
		CommandLineData data = parser.parse(input);

		assertNotNull(data);
		assertTrue(data.isDebug());
		assertTrue(data.isVerbose());
		assertEquals("clone", data.getCommand());
		assertEquals("12345", data.getRemoteToken());
	}

	@Test
	public void testPush() {

		String[] input = new String[] { "--verbose", "push", "--weak" };
		CommandLineData data = parser.parse(input);

		assertNotNull(data);
		assertFalse(data.isDebug());
		assertTrue(data.isVerbose());
		assertEquals("push", data.getCommand());
		assertTrue(data.isWeak());
		assertFalse(data.isForce());
	}

	@Test
	public void testOtherOkays() {
		check(true, "--verbose", "--debug", "--interactive", "push", "--weak", "--force");
		check(true, "--verbose", "push", "--weak");
		check(true, "synchronize");
	}
	
	@Test
	public void testWithWarning() {
		check(true, "--undefined", "push", "--weak", "--force", "--dunno");
		check(true, "init", "123", "456");
		check(true, "-verbose", "push", "-force");
	}
	

	@Test
	public void testIncorrects() {
		check(false);
		check(false, "make-world-peace", "--now");
		check(false, "clone");
		check(false, "synchronize", "--force");
	}
	
	
	@Test
	public void testLocals() {
		check(true,  "ad", "the-existing-item");
		check(true,  "mk", "the-new-item");
		check(true,  "mk", "the-new-item", "This is the content.");
		check(true,  "mv", "the-old-item", "the-new-item");
		check(true,  "rm", "the-item-to-remove");
	}
	

	private void check(boolean isOk, String... input) {
		CommandLineData data = parser.parse(input);
		
		if (isOk) {
			assertNotNull(data);
		} else {
			assertNull(data);
		}
	}

}
