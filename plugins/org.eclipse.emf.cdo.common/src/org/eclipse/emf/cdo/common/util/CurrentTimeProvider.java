/*
 * Copyright (c) 2004-2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

/**
 * @author Eike Stepper
 * @since 4.6
 */
public class CurrentTimeProvider implements CDOTimeProvider
{
  public static final CDOTimeProvider INSTANCE = new CurrentTimeProvider();

  private CurrentTimeProvider()
  {
  }

  public long getTimeStamp()
  {
    return System.currentTimeMillis();
  }
}
