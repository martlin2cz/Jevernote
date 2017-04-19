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
import cz.martlin.jevernote.storage.impls.ReadOnlyStorage;
import cz.martlin.jevernote.strategy.base.BaseOperationsStrategy;
import cz.martlin.jevernote.strategy.impl.backup.NoBackupStrategy;
import cz.martlin.jevernote.strategy.impl.backup.StrongBackupStrategy;
import cz.martlin.jevernote.strategy.impl.backup.WeakBackupStrategy;
import cz.martlin.jevernote.strategy.impl.misc.AndStrategy;
import cz.martlin.jevernote.strategy.impl.operations.DefaultOperationsStrategy;
import cz.martlin.jevernote.strategy.impl.operations.ForceOperationsStrategy;
import cz.martlin.jevernote.strategy.impl.operations.InteractiveOperationsStrategy;
import cz.martlin.jevernote.strategy.impl.operations.SynchronizeOperationsStrategy;
import cz.martlin.jevernote.strategy.impl.operations.WeakOperationsStrategy;

public class JevernoteCore implements RequiresLoad<String> {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final Exporter exporter;
	protected final BaseStorage local;
	protected final BaseStorage remote;
	private final BaseStorage wrappedLocal;
	private final BaseStorage wrappedRemote;
	private final boolean interactive;
	private final boolean save;

	private boolean loaded;

	public JevernoteCore(BaseStorage local, BaseStorage remote, boolean interactive, boolean save, boolean dryRun) {
		super();

		this.exporter = new Exporter();

		this.local = local;
		this.remote = remote;

		wrappedLocal = wrapStorage(local, dryRun);
		wrappedRemote = wrapStorage(remote, dryRun);

		this.interactive = interactive;
		this.save = save;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isInstalled() throws JevernoteException {
		return StorageRequiringLoad.isInstalled(local) && StorageRequiringLoad.isInstalled(remote);
	}

	@Override
	public void installAndLoad(String installData) throws Exception {
		throw new UnsupportedOperationException("Cannot directly install core, use initCmd instead");
	}

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
		StorageRequiringLoad.loadIfRequired(local);
		StorageRequiringLoad.loadIfRequired(remote);
	}

	private void storeStorages() throws JevernoteException {
		StorageRequiringLoad.storeIfRequired(local);
		StorageRequiringLoad.storeIfRequired(remote);
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

		doSynchronize();

		LOG.debug("Command completed");
	}

	public void statusCmd() throws JevernoteException {
		LOG.debug("Running status command");

		doStatus();

		LOG.debug("Command completed");
	}

	///////////////////////////////////////////////////////////////////////////

	private void doInit(String localSpecifier, String remoteSpecifier) throws JevernoteException {
		wrappedLocal.initialize(localSpecifier);
		wrappedRemote.initialize(remoteSpecifier);
	}

	private void doPull(boolean weak, boolean force) throws JevernoteException {
		transfer(wrappedRemote, wrappedLocal, weak, force, save, false, "at local");
	}

	private void doPush(boolean weak, boolean force) throws JevernoteException {
		transfer(wrappedLocal, wrappedRemote, weak, force, save, false, "at remote");
	}

	private void doSynchronize() throws JevernoteException {
		BaseOperationsStrategy operationsStrategy = new SynchronizeOperationsStrategy();
		BaseOperationsStrategy backupStrategy = findBackupStrategy(save, false);

		transfer(wrappedLocal, wrappedRemote, operationsStrategy, backupStrategy, "at remote");
		transfer(wrappedRemote, wrappedLocal, operationsStrategy, backupStrategy, "at local");
	}

	private void doStatus() throws JevernoteException {
		StoragesDifferencer differ = new StoragesDifferencer();
		StoragesDifference changes = differ.compute(wrappedRemote, wrappedLocal);

		LOG.info("Changes between local and remote:\n" + exporter.exportDiff(changes));
	}

	///////////////////////////////////////////////////////////////////////////

	private void transfer(BaseStorage source, BaseStorage target, boolean weak, boolean force, boolean weakSave,
			boolean strongSave, String desc) throws JevernoteException {

		BaseOperationsStrategy operationsStrategy = findOperationsStrategy(weak, force);
		BaseOperationsStrategy backupStrategy = findBackupStrategy(weakSave, strongSave);

		transfer(source, target, operationsStrategy, backupStrategy, desc);
	}

	private void transfer(BaseStorage source, BaseStorage target, BaseOperationsStrategy baseOperationsStrategy,
			BaseOperationsStrategy backupStrategy, String desc) throws JevernoteException {

		BaseOperationsStrategy operationsStrategy = tryMakeInteractive(desc, baseOperationsStrategy);

		StoragesDifferencer differ = new StoragesDifferencer();
		BaseDifferencesPerformer perf = new DiffPerformerUsingStragegies(target, operationsStrategy, backupStrategy);

		//StoragesDifference changes = differ.compute(source, target);
		

		StoragesDifference diffToPerform = differ.compute(target, source);
		LOG.info("Has to be done (" + desc + "):\n" + exporter.exportDiff(diffToPerform));
		perf.performDifferences(diffToPerform);
	}

	///////////////////////////////////////////////////////////////////////////

	private BaseStorage wrapStorage(BaseStorage storage, boolean dryRun) {

		storage = new LoggingStorageWrapper(storage);

		if (dryRun) {
			storage = new ReadOnlyStorage(storage); // TODO should'nt be
													// replaced by NoopStrategy?
		}

		return storage;
	}

	private BaseOperationsStrategy findOperationsStrategy(boolean weak, boolean force) {

		if (weak) {
			return new WeakOperationsStrategy();
		}

		if (force) {
			return new ForceOperationsStrategy();
		}

		return new DefaultOperationsStrategy();
	}

	private BaseOperationsStrategy findBackupStrategy(boolean weakSave, boolean strongSave) {
		if (weakSave) {
			return new WeakBackupStrategy();
		}

		if (strongSave) {
			return new StrongBackupStrategy();
		}

		return new NoBackupStrategy();
	}

	private BaseOperationsStrategy tryMakeInteractive(String desc, BaseOperationsStrategy baseStrategy) {
		if (interactive) {
			BaseOperationsStrategy interactiveStrategy = new InteractiveOperationsStrategy(desc);
			return new AndStrategy(baseStrategy, interactiveStrategy);

		} else {
			return baseStrategy;
		}
	}
}
