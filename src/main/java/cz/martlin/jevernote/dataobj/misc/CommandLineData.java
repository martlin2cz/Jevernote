package cz.martlin.jevernote.dataobj.misc;

import java.io.File;

public class CommandLineData {
	private boolean verbose;
	private boolean debug;
	private boolean interactive;
	private boolean dryRun;
	private boolean save;

	private File baseDir;
	private String command;

	private boolean weak;
	private boolean force;
	private String remoteToken;

	private String firstItemOrPack;
	private String secondItemOrPack;
	private String initialText;

	public CommandLineData() {
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isInteractive() {
		return interactive;
	}

	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}

	public boolean isWeak() {
		return weak;
	}

	public void setWeak(boolean weak) {
		this.weak = weak;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public void setRemoteToken(String remoteToken) {
		this.remoteToken = remoteToken;
	}

	public String getRemoteToken() {
		return remoteToken;
	}

	public String getFirstItemOrPack() {
		return firstItemOrPack;
	}

	public void setFirstItemOrPack(String firstItemOrPack) {
		this.firstItemOrPack = firstItemOrPack;
	}

	public String getSecondItemOrPack() {
		return secondItemOrPack;
	}

	public void setSecondItemOrPack(String secondItemOrPack) {
		this.secondItemOrPack = secondItemOrPack;
	}

	public String getInitialText() {
		return initialText;
	}

	public void setInitialText(String initialText) {
		this.initialText = initialText;
	}

	@Override
	public String toString() {
		return "CommandLineData [verbose=" + verbose + ", debug=" + debug + ", interactive=" + interactive + ", dryRun="
				+ dryRun + ", save=" + save + ", baseDir=" + baseDir + ", command=" + command + ", weak=" + weak
				+ ", force=" + force + ", remoteToken=" + remoteToken + ", firstItemOrPack=" + firstItemOrPack
				+ ", secondItemOrPack=" + secondItemOrPack + ", initialText=" + initialText + "]";
	}

}
