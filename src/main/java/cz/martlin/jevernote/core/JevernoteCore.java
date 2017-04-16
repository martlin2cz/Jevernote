package cz.martlin.jevernote.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.app.Exporter;
import cz.martlin.jevernote.dataobj.cmp.StoragesDifference;
import cz.martlin.jevernote.diff.core.StoragesDifferencer;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.RequiresLoad;
import cz.martlin.jevernote.perf.base.BaseDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.DiffPerformerUsingStragegies;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.StorageRequiringLoad;
import cz.martlin.jevernote.storage.impls.LoggingStorageWrapper;
import cz.martlin.jevernote.strategy.base.BaseDifferencePerformStrategy;
import cz.martlin.jevernote.strategy.impl.DefaultStrategy;
import cz.martlin.jevernote.strategy.impl.ForceStrategy;
import cz.martlin.jevernote.strategy.impl.WeakStrategy;

public class JevernoteCore implements RequiresLoad {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	protected final BaseStorage local;
	protected final BaseStorage remote;
	private final LoggingStorageWrapper loggingLocal;
	private final LoggingStorageWrapper loggingRemote;

	private boolean loaded;

	public JevernoteCore(BaseStorage local, BaseStorage remote) {
		super();

		this.local = local;
		this.remote = remote;

		loggingLocal = new LoggingStorageWrapper(local);
		loggingRemote = new LoggingStorageWrapper(remote);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void load() throws JevernoteException {
		LOG.debug("Loading core");

		loadStorages();
		loaded = true;

		LOG.debug("Loaded core");
	}

	@Override
	public void store() throws JevernoteException {
		LOG.debug("Storing core");

		storeStorages();
		loaded = false;

		LOG.debug("Stored core");
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	///////////////////////////////////////////////////////////////////////////

	private void loadStorages() throws JevernoteException {
		StorageRequiringLoad.checkAndLoad(local);
		StorageRequiringLoad.checkAndLoad(remote);
	}

	private void storeStorages() throws JevernoteException {
		StorageRequiringLoad.checkAndStore(local);
		StorageRequiringLoad.checkAndStore(remote);
	}

	///////////////////////////////////////////////////////////////////////////

	public void initCmd(String localSpecifier, String remoteSpecifier) throws JevernoteException {
		LOG.debug("Running init command (local specifier = " + localSpecifier + ", remote specifier = "
				+ remoteSpecifier + ")");

		doInit(localSpecifier, remoteSpecifier);

		LOG.debug("Command completed");
	}

	public void cloneCmd(String localSpecifier, String remoteSpecifier) throws JevernoteException {
		LOG.debug("Running clone command (local specifier = " + localSpecifier + ", remote specifier = "
				+ remoteSpecifier + ")");

		doInit(localSpecifier, remoteSpecifier);
		doPull(false, true);

		LOG.debug("Command completed");
	}

	public void pushCmd(boolean weak, boolean force) throws JevernoteException {
		LOG.debug("Running push command (weak? " + weak + ", force? " + force + ")");

		doPush(weak, force);

		LOG.debug("Command completed");
	}

	public void pullCmd(boolean weak, boolean force) throws JevernoteException {
		LOG.debug("Running pull command (weak? " + weak + ", force? " + force + ")");

		doPull(weak, force);

		LOG.debug("Command completed");
	}

	public void synchronizeCmd(boolean preferLocal) throws JevernoteException {
		LOG.debug("Running synchronize command (prefer local? " + preferLocal + ")");

		if (preferLocal) {
			doPull(true, false);
			doPush(false, true);
		} else {
			doPush(true, false);
			doPull(false, true);
		}

		LOG.debug("Command completed");
	}

	public void statusCmd() throws JevernoteException {
		doStatus();
	}

	///////////////////////////////////////////////////////////////////////////

	private void doInit(String localSpecifier, String remoteSpecifier) throws JevernoteException {
		loggingLocal.initialize(localSpecifier);
		loggingRemote.initialize(remoteSpecifier);
	}

	private void doPull(boolean weak, boolean force) throws JevernoteException {
		transfer(loggingRemote, loggingLocal, weak, force);
	}

	private void doPush(boolean weak, boolean force) throws JevernoteException {
		transfer(loggingLocal, loggingRemote, weak, force);
	}

	private void doStatus() throws JevernoteException {
		StoragesDifferencer differ = new StoragesDifferencer();

		StoragesDifference diff = differ.compute(local, remote);
		Exporter export = new Exporter();
		LOG.info(export.exportDiff(diff));

	}
	///////////////////////////////////////////////////////////////////////////

	private void transfer(BaseStorage source, BaseStorage target, boolean weak, boolean force)
			throws JevernoteException {

		StoragesDifferencer differ = new StoragesDifferencer();

		StoragesDifference diff = differ.compute(target, source);

		BaseDifferencePerformStrategy strategy = findStrategy(weak, force);
		BaseDifferencesPerformer perf = new DiffPerformerUsingStragegies(target, strategy);

		perf.performDifferences(diff);
	}

	private BaseDifferencePerformStrategy findStrategy(boolean weak, boolean force) {

		if (weak) {
			return new WeakStrategy();
		}

		if (force) {
			return new ForceStrategy();
		}

		return new DefaultStrategy();
	}
}
