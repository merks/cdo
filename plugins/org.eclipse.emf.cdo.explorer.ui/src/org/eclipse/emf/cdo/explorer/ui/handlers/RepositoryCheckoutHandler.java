/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.CDOCheckoutSource;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.CDORepository;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Eike Stepper
 */
public class RepositoryCheckoutHandler extends BaseHandler<CDOCheckoutSource>
{
  public RepositoryCheckoutHandler()
  {
    super(CDOCheckoutSource.class, null);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor progressMonitor) throws Exception
  {
    for (CDOCheckoutSource checkoutSource : elements)
    {
      CDORepository repository = checkoutSource.getRepository();

      String label = repository.getLabel();
      String branchPath = checkoutSource.getBranchPath();
      long timeStamp = checkoutSource.getTimeStamp();
      CDOID rootID = checkoutSource.getRootID();

      CDOCheckoutManager checkoutManager = CDOExplorerUtil.getCheckoutManager();
      checkoutManager.connect(label, repository, branchPath, timeStamp, false, rootID);
    }
  }
}