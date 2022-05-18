/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.Impact;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamMode;
import org.eclipse.emf.cdo.lm.StreamSpec;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyFactory;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException.Reason;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException.Reason.Conflicting;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException.Reason.Missing;
import org.eclipse.emf.cdo.lm.internal.client.bundle.OM;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.lm.util.LMMerger;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewCommitInfoListener;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OSGiUtil;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.IPasswordCredentials;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.MetadataFactory;
import org.eclipse.equinox.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.query.CollectionResult;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Eike Stepper
 */
public final class SystemDescriptor implements ISystemDescriptor
{
  public static final SystemDescriptor NO_SYSTEM = new SystemDescriptor();

  private static final String KEY_SYSTEM_DESCRIPTOR = ISystemDescriptor.class.getName();

  private static final String KEY_MODULE_NAME = "org.eclipse.emf.cdo.lm.client.ModuleName";

  private static final String PROP_BASELINE_INDEX = "baseline.index";

  private final IListener systemListener = new CDOViewCommitInfoListener()
  {
    @Override
    public void notifyCommitInfo(CDOCommitInfo commitInfo)
    {
      commitInfo.forEachRevisionDelta(revisionDelta -> {
        EClass eClass = revisionDelta.getEClass();

        if (eClass == LMPackage.Literals.SYSTEM)
        {
          forEachListDelta(revisionDelta, LMPackage.Literals.SYSTEM__MODULES, //
              o -> SystemManager.INSTANCE.fireModuleCreatedEvent(SystemDescriptor.this, (Module)o), //
              id -> SystemManager.INSTANCE.fireModuleDeletedEvent(SystemDescriptor.this, id));
        }
        else if (eClass == LMPackage.Literals.MODULE)
        {
          forEachListDelta(revisionDelta, LMPackage.Literals.MODULE__STREAMS, //
              o -> SystemManager.INSTANCE.fireBaselineCreatedEvent(SystemDescriptor.this, (Stream)o), //
              null);
        }
        else if (eClass == LMPackage.Literals.STREAM)
        {
          forEachListDelta(revisionDelta, LMPackage.Literals.STREAM__CONTENTS,
              o -> SystemManager.INSTANCE.fireBaselineCreatedEvent(SystemDescriptor.this, (Baseline)o), //
              null);

          CDOSetFeatureDelta maintenanceBranchDelta = (CDOSetFeatureDelta)revisionDelta.getFeatureDelta(LMPackage.Literals.STREAM__MAINTENANCE_BRANCH);
          if (maintenanceBranchDelta != null)
          {
            CDOID streamID = revisionDelta.getID();
            Stream stream = (Stream)systemView.getObject(streamID);
            String value = (String)maintenanceBranchDelta.getValue();
            CDOBranchRef newBranch = value == null ? null : new CDOBranchRef(value);
            SystemManager.INSTANCE.fireStreamBranchChangedEvent(SystemDescriptor.this, stream, newBranch);
          }
        }
      });
    }

    private void forEachListDelta(CDORevisionDelta revisionDelta, EReference feature, Consumer<CDOObject> additionConsumer, Consumer<CDOID> removalConsumer)
    {
      CDOListFeatureDelta listDelta = (CDOListFeatureDelta)revisionDelta.getFeatureDelta(feature);
      if (listDelta != null)
      {
        for (CDOFeatureDelta listChange : listDelta.getListChanges())
        {
          if (additionConsumer != null && listChange instanceof CDOAddFeatureDelta)
          {
            CDOAddFeatureDelta delta = (CDOAddFeatureDelta)listChange;
            CDOID id = (CDOID)delta.getValue();
            CDOObject newObject = systemView.getObject(id);
            additionConsumer.accept(newObject);
          }
          else if (removalConsumer != null && listChange instanceof CDORemoveFeatureDelta)
          {
            CDORemoveFeatureDelta delta = (CDORemoveFeatureDelta)listChange;
            CDOID id = (CDOID)delta.getValue();
            removalConsumer.accept(id);
          }
        }
      }
    }
  };

  private final CDORepository systemRepository;

  private final String systemName;

  private CDOView systemView;

  private System system;

  private String error;

  private State state = State.Closed;

  private final Map<String, CDORepository> moduleRepositories = new HashMap<>();

  private SystemDescriptor()
  {
    systemRepository = null;
    systemName = null;
  }

  public SystemDescriptor(CDORepository systemRepository, String systemName)
  {
    CheckUtil.checkArg(systemRepository, "systemRepository");
    CheckUtil.checkArg(systemName, "systemName");
    this.systemRepository = systemRepository;
    this.systemName = systemName;
  }

  @Override
  public CDORepository getSystemRepository()
  {
    return systemRepository;
  }

  @Override
  public String getSystemName()
  {
    return systemName;
  }

  @Override
  public System getSystem()
  {
    return system;
  }

  @Override
  public String getError()
  {
    return error;
  }

  @Override
  public State getState()
  {
    return state;
  }

  @Override
  public boolean isOpen()
  {
    return system != null;
  }

  @Override
  public void open()
  {
    boolean opened = false;
    System newSystem = null;

    synchronized (this)
    {
      if (state == State.Closed)
      {
        try
        {
          state = State.Opening;
          system = null;

          CDOSession systemSession = systemRepository.acquireSession();
          systemSession.properties().put(KEY_SYSTEM_DESCRIPTOR, this);

          systemView = systemSession.openView();
          systemView.addListener(systemListener);

          CDOResource resource = systemView.getResource(System.RESOURCE_PATH);
          system = (System)resource.getContents().get(0);

          newSystem = system;
          state = State.Open;
        }
        catch (RuntimeException | Error ex)
        {
          state = State.Closed;
          LifecycleUtil.deactivate(systemView);
          systemView = null;
          throw ex;
        }

        opened = true;
      }
    }

    if (opened)
    {
      SystemManager.INSTANCE.fireDescriptorStateEvent(this, newSystem, true);
    }
  }

  @Override
  public void close()
  {
    boolean closed = false;
    System oldSystem = null;

    synchronized (this)
    {
      if (isOpen())
      {
        state = State.Closing;
        oldSystem = system;

        try
        {
          for (CDORepository moduleRepository : moduleRepositories.values())
          {
            CDOSession moduleSession = moduleRepository.getSession();
            if (moduleSession != null)
            {
              IRegistry<String, Object> properties = moduleSession.properties();
              properties.remove(KEY_SYSTEM_DESCRIPTOR);
              properties.remove(KEY_MODULE_NAME);
            }

            moduleRepository.disconnect();
          }

          CDOSession systemSession = systemView.getSession();
          systemSession.properties().remove(KEY_SYSTEM_DESCRIPTOR);

          systemView.removeListener(systemListener);
          systemView.close();

          systemRepository.releaseSession();
        }
        finally
        {
          system = null;
          systemView = null;
          state = State.Closed;
        }

        closed = true;
      }
    }

    if (closed)
    {
      SystemManager.INSTANCE.fireDescriptorStateEvent(this, oldSystem, false);
    }
  }

  @Override
  public int compareTo(ISystemDescriptor o)
  {
    if (this == NO_SYSTEM || o == NO_SYSTEM)
    {
      throw new IllegalStateException();
    }

    return systemName.compareTo(o.getSystemName());
  }

  @Override
  public String toString()
  {
    return systemName == null ? "NO_SYSTEM" : systemName;
  }

  @Override
  public <E extends ModelElement, R> R modify(E element, Function<E, R> modifier, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    R result = null;

    if (element.cdoView() != systemView)
    {
      element = systemView.getObject(element);
    }

    CDOSession session = systemView.getSession();
    CDOTransaction transaction = session.openTransaction();

    try
    {
      E transactionalElement = transaction.getObject(element);
      result = modifier.apply(transactionalElement);

      CDOCommitInfo commitInfo = transaction.commit(monitor);
      long commitTime = commitInfo.getTimeStamp();

      if (!systemView.waitForUpdate(commitTime, 10000L))
      {
        throw new TimeoutRuntimeException("System view did not receive the update of commit " + commitTime);
      }

      if (result instanceof CDOObject)
      {
        @SuppressWarnings("unchecked")
        R object = (R)systemView.getObject((CDOObject)result);
        result = object;
      }
    }
    catch (OperationCanceledException ex)
    {
      transaction.rollback();
    }
    finally
    {
      transaction.close();
    }

    return result;
  }

  @Override
  public <R> R modify(Function<System, R> modifier, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    return modify(system, modifier, monitor);
  }

  @Override
  public CDORepository getModuleRepository(String moduleName)
  {
    CDORepository moduleRepository;
    synchronized (this)
    {
      moduleRepository = moduleRepositories.get(moduleName);

      if (moduleRepository == null)
      {
        moduleRepository = connectModuleRepository(moduleName);
        moduleRepositories.put(moduleName, moduleRepository);
      }
    }

    return moduleRepository;
  }

  private CDORepository connectModuleRepository(String moduleName)
  {
    // TODO Move to NamingStrategy.
    String label = "_LM_Module_" + systemName + "_" + moduleName;

    CDORepositoryManager repositoryManager = CDOExplorerUtil.getRepositoryManager();
    CDORepository moduleRepository = repositoryManager.getRepositoryByLabel(label);
    if (moduleRepository == null)
    {
      Properties properties = systemRepository.getProperties();
      properties.setProperty("label", label);
      properties.setProperty("name", moduleName);

      IPasswordCredentials credentials = systemRepository.getCredentials();
      moduleRepository = repositoryManager.addRepository(properties, credentials);
    }

    moduleRepository.connect();

    CDOSession moduleSession = moduleRepository.getSession();
    if (moduleSession != null)
    {
      IRegistry<String, Object> properties = moduleSession.properties();
      properties.put(KEY_SYSTEM_DESCRIPTOR, this);
      properties.put(KEY_MODULE_NAME, moduleName);
    }

    return moduleRepository;
  }

  @Override
  public boolean withModuleSession(String moduleName, Consumer<CDOSession> consumer)
  {
    CDORepository moduleRepository = getModuleRepository(moduleName);
    CDOSession session = moduleRepository.acquireSession();
    if (session != null)
    {
      try
      {
        consumer.accept(session);
      }
      finally
      {
        moduleRepository.releaseSession();
      }

      return true;
    }

    return false;
  }

  @Override
  public ModuleDefinition extractModuleDefinition(Baseline baseline)
  {
    String moduleName = baseline.getModule().getName();

    if (baseline instanceof FixedBaseline)
    {
      FixedBaseline fixedBaseline = (FixedBaseline)baseline;

      ModuleDefinition moduleDefinition = ModulesFactory.eINSTANCE.createModuleDefinition();
      moduleDefinition.setName(moduleName);
      moduleDefinition.setVersion(fixedBaseline.getVersion());

      for (Dependency dependency : fixedBaseline.getDependencies())
      {
        Module target = dependency.getTarget();
        if (target != null)
        {
          moduleDefinition.getDependencies().add(ModulesFactory.eINSTANCE.createDependencyDefinition(target.getName(), dependency.getVersionRange()));
        }
      }

      return moduleDefinition;
    }

    return extractModuleDefinitionModuleDefinition((FloatingBaseline)baseline, CDOBranchPoint.UNSPECIFIED_DATE, moduleName);
  }

  @Override
  public ModuleDefinition extractModuleDefinition(FloatingBaseline baseline, long timeStamp)
  {
    String moduleName = baseline.getModule().getName();
    return extractModuleDefinitionModuleDefinition(baseline, timeStamp, moduleName);
  }

  private ModuleDefinition extractModuleDefinitionModuleDefinition(FloatingBaseline baseline, long timeStamp, String moduleName)
  {
    ModuleDefinition[] result = { null };

    withModuleSession(moduleName, session -> {
      CDOView view = null;

      try
      {
        CDOBranchRef branchRef = baseline.getBranch();
        CDOBranch branch = branchRef.resolve(session.getBranchManager());

        view = session.openView(branch, timeStamp);
        result[0] = extractModuleDefinition(view);
      }
      finally
      {
        if (view != null)
        {
          view.close();
        }
      }
    });

    return result[0];
  }

  @Override
  public ModuleDefinition extractModuleDefinition(CDOView view)
  {
    String moduleDefinitionPath = system.getProcess().getModuleDefinitionPath();

    try
    {
      CDOResource moduleDefinitionResource = view.getResource(moduleDefinitionPath);
      ModuleDefinition moduleDefinition = (ModuleDefinition)moduleDefinitionResource.getContents().get(0);
      return EcoreUtil.copy(moduleDefinition);
    }
    catch (CDOResourceNodeNotFoundException ex)
    {
      return null;
    }
  }

  public Map<String, CDOView> configureModuleResourceSet(CDOView view) throws ResolutionException
  {
    ModuleDefinition rootDefinition = extractModuleDefinition(view);
    if (rootDefinition != null)
    {
      Assembly assembly = AssemblyFactory.eINSTANCE.createAssembly();

      try
      {
        resolveDependencies(rootDefinition, assembly, new NullProgressMonitor());
      }
      catch (ProvisionException ex)
      {
        OM.LOG.error(ex);
        return null;
      }

      ResourceSet resourceSet = view.getResourceSet();
      return configureModuleResourceSet(resourceSet, assembly);
    }

    return null;
  }

  private Map<String, CDOView> configureModuleResourceSet(ResourceSet resourceSet, Assembly assembly)
  {
    Map<String, CDOView> moduleViews = new HashMap<>();
    assembly.forEachDependency(module -> {
      CDOView view = LMResourceSetConfiguration.openView(this, module, resourceSet);
      if (view != null)
      {
        moduleViews.put(module.getName(), view);
      }
    });

    return moduleViews;
  }

  @Override
  public Assembly resolve(ModuleDefinition rootDefinition, Baseline rootBaseline, IProgressMonitor monitor) throws ResolutionException
  {
    Assembly assembly = AssemblyFactory.eINSTANCE.createAssembly();
    assembly.setSystemName(systemName);

    String rootModuleName = rootDefinition.getName();
    Version rootModuleVersion = rootDefinition.getVersion();
    CDOBranchPointRef rootBranchPoint = rootBaseline.getBranchPoint();

    AssemblyModule rootModule = AssemblyFactory.eINSTANCE.createAssemblyModule();
    rootModule.setAssembly(assembly);
    rootModule.setName(rootModuleName);
    rootModule.setVersion(rootModuleVersion);
    rootModule.setBranchPoint(rootBranchPoint);
    rootModule.setRoot(true);
    addAnnotation(rootModule, rootBaseline, rootDefinition);

    try
    {
      resolveDependencies(rootDefinition, assembly, monitor);
    }
    catch (ProvisionException ex)
    {
      OM.LOG.error(ex);
      return null;
    }

    return assembly;
  }

  private void resolveDependencies(ModuleDefinition rootDefinition, Assembly assembly, IProgressMonitor monitor) throws ResolutionException, ProvisionException
  {
    List<FixedBaseline> baselines = new ArrayList<>();
    List<IInstallableUnit> ius = new ArrayList<>();

    system.forEachBaseline(baseline -> {
      if (baseline instanceof FixedBaseline)
      {
        FixedBaseline fixedBaseline = (FixedBaseline)baseline;
        String moduleName = fixedBaseline.getModule().getName();

        if (!Objects.equals(moduleName, rootDefinition.getName()))
        {
          String baselineIndex = Integer.toString(baselines.size());
          baselines.add(fixedBaseline);

          ModuleDefinition moduleDefinition = extractModuleDefinition(fixedBaseline);

          InstallableUnitDescription iuDescription = createIUDescription(moduleDefinition);
          iuDescription.setProperty(PROP_BASELINE_INDEX, baselineIndex);

          IInstallableUnit iu = MetadataFactory.createInstallableUnit(iuDescription);
          ius.add(iu);
        }
      }
    });

    File agentLocation = TMPUtil.createTempFolder("lm-p2-");
    IProvisioningAgent provisioningAgent = null;

    try
    {
      agentLocation.mkdirs();
      provisioningAgent = createProvisioningAgent(agentLocation);

      IProfileRegistry profileRegistry = (IProfileRegistry)provisioningAgent.getService(IProfileRegistry.SERVICE_NAME);
      IProfile profile = profileRegistry.addProfile("TEMP", null);

      IInstallableUnit rootIU = MetadataFactory.createInstallableUnit(createIUDescription(rootDefinition));

      IPlanner planner = (IPlanner)provisioningAgent.getService(IPlanner.SERVICE_NAME);
      IProfileChangeRequest profileChangeRequest = planner.createChangeRequest(profile);
      profileChangeRequest.add(rootIU);
      profileChangeRequest.setInstallableUnitProfileProperty(rootIU, IProfile.PROP_PROFILE_ROOT_IU, Boolean.TRUE.toString());

      CollectionResult<IInstallableUnit> metadata = new CollectionResult<>(ius);
      ProvisioningContext provisioningContext = new ProvisioningContext(provisioningAgent)
      {
        @Override
        public IQueryable<IInstallableUnit> getMetadata(IProgressMonitor monitor)
        {
          return metadata;
        }
      };

      IProvisioningPlan provisioningPlan = planner.getProvisioningPlan(profileChangeRequest, provisioningContext, monitor);
      IStatus status = provisioningPlan.getStatus();
      if (!status.isOK())
      {
        throw createResolutionException(status, rootIU, baselines);
      }

      IQueryable<IInstallableUnit> futureState = provisioningPlan.getFutureState();
      IQueryResult<IInstallableUnit> matches = futureState.query(QueryUtil.createIUQuery(null, VersionRange.emptyRange), monitor);

      for (IInstallableUnit iu : matches)
      {
        FixedBaseline baseline = getBaseline(iu, baselines);
        if (baseline != null)
        {
          AssemblyModule assemblyModule = AssemblyFactory.eINSTANCE.createAssemblyModule();
          assemblyModule.setAssembly(assembly);
          assemblyModule.setName(baseline.getModule().getName());
          assemblyModule.setVersion(baseline.getVersion());
          assemblyModule.setBranchPoint(baseline.getBranchPoint());
          addAnnotation(assemblyModule, baseline, null);
        }
      }
    }
    finally
    {
      if (provisioningAgent != null)
      {
        provisioningAgent.stop();
      }

      if (agentLocation.isDirectory())
      {
        IOUtil.delete(agentLocation);
      }
    }

    assembly.sortModules();
  }

  @Override
  public Module createModule(String name, StreamSpec streamSpec, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    return createModule(name, null, streamSpec, monitor);
  }

  @Override
  public Module createModule(String name, ModuleType type, StreamSpec streamSpec, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    return modify(system -> {
      Stream stream = streamSpec.createStream();
      stream.setAllowedChanges(Impact.MAJOR);

      Module module = LMFactory.eINSTANCE.createModule();
      module.setName(name);
      module.setType(type);
      module.getStreams().add(stream);

      system.getModules().add(module);
      setCommitComment(system, "Add module '" + name + "' and stream '" + stream.getName() + "'");
      return module;
    }, monitor);
  }

  @Override
  public void deleteModule(Module module, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException, ModuleDeletionException
  {
    CDOView view = module.cdoView();
    String moduleName = module.getName();

    // Here we check that there is no module that depends on this module.
    List<CDOObjectReference> result = view.queryXRefs(module, LMPackage.eINSTANCE.getDependency_Target());
    if (result.size() > 0)
    {
      // The deletion is not possible because another module references this one.
      throw new ModuleDeletionException(moduleName, result);
    }

    modify(module, m -> {
      setCommitComment(m, "Delete module '" + moduleName + "'");
      EcoreUtil.remove(m);
      return null;
    }, monitor);
  }

  @Override
  public Stream createStream(Module module, Drop base, StreamSpec streamSpec, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    return modify(module, m -> {
      Stream stream = streamSpec.createStream();
      stream.setBase(base);
      stream.setStartTimeStamp(base.getBranchPoint().getTimeStamp());

      m.getStreams().add(stream);
      setCommitComment(m, "Add stream '" + stream.getName() + "'"); // TODO Move to NamingStrategy.
      return stream;
    }, monitor);
  }

  @Override
  public Drop createDrop(Stream stream, DropType dropType, long timeStamp, String label, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException
  {
    return modify(stream, s -> {
      ModuleDefinition moduleDefinition = extractModuleDefinition(s, timeStamp);
      String moduleName = moduleDefinition.getName();
      Version moduleVersion = moduleDefinition.getVersion();

      int major = (int)moduleVersion.getSegment(0);
      int minor = (int)moduleVersion.getSegment(1);
      int micro = (int)moduleVersion.getSegment(2);

      int streamMajor = s.getMajorVersion();
      int streamMinor = s.getMinorVersion();
      if (major != streamMajor || minor != streamMinor)
      {
        throw new CDOException(
            MessageFormat.format("Module definition version {0} is inconsistent with the stream version {1}.{2}", moduleVersion, streamMajor, streamMinor));
      }

      if (dropType.isRelease())
      {
        Drop lastRelease = s.getLastRelease();
        if (lastRelease != null)
        {
          Version lastVersion = lastRelease.getVersion();
          int lastMicro = (int)lastVersion.getSegment(2);
          if (micro <= lastMicro)
          {
            throw new CDOException(
                MessageFormat.format("Module definition version {0} is not greater than the last released version {1}", moduleVersion, lastVersion));
          }
        }
      }

      String qualifier = TimeStamp.toString(timeStamp);
      moduleVersion = Version.createOSGi(major, minor, micro, qualifier);

      Drop drop = LMFactory.eINSTANCE.createDrop();
      drop.setType(dropType);
      drop.setLabel(label);
      drop.setVersion(moduleVersion);

      CDOBranchPointRef branchPoint = s.getBranchPoint(timeStamp);
      drop.setBranchPoint(branchPoint);

      addDependencies(moduleDefinition, drop);
      s.insertContent(drop);

      if (dropType.isRelease())
      {
        withModuleSession(moduleName, session -> {
          if (s.getMode() == StreamMode.DEVELOPMENT)
          {
            CDOBranch developmentBranch = s.getDevelopmentBranch().resolve(session.getBranchManager());
            CDOBranch maintenanceBranch = developmentBranch.createBranch(streamMajor + "." + streamMinor + "-maintenance", timeStamp);
            s.setMaintenanceBranch(new CDOBranchRef(maintenanceBranch));
            s.setMaintenanceTimeStamp(timeStamp);
          }
        });
      }

      setCommitComment(s, "Add " + dropType.getName().toLowerCase() + " '" + label + "' to stream '" + stream.getName() + "'");
      return drop;
    }, monitor);
  }

  @Override
  public Change createChange(Stream stream, FixedBaseline base, String label, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    String branchName = LMNamingStrategy.getChangeBranchName(label);

    return modify(stream, s -> {
      Change change = LMFactory.eINSTANCE.createChange();
      change.setBase(base);
      change.setLabel(label);
      s.insertContent(change);

      String moduleName = s.getModule().getName();
      withModuleSession(moduleName, session -> {
        CDOBranchPoint baseBranchPoint = (base == null ? stream.getBranch().getPointRef(java.lang.System.currentTimeMillis()) : base.getBranchPoint())
            .resolve(session.getBranchManager());
        CDOBranch changeBranch = baseBranchPoint.getBranch().createBranch(branchName, baseBranchPoint.getTimeStamp());
        change.setBranch(new CDOBranchRef(changeBranch));
      });

      setCommitComment(s, "Add change '" + change.getName() + "' to stream '" + stream.getName() + "'");
      return change;
    }, monitor);
  }

  @Override
  public void renameChange(Change change, String newLabel, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    String branchName = LMNamingStrategy.getChangeBranchName(newLabel);

    modify(change, c -> {
      String oldLabel = c.getLabel();

      String moduleName = c.getModule().getName();
      withModuleSession(moduleName, session -> {
        CDOBranchRef branchRef = c.getBranch();

        CDOBranch branch = branchRef.resolve(session.getBranchManager());
        branch.setName(branchName);

        c.setLabel(newLabel);
        c.setBranch(new CDOBranchRef(branch));
      });

      setCommitComment(c, "Rename change '" + oldLabel + "' to '" + newLabel + "'");
      return null;
    }, monitor);
  }

  @Override
  public void deleteChange(Change change, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    modify(change, c -> {
      String label = c.getLabel();
      Stream stream = c.getStream();

      String moduleName = c.getModule().getName();
      withModuleSession(moduleName, session -> {
        CDOBranchRef changeBranchRef = change.getBranch();
        CDOBranch changeBranch = changeBranchRef.resolve(session.getBranchManager());

        changeBranch.delete(new EclipseMonitor(monitor));
        EcoreUtil.remove(c);
      });

      setCommitComment(stream, "Delete change '" + label + "'");
      return null;
    }, monitor);
  }

  @Override
  public Delivery createDelivery(Stream stream, Change change, LMMerger merger, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    Delivery[] result = { null };
    CommitException[] commitException = { null };

    String moduleName = stream.getModule().getName();
    withModuleSession(moduleName, session -> {
      CDOBranchRef sourceBranchRef = change.getBranch();
      CDOBranchRef targetBranchRef = stream.getBranch();

      CDOBranchManager branchManager = session.getBranchManager();
      CDOBranch sourceBranch = sourceBranchRef.resolve(branchManager);
      CDOBranch targetBranch = targetBranchRef.resolve(branchManager);

      CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
      long sourceCommitTime = commitInfoManager.getLastCommitOfBranch(sourceBranch, true);

      CDOBranchPointRef sourceBranchPointRef = sourceBranchRef.getPointRef(sourceCommitTime);
      CDOBranchPoint sourceBranchPoint = sourceBranch.getPoint(sourceCommitTime);

      long targetCommitTime = merger.mergeDelivery(session, sourceBranchPoint, targetBranch);
      if (targetCommitTime != CDOBranchPoint.INVALID_DATE)
      {
        CDOBranchPointRef targetBranchPointRef = stream.getBranch().getPointRef(targetCommitTime);

        ModuleDefinition moduleDefinition = extractModuleDefinition(stream, targetCommitTime);
        Version moduleVersion = moduleDefinition.getVersion();

        try
        {
          result[0] = modify(stream, s -> {
            CDOTransaction transaction = (CDOTransaction)s.cdoView();
            Change c = transaction.getObject(change);

            Delivery delivery = LMFactory.eINSTANCE.createDelivery();
            delivery.setChange(c);
            delivery.setVersion(moduleVersion);
            delivery.setMergeSource(sourceBranchPointRef);
            delivery.setMergeTarget(targetBranchPointRef);

            addDependencies(moduleDefinition, delivery);
            s.insertContent(delivery);
            setCommitComment(s, "Add delivery '" + delivery.getName() + "' to stream '" + s.getName() + "'");
            return delivery;
          }, monitor);
        }
        catch (CommitException ex)
        {
          commitException[0] = ex;
        }
      }
    });

    if (commitException[0] != null)
    {
      throw commitException[0];
    }

    return result[0];
  }

  private void addDependencies(ModuleDefinition from, FixedBaseline to)
  {
    for (DependencyDefinition dependencyDefinition : from.getDependencies())
    {
      String targetName = dependencyDefinition.getTargetName();
      Module targetModule = system.getModule(targetName);

      Dependency dependency = LMFactory.eINSTANCE.createDependency();
      dependency.setTarget(targetModule);
      dependency.setVersionRange(dependencyDefinition.getVersionRange());
      to.getDependencies().add(dependency);
    }
  }

  private static void setCommitComment(CDOObject object, String comment)
  {
    CDOTransaction transaction = (CDOTransaction)object.cdoView();
    transaction.setCommitComment(comment);
  }

  private static InstallableUnitDescription createIUDescription(ModuleDefinition moduleDefinition)
  {
    List<IRequirement> requirements = new ArrayList<>();
    for (DependencyDefinition dependencyDefinition : moduleDefinition.getDependencies())
    {
      String targetName = dependencyDefinition.getTargetName();
      VersionRange versionRange = dependencyDefinition.getVersionRange();
      if (versionRange == null)
      {
        versionRange = VersionRange.emptyRange;
      }

      IRequirement requirement = MetadataFactory.createRequirement( //
          IInstallableUnit.NAMESPACE_IU_ID, //
          targetName, //
          versionRange, //
          null, // filter
          false, // optional
          false, // multiple
          true // greedy
      );

      requirements.add(requirement);
    }

    String name = moduleDefinition.getName();
    Version version = moduleDefinition.getVersion();

    IProvidedCapability selfCapability = MetadataFactory.createProvidedCapability( //
        IInstallableUnit.NAMESPACE_IU_ID, //
        name, //
        version);

    InstallableUnitDescription iuDescription = new InstallableUnitDescription();
    iuDescription.setId(name);
    iuDescription.setVersion(version);
    iuDescription.setSingleton(true);
    iuDescription.addProvidedCapabilities(Collections.singleton(selfCapability));
    iuDescription.addRequirements(requirements);
    iuDescription.setArtifacts(new IArtifactKey[0]);
    return iuDescription;
  }

  private static IProvisioningAgent createProvisioningAgent(File agentLocation) throws ProvisionException
  {
    BundleContext context = OSGiUtil.getBundleContext(OM.BUNDLE);
    ServiceReference<IProvisioningAgentProvider> providerRef = null;
    IProvisioningAgent provisioningAgent = null;

    try
    {
      providerRef = context.getServiceReference(IProvisioningAgentProvider.class);
      if (providerRef != null)
      {
        IProvisioningAgentProvider provider = context.getService(providerRef);
        provisioningAgent = provider.createAgent(agentLocation.toURI());
      }
    }
    finally
    {
      if (providerRef != null)
      {
        context.ungetService(providerRef);
      }
    }

    return provisioningAgent;
  }

  @SuppressWarnings("restriction")
  private static ResolutionException createResolutionException(IStatus status, IInstallableUnit rootIU, List<FixedBaseline> baselines)
  {
    if (status instanceof org.eclipse.equinox.internal.provisional.p2.director.PlannerStatus)
    {
      org.eclipse.equinox.internal.provisional.p2.director.PlannerStatus plannerStatus = (org.eclipse.equinox.internal.provisional.p2.director.PlannerStatus)status;
      Set<org.eclipse.equinox.internal.p2.director.Explanation> explanations = plannerStatus.getRequestStatus().getExplanations();
      List<Reason> reasons = new ArrayList<>();

      for (org.eclipse.equinox.internal.p2.director.Explanation explanation : explanations)
      {
        if (explanation instanceof org.eclipse.equinox.internal.p2.director.Explanation.MissingIU)
        {
          org.eclipse.equinox.internal.p2.director.Explanation.MissingIU missingIU = (org.eclipse.equinox.internal.p2.director.Explanation.MissingIU)explanation;

          Reason.Module module = createReasonModule(missingIU.iu, rootIU, baselines);

          if (missingIU.req == null)
          {
            reasons.add(new Missing(module, null));
          }
          else
          {
            org.eclipse.equinox.internal.p2.metadata.IRequiredCapability req = (org.eclipse.equinox.internal.p2.metadata.IRequiredCapability)missingIU.req;
            reasons.add(new Missing(module, new Reason.Dependency(req.getName(), req.getRange())));
          }
        }
        else if (explanation instanceof org.eclipse.equinox.internal.p2.director.Explanation.Singleton)
        {
          org.eclipse.equinox.internal.p2.director.Explanation.Singleton singleton = (org.eclipse.equinox.internal.p2.director.Explanation.Singleton)explanation;
          List<Reason.Module> modules = new ArrayList<>();

          for (IInstallableUnit iu : singleton.ius)
          {
            Reason.Module module = createReasonModule(iu, rootIU, baselines);
            modules.add(module);
          }

          reasons.add(new Conflicting(modules.toArray(new Reason.Module[modules.size()])));
        }
      }

      return new ResolutionException(reasons.toArray(new Reason[reasons.size()]));
    }

    return new ResolutionException(null);
  }

  private static Reason.Module createReasonModule(IInstallableUnit iu, IInstallableUnit rootIU, List<FixedBaseline> baselines)
  {
    if (iu == rootIU)
    {
      return new Reason.Module(rootIU.getId(), rootIU.getVersion());
    }

    FixedBaseline baseline = getBaseline(iu, baselines);
    if (baseline != null)
    {
      return new Reason.Module(baseline.getModule().getName(), baseline.getVersion());
    }

    return null;
  }

  private static FixedBaseline getBaseline(IInstallableUnit iu, List<FixedBaseline> baselines)
  {
    String property = iu.getProperty(PROP_BASELINE_INDEX);
    if (property != null)
    {
      int baselineIndex = Integer.parseInt(property);
      return baselines.get(baselineIndex);
    }

    return null;
  }

  private static void addAnnotation(AssemblyModule module, Baseline baseline, ModuleDefinition definition)
  {
    Annotation annotation = EtypesFactory.eINSTANCE.createAnnotation();
    annotation.setSource(LMPackage.ANNOTATION_SOURCE);
    module.getAnnotations().add(annotation);

    EMap<String, String> details = annotation.getDetails();
    details.put(LMPackage.ANNOTATION_DETAIL_BASELINE_ID, CDOExplorerUtil.getCDOIDString(baseline.cdoID()));
    details.put(LMPackage.ANNOTATION_DETAIL_BASELINE_TYPE, baseline.getTypeName());
    details.put(LMPackage.ANNOTATION_DETAIL_BASELINE_NAME, baseline.getName());

    if (definition != null)
    {
      annotation.getContents().addAll(EcoreUtil.copyAll(definition.getDependencies()));
    }
  }

  public static ISystemDescriptor getSystemDescriptor(CDOSession session)
  {
    return (ISystemDescriptor)session.properties().get(KEY_SYSTEM_DESCRIPTOR);
  }

  public static String getModuleName(CDOSession session)
  {
    return (String)session.properties().get(KEY_MODULE_NAME);
  }
}