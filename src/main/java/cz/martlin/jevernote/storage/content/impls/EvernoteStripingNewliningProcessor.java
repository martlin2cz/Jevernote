package cz.martlin.jevernote.storage.content.impls;

public class EvernoteStripingNewliningProcessor extends EvernoteStrippingProcessor {

	public EvernoteStripingNewliningProcessor() {
	}

	@Override
	public String fromStorage(String content) {
		String stripped = super.fromStorage(content);
		return replaceBRsByNewlines(stripped);
	}

	@Override
	public String toStorage(String content) {
		String newlined = replaceNewlinesByBRs(content);
		return super.toStorage(newlined);
	}

	///////////////////////////////////////////////////////////////////////////

	protected static String replaceBRsByNewlines(String stripped) {
		return stripped.replaceAll("\\<br/\\>", "\n");
	}

	protected static String replaceNewlinesByBRs(String content) {
		return content.replaceAll("\n", "<br/>");
	}
}
