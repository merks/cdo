/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.server.PackageManager;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("unused")
public class LoadPackageIndication extends CDOReadIndication
{
  private CDOPackageImpl cdoPackage;

  public LoadPackageIndication()
  {
    super(CDOProtocolConstants.SIGNAL_LOAD_PACKAGE);
  }

  @Override
  protected void accessStore(ExtendedDataInputStream in) throws IOException
  {
    String packageURI = in.readString();
    PackageManager packageManager = getPackageManager();
    cdoPackage = packageManager.lookupPackage(packageURI);
    if (cdoPackage == null)
    {
      throw new ImplementationError("CDO package not found: " + packageURI);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    cdoPackage.write(out);
  }
}
