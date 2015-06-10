/*
 * Copyright (c) 2012, 2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.NotifyingMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitorProgress;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOAdminServerRepository extends Notifier implements CDOAdminRepository
{
  private final CDOAdminServer admin;

  private final IRepository delegate;

  private IListener delegateListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof TypeChangedEvent)
      {
        TypeChangedEvent e = (TypeChangedEvent)event;
        Type oldType = e.getOldType();
        Type newType = e.getNewType();

        fireEvent(new RepositoryTypeChangedEvent(CDOAdminServerRepository.this, oldType, newType));
        admin.repositoryTypeChanged(getName(), oldType, newType);
      }
      else if (event instanceof StateChangedEvent)
      {
        StateChangedEvent e = (StateChangedEvent)event;
        State oldState = e.getOldState();
        State newState = e.getNewState();

        fireEvent(new RepositoryStateChangedEvent(CDOAdminServerRepository.this, oldState, newState));
        admin.repositoryStateChanged(getName(), oldState, newState);
      }
    }
  };

  private IListener delegateSynchronizerListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof OMMonitorProgress)
      {
        OMMonitorProgress e = (OMMonitorProgress)event;
        double totalWork = e.getTotalWork();
        double work = e.getWork();

        fireEvent(new NotifyingMonitor.ProgressEvent(CDOAdminServerRepository.this, totalWork, work));
        admin.repositoryReplicationProgressed(getName(), totalWork, work);
      }
    }
  };

  public CDOAdminServerRepository(CDOAdminServer admin, IRepository delegate)
  {
    this.admin = admin;
    this.delegate = delegate;

    delegate.addListener(delegateListener);
    if (delegate instanceof ISynchronizableRepository)
    {
      IRepositorySynchronizer synchronizer = ((ISynchronizableRepository)delegate).getSynchronizer();
      synchronizer.addListener(delegateSynchronizerListener);
    }
  }

  public final CDOAdmin getAdmin()
  {
    return admin;
  }

  public final IRepository getDelegate()
  {
    return delegate;
  }

  public boolean delete(String type)
  {
    return admin.deleteRepository(this, type);
  }

  public String getName()
  {
    return delegate.getName();
  }

  public String getUUID()
  {
    return delegate.getUUID();
  }

  public Type getType()
  {
    return delegate.getType();
  }

  public State getState()
  {
    return delegate.getState();
  }

  public String getStoreType()
  {
    return delegate.getStoreType();
  }

  public Set<ObjectType> getObjectIDTypes()
  {
    return delegate.getObjectIDTypes();
  }

  public long getCreationTime()
  {
    return delegate.getCreationTime();
  }

  public CDOID getRootResourceID()
  {
    return delegate.getRootResourceID();
  }

  public boolean isAuthenticating()
  {
    return delegate.isAuthenticating();
  }

  public boolean isSupportingAudits()
  {
    return delegate.isSupportingAudits();
  }

  public boolean isSupportingBranches()
  {
    return delegate.isSupportingBranches();
  }

  @Deprecated
  public boolean isSupportingEcore()
  {
    return delegate.isSupportingEcore();
  }

  public boolean isSerializingCommits()
  {
    return delegate.isSerializingCommits();
  }

  public boolean isEnsuringReferentialIntegrity()
  {
    return delegate.isEnsuringReferentialIntegrity();
  }

  public boolean waitWhileInitial(IProgressMonitor monitor)
  {
    return delegate.waitWhileInitial(monitor);
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    return delegate.getIDGenerationLocation();
  }

  public long getTimeStamp() throws UnsupportedOperationException
  {
    return delegate.getTimeStamp();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public String toString()
  {
    return delegate.toString();
  }

  public void write(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(getName());
    out.writeString(getUUID());
    out.writeEnum(getType());
    out.writeEnum(getState());
    out.writeString(getStoreType());

    Set<CDOID.ObjectType> objectIDTypes = getObjectIDTypes();
    int types = objectIDTypes.size();
    out.writeInt(types);
    for (CDOID.ObjectType objectIDType : objectIDTypes)
    {
      out.writeEnum(objectIDType);
    }

    out.writeLong(getCreationTime());
    new CDODataOutputImpl(out).writeCDOID(getRootResourceID());
    out.writeBoolean(isAuthenticating());
    out.writeBoolean(isSupportingAudits());
    out.writeBoolean(isSupportingBranches());
    out.writeBoolean(isSerializingCommits());
    out.writeBoolean(isEnsuringReferentialIntegrity());
    out.writeEnum(getIDGenerationLocation());
  }

  public void dispose()
  {
    delegate.removeListener(delegateListener);
    if (delegate instanceof ISynchronizableRepository)
    {
      IRepositorySynchronizer synchronizer = ((ISynchronizableRepository)delegate).getSynchronizer();
      synchronizer.removeListener(delegateSynchronizerListener);
    }
  }
}
