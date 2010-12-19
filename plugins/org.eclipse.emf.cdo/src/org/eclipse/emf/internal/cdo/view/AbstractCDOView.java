/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.DanglingReferenceException;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewAdaptersNotifiedEvent;
import org.eclipse.emf.cdo.view.CDOViewEvent;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.emf.internal.cdo.CDOMetaWrapper;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.CDOStore;
import org.eclipse.emf.internal.cdo.CDOURIHandler;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.query.CDOQueryImpl;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.collection.FastList;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOView extends Lifecycle implements InternalCDOView
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, AbstractCDOView.class);

  private final boolean legacyModeEnabled;

  private CDOBranchPoint branchPoint;

  private CDOURIHandler uriHandler = new CDOURIHandler(this);

  private InternalCDOViewSet viewSet;

  private ConcurrentMap<CDOID, InternalCDOObject> objects;

  private CDOStore store = new CDOStore(this);

  private ReentrantLock lock = new ReentrantLock(true);

  private ReentrantLock stateLock = new ReentrantLock(true);

  private CDOResourceImpl rootResource;

  private FastList<CDOObjectHandler> objectHandlers = new FastList<CDOObjectHandler>()
  {
    @Override
    protected CDOObjectHandler[] newArray(int length)
    {
      return new CDOObjectHandler[length];
    }
  };

  @ExcludeFromDump
  private transient CDOID lastLookupID;

  @ExcludeFromDump
  private transient InternalCDOObject lastLookupObject;

  public AbstractCDOView(CDOBranchPoint branchPoint, boolean legacyModeEnabled)
  {
    this.branchPoint = branchPoint;
    this.legacyModeEnabled = legacyModeEnabled;
  }

  public boolean isReadOnly()
  {
    return true;
  }

  public boolean isLegacyModeEnabled()
  {
    return legacyModeEnabled;
  }

  protected ConcurrentMap<CDOID, InternalCDOObject> getObjects()
  {
    return objects;
  }

  protected void setObjects(ConcurrentMap<CDOID, InternalCDOObject> objects)
  {
    this.objects = objects;
  }

  public CDOStore getStore()
  {
    checkActive();
    return store;
  }

  public ResourceSet getResourceSet()
  {
    return getViewSet().getResourceSet();
  }

  /**
   * @since 2.0
   */
  public InternalCDOViewSet getViewSet()
  {
    return viewSet;
  }

  /**
   * @since 2.0
   */
  public void setViewSet(InternalCDOViewSet viewSet)
  {
    this.viewSet = viewSet;
    if (viewSet != null)
    {
      viewSet.getResourceSet().getURIConverter().getURIHandlers().add(0, getURIHandler());
    }
  }

  public CDOResourceImpl getRootResource()
  {
    checkActive();
    if (rootResource == null)
    {
      CDOID rootResourceID = getSession().getRepositoryInfo().getRootResourceID();
      rootResource = (CDOResourceImpl)getObject(rootResourceID);
      rootResource.setRoot(true);
      registerObject(rootResource);
      getResourceSet().getResources().add(rootResource);
    }

    return rootResource;
  }

  protected void clearRootResource()
  {
    if (rootResource != null)
    {
      getResourceSet().getResources().remove(rootResource);
      deregisterObject(rootResource);
      rootResource = null;
    }
  }

  public CDOURIHandler getURIHandler()
  {
    return uriHandler;
  }

  protected CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  public boolean setBranch(CDOBranch branch)
  {
    return setBranchPoint(branch, getTimeStamp());
  }

  public boolean setTimeStamp(long timeStamp)
  {
    return setBranchPoint(getBranch(), timeStamp);
  }

  public boolean setBranchPoint(CDOBranch branch, long timeStamp)
  {
    checkActive();
    return setBranchPoint(branch.getPoint(timeStamp));
  }

  protected void basicSetBranchPoint(CDOBranchPoint branchPoint)
  {
    this.branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
  }

  public void waitForUpdate(long updateTime)
  {
    waitForUpdate(updateTime, NO_TIMEOUT);
  }

  protected List<InternalCDOObject> getInvalidObjects(long timeStamp)
  {
    List<InternalCDOObject> result = new ArrayList<InternalCDOObject>();
    synchronized (objects)
    {
      for (InternalCDOObject object : objects.values())
      {
        CDORevision revision = object.cdoRevision();
        if (revision == null)
        {
          revision = getRevision(object.cdoID(), false);
        }

        if (revision == null || !revision.isValid(timeStamp))
        {
          result.add(object);
        }
      }
    }

    return result;
  }

  public CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  protected void fireViewTargetChangedEvent(IListener[] listeners)
  {
    fireEvent(new ViewTargetChangedEvent(branchPoint), listeners);
  }

  public ReentrantLock getLock()
  {
    return lock;
  }

  public ReentrantLock getStateLock()
  {
    return stateLock;
  }

  public boolean isDirty()
  {
    return false;
  }

  public boolean hasConflict()
  {
    return false;
  }

  public boolean hasResource(String path)
  {
    checkActive();

    try
    {
      getResourceNodeID(path);
      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
  }

  public CDOQueryImpl createQuery(String language, String queryString)
  {
    return createQuery(language, queryString, null);
  }

  public CDOQueryImpl createQuery(String language, String queryString, Object context)
  {
    checkActive();
    return new CDOQueryImpl(this, language, queryString, context);
  }

  public CDOResourceNode getResourceNode(String path)
  {
    CDOID id = getResourceNodeID(path);
    if (id == null)
    {
      return null;
    }

    InternalCDOObject object = getObject(id);
    if (object instanceof CDOResourceNode)
    {
      return (CDOResourceNode)object;
    }

    return null;
  }

  /**
   * @return never <code>null</code>
   */
  public CDOID getResourceNodeID(String path)
  {
    if (StringUtil.isEmpty(path))
    {
      throw new IllegalArgumentException(Messages.getString("CDOViewImpl.1")); //$NON-NLS-1$
    }

    CDOID folderID = null;
    if (CDOURIUtil.SEGMENT_SEPARATOR.equals(path))
    {
      folderID = getResourceNodeIDChecked(null, null);
    }
    else
    {
      List<String> names = CDOURIUtil.analyzePath(path);
      for (String name : names)
      {
        folderID = getResourceNodeIDChecked(folderID, name);
      }
    }

    return folderID;
  }

  /**
   * @return never <code>null</code>
   */
  private CDOID getResourceNodeIDChecked(CDOID folderID, String name)
  {
    folderID = getResourceNodeID(folderID, name);
    if (folderID == null)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.2"), name)); //$NON-NLS-1$
    }

    return folderID;
  }

  /**
   * @return never <code>null</code>
   */
  protected CDOResourceNode getResourceNode(CDOID folderID, String name)
  {
    try
    {
      CDOID id = getResourceNodeID(folderID, name);
      return (CDOResourceNode)getObject(id);
    }
    catch (CDOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new CDOException(ex);
    }
  }

  protected CDOID getResourceNodeID(CDOID folderID, String name)
  {
    if (folderID == null)
    {
      return getRootOrTopLevelResourceNodeID(name);
    }

    if (name == null)
    {
      throw new IllegalArgumentException(Messages.getString("CDOViewImpl.3")); //$NON-NLS-1$
    }

    InternalCDORevision folderRevision = getLocalRevision(folderID);
    EClass resourceFolderClass = EresourcePackage.eINSTANCE.getCDOResourceFolder();
    if (folderRevision.getEClass() != resourceFolderClass)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.4"), folderID)); //$NON-NLS-1$
    }

    EReference nodesFeature = EresourcePackage.eINSTANCE.getCDOResourceFolder_Nodes();
    EAttribute nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    int size = folderRevision.data().size(nodesFeature);
    for (int i = 0; i < size; i++)
    {
      Object value = folderRevision.data().get(nodesFeature, i);
      value = getStore().resolveProxy(folderRevision, nodesFeature, i, value);

      CDORevision childRevision = getLocalRevision((CDOID)convertObjectToID(value));
      if (name.equals(childRevision.data().get(nameFeature, 0)))
      {
        return childRevision.getID();
      }
    }

    throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.5"), name)); //$NON-NLS-1$
  }

  protected CDOID getRootOrTopLevelResourceNodeID(String name)
  {
    CDOQuery resourceQuery = createResourcesQuery(null, name, true);
    resourceQuery.setMaxResults(1);
    List<CDOID> ids = resourceQuery.getResult(CDOID.class);
    if (ids.isEmpty())
    {
      if (name == null)
      {
        throw new CDOException(Messages.getString("CDOViewImpl.6")); //$NON-NLS-1$
      }

      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.7"), name)); //$NON-NLS-1$
    }

    if (ids.size() > 1)
    {
      // TODO is this still needed since the is resourceQuery.setMaxResults(1) ??
      throw new ImplementationError(Messages.getString("CDOViewImpl.8")); //$NON-NLS-1$
    }

    return ids.get(0);
  }

  protected InternalCDORevision getLocalRevision(CDOID id)
  {
    InternalCDORevision revision = null;
    InternalCDOObject object = getObject(id, false);
    if (object != null && object.cdoState() != CDOState.PROXY)
    {
      revision = object.cdoRevision();
    }

    if (revision == null)
    {
      revision = getRevision(id, true);
    }

    if (revision == null)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.9"), id)); //$NON-NLS-1$
    }

    return revision;
  }

  public InternalCDOObject[] getObjectsArray()
  {
    synchronized (objects)
    {
      return objects.values().toArray(new InternalCDOObject[objects.size()]);
    }
  }

  public CDOResource getResource(String path)
  {
    return getResource(path, true);
  }

  public CDOResource getResource(String path, boolean loadInDemand)
  {
    checkActive();
    URI uri = CDOURIUtil.createResourceURI(this, path);
    return (CDOResource)getResourceSet().getResource(uri, loadInDemand);
  }

  public List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    CDOQuery resourceQuery = createResourcesQuery(folder, name, exactMatch);
    return resourceQuery.getResult(CDOResourceNode.class);
  }

  public CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name,
      boolean exactMatch)
  {
    CDOQuery resourceQuery = createResourcesQuery(folder, name, exactMatch);
    return resourceQuery.getResultAsync(CDOResourceNode.class);
  }

  private CDOQuery createResourcesQuery(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    checkActive();
    CDOQueryImpl query = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES, name);
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_FOLDER_ID, folder == null ? null : folder.cdoID());
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_EXACT_MATCH, exactMatch);
    return query;
  }

  public List<CDOObjectReference> queryXRefs(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    CDOQuery xrefsQuery = createXRefsQuery(targetObjects, sourceReferences);
    return xrefsQuery.getResult(CDOObjectReference.class);
  }

  public CloseableIterator<CDOObjectReference> queryXRefsAsync(Set<CDOObject> targetObjects,
      EReference... sourceReferences)
  {
    CDOQuery xrefsQuery = createXRefsQuery(targetObjects, sourceReferences);
    return xrefsQuery.getResultAsync(CDOObjectReference.class);
  }

  private CDOQuery createXRefsQuery(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    checkActive();

    String string = createXRefsQueryString(targetObjects);
    CDOQuery query = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_XREFS, string);

    if (sourceReferences.length != 0)
    {
      string = createXRefsQueryParameter(sourceReferences);
      query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_XREFS_SOURCE_REFERENCES, string);
    }

    return query;
  }

  private String createXRefsQueryString(Set<CDOObject> targetObjects)
  {
    StringBuilder builder = new StringBuilder();
    for (CDOObject target : targetObjects)
    {
      if (FSMUtil.isTransient(target))
      {
        throw new IllegalArgumentException("Cross referencing for transient objects not supported " + target);
      }

      CDOID id = target.cdoID();
      if (id.isTemporary())
      {
        throw new IllegalArgumentException("Cross referencing for uncommitted new objects not supported " + target);
      }

      if (builder.length() != 0)
      {
        builder.append("|");
      }

      builder.append(id.toURIFragment());

      if (!(id instanceof CDOClassifierRef.Provider))
      {
        builder.append("|");
        CDOClassifierRef classifierRef = new CDOClassifierRef(target.eClass());
        builder.append(classifierRef.getURI());
      }
    }

    return builder.toString();
  }

  private String createXRefsQueryParameter(EReference[] sourceReferences)
  {
    StringBuilder builder = new StringBuilder();
    for (EReference sourceReference : sourceReferences)
    {
      if (builder.length() != 0)
      {
        builder.append("|");
      }

      CDOClassifierRef classifierRef = new CDOClassifierRef(sourceReference.eClass());
      builder.append(classifierRef.getURI());
      builder.append("|");
      builder.append(sourceReference.getName());
    }

    return builder.toString();
  }

  public CDOResourceImpl getResource(CDOID resourceID)
  {
    if (CDOIDUtil.isNull(resourceID))
    {
      throw new IllegalArgumentException("resourceID: " + resourceID); //$NON-NLS-1$
    }

    return (CDOResourceImpl)getObject(resourceID);
  }

  public InternalCDOObject newInstance(EClass eClass)
  {
    EObject eObject = EcoreUtil.create(eClass);
    return FSMUtil.adapt(eObject, this);
  }

  public InternalCDORevision getRevision(CDOID id)
  {
    return getRevision(id, true);
  }

  public InternalCDOObject getObject(CDOID id)
  {
    return getObject(id, true);
  }

  /**
   * Support recursivity and concurrency.
   */
  public InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    if (rootResource != null && rootResource.cdoID().equals(id))
    {
      return rootResource;
    }

    // Since getObject could trigger a read (ONLY when we load a resource) we NEED to make sure the state lock is
    // active.
    // Always use in the following order
    // 1- getStateLock().lock();
    // 2- synchronized(objects)
    // DO NOT inverse them otherwise deadlock could occured.

    ReentrantLock stateLock = getStateLock();
    stateLock.lock();

    try
    {
      synchronized (objects)
      {
        if (id.equals(lastLookupID))
        {
          return lastLookupObject;
        }

        // Needed for recursive call to getObject. (from createObject/cleanObject/getResource/getObject)
        InternalCDOObject localLookupObject = objects.get(id);
        if (localLookupObject == null)
        {
          if (id.isMeta())
          {
            localLookupObject = createMetaObject((CDOIDMeta)id);
          }
          else
          {
            if (loadOnDemand)
            {
              excludeTempIDs(id);
              localLookupObject = createObject(id);
            }
            else
            {
              return null;
            }
          }

          // CDOResource have a special way to register to the view.
          if (!CDOModelUtil.isResource(localLookupObject.eClass()))
          {
            registerObject(localLookupObject);
          }
        }

        lastLookupID = id;
        lastLookupObject = localLookupObject;
        return lastLookupObject;
      }
    }
    finally
    {
      stateLock.unlock();
    }
  }

  protected void excludeTempIDs(CDOID id)
  {
    if (id.isTemporary())
    {
      throw new ObjectNotFoundException(id, this);
    }
  }

  /**
   * @since 2.0
   */
  public <T extends EObject> T getObject(T objectFromDifferentView)
  {
    checkActive();
    CDOObject object = CDOUtil.getCDOObject(objectFromDifferentView);
    CDOView view = object.cdoView();
    if (view != this)
    {
      if (!view.getSession().getRepositoryInfo().getUUID().equals(getSession().getRepositoryInfo().getUUID()))
      {
        throw new IllegalArgumentException(MessageFormat.format(
            Messages.getString("CDOViewImpl.11"), objectFromDifferentView)); //$NON-NLS-1$
      }

      CDOID id = object.cdoID();
      InternalCDOObject contextified = getObject(id, true);

      @SuppressWarnings("unchecked")
      T cast = (T)CDOUtil.getEObject(contextified);
      return cast;
    }

    return objectFromDifferentView;
  }

  public boolean isObjectRegistered(CDOID id)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return false;
    }

    synchronized (objects)
    {
      return objects.containsKey(id);
    }
  }

  public InternalCDOObject removeObject(CDOID id)
  {
    synchronized (objects)
    {
      if (id.equals(lastLookupID))
      {
        lastLookupID = null;
        lastLookupObject = null;
      }

      return objects.remove(id);
    }
  }

  /**
   * @return Never <code>null</code>
   */
  private InternalCDOObject createMetaObject(CDOIDMeta id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating meta object for " + id); //$NON-NLS-1$
    }

    InternalCDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    InternalEObject metaInstance = packageRegistry.getMetaInstanceMapper().lookupMetaInstance(id);
    return new CDOMetaWrapper(this, metaInstance, id);
  }

  /**
   * @return Never <code>null</code>
   */
  private InternalCDOObject createObject(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating object for " + id); //$NON-NLS-1$
    }

    InternalCDORevision revision = getRevision(id, true);
    if (revision == null)
    {
      throw new ObjectNotFoundException(id, this);
    }

    EClass eClass = revision.getEClass();
    InternalCDOObject object;
    if (CDOModelUtil.isResource(eClass) && !id.equals(getSession().getRepositoryInfo().getRootResourceID()))
    {
      object = (InternalCDOObject)newResourceInstance(revision);
      // object is PROXY
    }
    else
    {
      object = newInstance(eClass);
      // object is TRANSIENT
    }

    cleanObject(object, revision);
    return object;
  }

  private CDOResource newResourceInstance(InternalCDORevision revision)
  {
    String path = getResourcePath(revision);
    return getResource(path, true);
  }

  private String getResourcePath(InternalCDORevision revision)
  {
    EAttribute nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    CDOID folderID = (CDOID)revision.data().getContainerID();
    String name = (String)revision.data().get(nameFeature, 0);
    if (CDOIDUtil.isNull(folderID))
    {
      if (name == null)
      {
        return CDOURIUtil.SEGMENT_SEPARATOR;
      }

      return name;
    }

    InternalCDOObject object = getObject(folderID, true);
    if (object instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)object;
      String path = folder.getPath();
      return path + CDOURIUtil.SEGMENT_SEPARATOR + name;
    }

    throw new ImplementationError(MessageFormat.format(Messages.getString("CDOViewImpl.14"), object)); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  protected void cleanObject(InternalCDOObject object, InternalCDORevision revision)
  {
    object.cdoInternalCleanup();

    object.cdoInternalSetView(this);
    object.cdoInternalSetRevision(revision);
    object.cdoInternalSetID(revision.getID());
    object.cdoInternalSetState(CDOState.CLEAN);

    object.cdoInternalPostLoad();
  }

  public CDOID provideCDOID(Object idOrObject)
  {
    Object shouldBeCDOID = convertObjectToID(idOrObject);
    if (shouldBeCDOID instanceof CDOID)
    {
      CDOID id = (CDOID)shouldBeCDOID;
      if (TRACER.isEnabled() && id != idOrObject)
      {
        TRACER.format("Converted object to CDOID: {0} --> {1}", idOrObject, id); //$NON-NLS-1$
      }

      return id;
    }
    else if (idOrObject instanceof InternalEObject)
    {
      InternalEObject eObject = (InternalEObject)idOrObject;
      if (eObject instanceof InternalCDOObject)
      {
        InternalCDOObject object = (InternalCDOObject)idOrObject;
        if (object.cdoView() != null && FSMUtil.isNew(object))
        {
          String uri = EcoreUtil.getURI(eObject).toString();
          return CDOIDUtil.createTempObjectExternal(uri);
        }
      }

      Resource eResource = eObject.eResource();
      if (eResource != null)
      {
        // Check if eObject is contained by a deleted resource
        if (!(eResource instanceof CDOResource) || ((CDOResource)eResource).cdoState() != CDOState.TRANSIENT)
        {
          String uri = EcoreUtil.getURI(eObject).toString();
          return CDOIDUtil.createExternal(uri);
        }
      }

      throw new DanglingReferenceException(eObject);
    }

    throw new IllegalStateException(MessageFormat.format(
        Messages.getString("CDOViewImpl.16"), idOrObject.getClass().getName())); //$NON-NLS-1$
  }

  public Object convertObjectToID(Object potentialObject)
  {
    return convertObjectToID(potentialObject, false);
  }

  /**
   * @since 2.0
   */
  public Object convertObjectToID(Object potentialObject, boolean onlyPersistedID)
  {
    if (potentialObject instanceof CDOID)
    {
      return potentialObject;
    }

    if (potentialObject instanceof InternalEObject)
    {
      if (potentialObject instanceof InternalCDOObject)
      {
        InternalCDOObject object = (InternalCDOObject)potentialObject;
        CDOID id = getID(object, onlyPersistedID);
        if (id != null)
        {
          return id;
        }
      }
      else
      {
        try
        {
          InternalCDOObject object = FSMUtil.getLegacyAdapter(((InternalEObject)potentialObject).eAdapters());
          if (object != null)
          {
            CDOID id = getID(object, onlyPersistedID);
            if (id != null)
            {
              return id;
            }

            potentialObject = object;
          }
        }
        catch (Throwable ex)
        {
          OM.LOG.warn(ex);
        }
      }
    }

    return potentialObject;
  }

  protected CDOID getID(InternalCDOObject object, boolean onlyPersistedID)
  {
    if (onlyPersistedID)
    {
      if (FSMUtil.isTransient(object) || FSMUtil.isNew(object))
      {
        return null;
      }
    }

    CDOView view = object.cdoView();
    if (view == this)
    {
      return object.cdoID();
    }

    if (view != null && view.getSession() == getSession())
    {
      boolean sameTarget = view.getBranch().equals(getBranch()) && view.getTimeStamp() == getTimeStamp();
      if (sameTarget)
      {
        return object.cdoID();
      }

      throw new IllegalArgumentException("Object " + object + " is managed by a view with different target: " + view);
    }

    return null;
  }

  public Object convertIDToObject(Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      if (potentialID == CDOID.NULL)
      {
        return null;
      }

      CDOID id = (CDOID)potentialID;
      if (id.isExternal())
      {
        return getResourceSet().getEObject(URI.createURI(id.toURIFragment()), true);
      }

      InternalCDOObject result = getObject(id, true);
      if (result == null)
      {
        throw new ImplementationError(MessageFormat.format(Messages.getString("CDOViewImpl.17"), id)); //$NON-NLS-1$
      }

      return result.cdoInternalInstance();
    }

    return potentialID;
  }

  /**
   * @since 2.0
   */
  public void attachResource(CDOResourceImpl resource)
  {
    if (!resource.isExisting())
    {
      throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.18"), this)); //$NON-NLS-1$
    }

    // ResourceSet.getResource(uri, true) was called!!
    resource.cdoInternalSetView(this);
    resource.cdoInternalSetState(CDOState.PROXY);
  }

  /**
   * @since 2.0
   */
  public void registerProxyResource(CDOResourceImpl resource)
  {
    URI uri = resource.getURI();
    String path = CDOURIUtil.extractResourcePath(uri);
    boolean isRoot = "/".equals(path); //$NON-NLS-1$

    try
    {
      CDOID id = isRoot ? getSession().getRepositoryInfo().getRootResourceID() : getResourceNodeID(path);
      resource.cdoInternalSetID(id);
      registerObject(resource);
      if (isRoot)
      {
        resource.setRoot(true);
        rootResource = resource;
      }

    }
    catch (Exception ex)
    {
      throw new InvalidURIException(uri, ex);
    }
  }

  public void registerObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering {0}", object); //$NON-NLS-1$
    }

    InternalCDOObject old;
    synchronized (objects)
    {
      old = objects.put(object.cdoID(), object);
    }

    if (old != null)
    {
      if (CDOUtil.isLegacyObject(object))
      {
        if (old != object)
        {
          throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOViewImpl.30"), object.cdoID())); //$NON-NLS-1$
        }

        OM.LOG.warn("Legacy object has been registered multiple times: " + object);
      }
      else
      {
        throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOViewImpl.20"), object)); //$NON-NLS-1$
      }
    }
  }

  public void deregisterObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deregistering {0}", object); //$NON-NLS-1$
    }

    removeObject(object.cdoID());
  }

  public void remapObject(CDOID oldID)
  {
    CDOID newID;
    synchronized (objects)
    {
      InternalCDOObject object = objects.remove(oldID);
      newID = object.cdoID();
      objects.put(newID, object);

      if (lastLookupID == oldID)
      {
        lastLookupID = null;
        lastLookupObject = null;
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Remapping {0} --> {1}", oldID, newID); //$NON-NLS-1$
    }
  }

  public void addObjectHandler(CDOObjectHandler handler)
  {
    objectHandlers.add(handler);
  }

  public void removeObjectHandler(CDOObjectHandler handler)
  {
    objectHandlers.remove(handler);
  }

  public CDOObjectHandler[] getObjectHandlers()
  {
    return objectHandlers.get();
  }

  public void handleObjectStateChanged(InternalCDOObject object, CDOState oldState, CDOState newState)
  {
    CDOObjectHandler[] handlers = getObjectHandlers();
    if (handlers != null)
    {
      for (int i = 0; i < handlers.length; i++)
      {
        CDOObjectHandler handler = handlers[i];
        handler.objectStateChanged(this, object, oldState, newState);
      }
    }
  }

  protected Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> invalidate(long lastUpdateTime,
      List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects, List<CDORevisionDelta> deltas,
      Map<CDOObject, CDORevisionDelta> revisionDeltas, Set<CDOObject> detachedObjects)
  {
    Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts = null;
    for (CDORevisionKey key : allChangedObjects)
    {
      CDORevisionDelta delta = null;
      if (key instanceof CDORevisionDelta)
      {
        delta = (CDORevisionDelta)key;
        // Copy the revision delta if we are a transaction, so that conflict resolvers can modify it.
        if (this instanceof CDOTransaction)
        {
          delta = new CDORevisionDeltaImpl(delta, true);
        }

        deltas.add(delta);
      }

      CDOObject changedObject = null;
      // 258831 - Causes deadlock when introduce thread safe mechanisms in State machine.
      synchronized (objects)
      {
        changedObject = objects.get(key.getID());
      }

      if (changedObject != null)
      {
        Pair<CDORevision, CDORevisionDelta> oldInfo = new Pair<CDORevision, CDORevisionDelta>(
            changedObject.cdoRevision(), delta);
        // if (!isLocked(changedObject))
        {
          CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)changedObject, key, lastUpdateTime);
        }

        revisionDeltas.put(changedObject, delta);
        if (changedObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashMap<CDOObject, Pair<CDORevision, CDORevisionDelta>>();
          }

          conflicts.put(changedObject, oldInfo);
        }
      }
    }

    for (CDOIDAndVersion key : allDetachedObjects)
    {
      InternalCDOObject detachedObject = removeObject(key.getID());
      if (detachedObject != null)
      {
        Pair<CDORevision, CDORevisionDelta> oldInfo = new Pair<CDORevision, CDORevisionDelta>(
            detachedObject.cdoRevision(), CDORevisionDelta.DETACHED);
        // if (!isLocked(detachedObject))
        {
          CDOStateMachine.INSTANCE.detachRemote(detachedObject);
        }

        detachedObjects.add(detachedObject);
        if (detachedObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashMap<CDOObject, Pair<CDORevision, CDORevisionDelta>>();
          }

          conflicts.put(detachedObject, oldInfo);
        }
      }
    }

    return conflicts;
  }

  protected void handleConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts,
      List<CDORevisionDelta> deltas)
  {
    // Do nothing
  }

  public void fireAdaptersNotifiedEvent(long timeStamp)
  {
    fireEvent(new AdaptersNotifiedEvent(timeStamp));
  }

  /**
   * TODO For this method to be useable locks must be cached locally!
   */
  @SuppressWarnings("unused")
  private boolean isLocked(InternalCDOObject object)
  {
    if (object.cdoWriteLock().isLocked())
    {
      return true;
    }

    if (object.cdoReadLock().isLocked())
    {
      return true;
    }

    return false;
  }

  public int reload(CDOObject... objects)
  {
    Collection<InternalCDOObject> internalObjects;
    // TODO Should objects.length == 0 reload *all* objects, too?
    if (objects != null && objects.length != 0)
    {
      internalObjects = new ArrayList<InternalCDOObject>(objects.length);
      for (CDOObject object : objects)
      {
        if (object instanceof InternalCDOObject)
        {
          internalObjects.add((InternalCDOObject)object);
        }
      }
    }
    else
    {
      synchronized (this.objects)
      {
        internalObjects = new ArrayList<InternalCDOObject>(this.objects.values());
      }
    }

    int result = internalObjects.size();
    if (result != 0)
    {
      CDOStateMachine.INSTANCE.reload(internalObjects.toArray(new InternalCDOObject[result]));
    }

    return result;
  }

  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return !isActive();
  }

  public int compareTo(CDOBranchPoint o)
  {
    return branchPoint.compareTo(o);
  }

  @Override
  public String toString()
  {
    InternalCDOSession session = getSession();
    int sessionID = session == null ? 0 : session.getSessionID();
    return MessageFormat.format("{0}[{1}:{2}]", getClassName(), sessionID, getViewID()); //$NON-NLS-1$
  }

  protected String getClassName()
  {
    return "CDOView"; //$NON-NLS-1$
  }

  public boolean isAdapterForType(Object type)
  {
    return type instanceof ResourceSet;
  }

  public org.eclipse.emf.common.notify.Notifier getTarget()
  {
    return getResourceSet();
  }

  public void collectViewedRevisions(Map<CDOID, InternalCDORevision> revisions)
  {
    synchronized (objects)
    {
      for (InternalCDOObject object : objects.values())
      {
        CDOState state = object.cdoState();
        if (state != CDOState.CLEAN && state != CDOState.DIRTY && state != CDOState.CONFLICT)
        {
          continue;
        }

        CDOID id = object.cdoID();
        if (revisions.containsKey(id))
        {
          continue;
        }

        InternalCDORevision revision = CDOStateMachine.INSTANCE.readNoLoad(object);
        if (revision == null)
        {
          continue;
        }

        revisions.put(id, revision);
      }
    }
  }

  public CDOChangeSetData compare(CDOBranchPoint source)
  {
    InternalCDOSession session = getSession();
    synchronized (session.getInvalidationLock())
    {
      long now = getLastUpdateTime();
      CDOBranchPoint target = this;

      if (target.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        target = target.getBranch().getPoint(now);
      }

      if (source.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        source = source.getBranch().getPoint(now);
      }

      CDORevisionAvailabilityInfo targetInfo = createRevisionAvailabilityInfo(target);
      CDORevisionAvailabilityInfo sourceInfo = createRevisionAvailabilityInfo(source);

      CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
      Set<CDOID> ids = sessionProtocol.loadMergeData(targetInfo, sourceInfo, null, null);

      cacheRevisions(targetInfo);
      cacheRevisions(sourceInfo);

      return CDORevisionDeltaUtil.createChangeSetData(ids, sourceInfo, targetInfo);
    }
  }

  protected CDORevisionAvailabilityInfo createRevisionAvailabilityInfo(CDOBranchPoint branchPoint)
  {
    CDORevisionAvailabilityInfo info = new CDORevisionAvailabilityInfo(branchPoint);

    InternalCDORevisionManager revisionManager = getSession().getRevisionManager();
    InternalCDORevisionCache cache = revisionManager.getCache();

    List<CDORevision> revisions = cache.getRevisions(branchPoint);
    for (CDORevision revision : revisions)
    {
      if (revision instanceof PointerCDORevision)
      {
        PointerCDORevision pointer = (PointerCDORevision)revision;
        CDOBranchVersion target = pointer.getTarget();
        if (target != null)
        {
          revision = cache.getRevisionByVersion(pointer.getID(), target);
        }
      }
      else if (revision instanceof DetachedCDORevision)
      {
        revision = null;
      }

      if (revision != null)
      {
        info.addRevision(revision);
      }
    }

    return info;
  }

  protected void cacheRevisions(CDORevisionAvailabilityInfo info)
  {
    InternalCDORevisionManager revisionManager = getSession().getRevisionManager();
    CDOBranch branch = info.getBranchPoint().getBranch();
    for (CDORevisionKey key : info.getAvailableRevisions().values())
    {
      CDORevision revision = (CDORevision)key;
      revisionManager.addRevision(revision);

      if (!ObjectUtil.equals(revision.getBranch(), branch))
      {
        CDOID id = revision.getID();
        CDORevision firstRevision = revisionManager.getCache().getRevisionByVersion(id,
            branch.getVersion(CDOBranchVersion.FIRST_VERSION));
        if (firstRevision != null)
        {
          long revised = firstRevision.getTimeStamp() - 1L;
          CDOBranchVersion target = CDOBranchUtil.copyBranchVersion(revision);
          PointerCDORevision pointer = new PointerCDORevision(revision.getEClass(), id, branch, revised, target);
          revisionManager.addRevision(pointer);
        }
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    viewSet = null;
    objects = null;
    store = null;
    lastLookupID = null;
    lastLookupObject = null;
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  protected abstract class Event extends org.eclipse.net4j.util.event.Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    public Event()
    {
      super(AbstractCDOView.this);
    }

    @Override
    public AbstractCDOView getSource()
    {
      return (AbstractCDOView)super.getSource();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AdaptersNotifiedEvent extends Event implements CDOViewAdaptersNotifiedEvent
  {
    private static final long serialVersionUID = 1L;

    private long timeStamp;

    public AdaptersNotifiedEvent(long timeStamp)
    {
      this.timeStamp = timeStamp;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    @Override
    public String toString()
    {
      return "CDOViewAdaptersNotifiedEvent: " + timeStamp; //$NON-NLS-1$
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class ViewTargetChangedEvent extends Event implements CDOViewTargetChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private CDOBranchPoint branchPoint;

    public ViewTargetChangedEvent(CDOBranchPoint branchPoint)
    {
      this.branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOViewTargetChangedEvent: {0}", branchPoint); //$NON-NLS-1$
    }

    public CDOBranchPoint getBranchPoint()
    {
      return branchPoint;
    }
  }
}
