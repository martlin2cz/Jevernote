package cz.martlin.jevernote.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import cz.martlin.jevernote.strategy.impl.AndStrategy;
import cz.martlin.jevernote.strategy.impl.DefaultStrategy;
import cz.martlin.jevernote.strategy.impl.ForceStrategy;
import cz.martlin.jevernote.strategy.impl.InteractiveStrategy;
import cz.martlin.jevernote.strategy.impl.SynchronizeStrategy;
import cz.martlin.jevernote.strategy.impl.WeakStrategy;

public class JevernoteCore implements RequiresLoad {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	protected final BaseStorage local;
	protected final BaseStorage remote;
	private final LoggingStorageWrapper loggingLocal;
	private final LoggingStorageWrapper loggingRemote;
	private final boolean interactive;

	private boolean loaded;

	public JevernoteCore(BaseStorage local, BaseStorage remote, boolean interactive) {
		super();

		this.local = local;
		this.remote = remote;

		loggingLocal = new LoggingStorageWrapper(local);
		loggingRemote = new LoggingStorageWrapper(remote);

		this.interactive = interactive;
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

	public void synchronizeCmd() throws JevernoteException {
		LOG.debug("Running synchronize command");

		doSyncrhonize();

		LOG.debug("Command completed");
	}

	///////////////////////////////////////////////////////////////////////////

	private void doInit(String localSpecifier, String remoteSpecifier) throws JevernoteException {
		loggingLocal.initialize(localSpecifier);
		loggingRemote.initialize(remoteSpecifier);
	}

	private void doPull(boolean weak, boolean force) throws JevernoteException {
		transfer(loggingRemote, loggingLocal, weak, force, "at local");
	}

	private void doPush(boolean weak, boolean force) throws JevernoteException {
		transfer(loggingLocal, loggingRemote, weak, force, "at remote");
	}

	private void doSyncrhonize() throws JevernoteException {
		BaseDifferencePerformStrategy strategy = new SynchronizeStrategy();

		transfer(loggingLocal, loggingRemote, strategy, "at remote");
		transfer(loggingRemote, loggingLocal, strategy, "at local");
	}

	///////////////////////////////////////////////////////////////////////////

	private void transfer(BaseStorage source, BaseStorage target, boolean weak, boolean force, String desc)
			throws JevernoteException {

		BaseDifferencePerformStrategy strategy = findStrategy(weak, force);
		transfer(source, target, strategy, desc);
	}

	private void transfer(BaseStorage source, BaseStorage target, BaseDifferencePerformStrategy strategy, String desc)
			throws JevernoteException {

		strategy = tryMakeInteractive(desc, strategy);

		StoragesDifferencer differ = new StoragesDifferencer();
		BaseDifferencesPerformer perf = new DiffPerformerUsingStragegies(target, strategy);

		StoragesDifference diff = differ.compute(target, source);
		perf.performDifferences(diff);
	}

	///////////////////////////////////////////////////////////////////////////

	private BaseDifferencePerformStrategy findStrategy(boolean weak, boolean force) {

		if (weak) {
			return new WeakStrategy();
		}

		if (force) {
			return new ForceStrategy();
		}

		return new DefaultStrategy();
	}

	private BaseDifferencePerformStrategy tryMakeInteractive(String desc, BaseDifferencePerformStrategy baseStrategy) {
		if (interactive) {
			BaseDifferencePerformStrategy interactiveStrategy = new InteractiveStrategy(desc);
			return new AndStrategy(baseStrategy, interactiveStrategy);
		} else {
			return baseStrategy;
		}
	}
}
