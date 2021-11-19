/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 4.15
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOLockStateCache extends ILifecycle
{
  public InternalCDOSession getSession();

  public Object createKey(CDOBranch branch, CDOID id);

  public CDOLockState getLockState(CDOBranch branch, CDOID id);

  public void getLockStates(CDOBranch branch, Collection<CDOID> ids, boolean loadOnDemand, Consumer<CDOLockState> consumer);

  public void forEachLockState(CDOBranch branch, CDOLockOwner owner, Consumer<CDOLockState> consumer);

  public void addLockStates(CDOBranch branch, Collection<? extends CDOLockState> newLockStates, Consumer<CDOLockState> consumer);

  public void removeOwner(CDOBranch branch, CDOLockOwner owner, Consumer<CDOLockState> changeConsumer);

  public void remapOwner(CDOBranch branch, CDOLockOwner oldOwner, CDOLockOwner newOwner);

  public void removeLockStates(CDOBranch branch, Collection<CDOID> ids, Consumer<CDOLockState> consumer);

  public void removeLockStates(CDOBranch branch);
}
