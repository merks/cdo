/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.repositories;

import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.internal.explorer.AbstractManager;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.event.Event;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDORepositoryManagerImpl extends AbstractManager<CDORepository>implements CDORepositoryManager
{
  public static final String SECURE_STORE_PATH = "/CDO/repositories";

  private static final String PROPERTIES_FILE = "repository.properties";

  private final Map<CDOSession, CDORepository> sessionMap = new ConcurrentHashMap<CDOSession, CDORepository>();

  public CDORepositoryManagerImpl(File folder)
  {
    super(CDORepository.class, folder);
  }

  @Override
  public String getPropertiesFileName()
  {
    return PROPERTIES_FILE;
  }

  public CDORepository getRepository(String id)
  {
    return getElement(id);
  }

  public CDORepository getRepository(CDOSession session)
  {
    return sessionMap.get(session);
  }

  public CDORepository[] getRepositories()
  {
    return getElements();
  }

  public CDORepository addRepository(Properties properties)
  {
    return newElement(properties);
  }

  public void disconnectUnusedRepositories()
  {
    for (CDORepository repository : getRepositories())
    {
      ((CDORepositoryImpl)repository).disconnectIfUnused();
    }
  }

  public void fireRepositoryConnectionEvent(CDORepository repository, CDOSession session, boolean connected)
  {
    if (connected)
    {
      sessionMap.put(session, repository);
    }
    else
    {
      sessionMap.remove(session);
    }

    fireEvent(new RepositoryConnectionEventImpl(this, repository, connected));
  }

  @Override
  protected CDORepositoryImpl createElement(String type)
  {
    if (CDORepository.TYPE_REMOTE.equals(type))
    {
      return new RemoteCDORepository();
    }

    if (CDORepository.TYPE_CLONE.equals(type))
    {
      return new CloneCDORepository();
    }

    if (CDORepository.TYPE_LOCAL.equals(type))
    {
      return new LocalCDORepository();
    }

    throw new IllegalArgumentException("Unknown type: " + type);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (CDORepository repository : getRepositories())
    {
      ((CDORepositoryImpl)repository).doDisconnect(true);
    }

    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private static final class RepositoryConnectionEventImpl extends Event implements RepositoryConnectionEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDORepository repository;

    private final boolean connected;

    public RepositoryConnectionEventImpl(CDORepositoryManager repositoryManager, CDORepository repository,
        boolean connected)
    {
      super(repositoryManager);
      this.repository = repository;
      this.connected = connected;
    }

    @Override
    public CDORepositoryManager getSource()
    {
      return (CDORepositoryManager)super.getSource();
    }

    public CDORepository getRepository()
    {
      return repository;
    }

    public boolean isConnected()
    {
      return connected;
    }
  }
}
