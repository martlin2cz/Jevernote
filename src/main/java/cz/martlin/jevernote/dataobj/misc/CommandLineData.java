package cz.martlin.jevernote.dataobj.misc;

import java.io.File;

public class CommandLineData {
	private boolean verbose;
	private boolean debug;
	private boolean interactive;
	private File baseDir;
	private String command;
	private boolean dryRun;
	private boolean save;
	private boolean weak;
	private boolean force;
	private String remoteToken;
	private boolean preferLocal;

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

	public boolean isPreferLocal() {
		return preferLocal;
	}

	public void setPreferLocal(boolean preferLocal) {
		this.preferLocal = preferLocal;
	}

	@Override
	public String toString() {
		return "CommandLineData [verbose=" + verbose + ", debug=" + debug + ", interactive=" + interactive
				+ ", baseDir=" + baseDir + ", command=" + command + ", dryRun=" + dryRun + ", save=" + save + ", weak="
				+ weak + ", force=" + force + ", remoteToken=" + remoteToken + ", preferLocal=" + preferLocal + "]";
	}

}
