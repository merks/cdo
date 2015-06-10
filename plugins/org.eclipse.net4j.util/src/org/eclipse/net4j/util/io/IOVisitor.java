/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public interface IOVisitor
{
  public boolean visit(File file) throws IOException;
}
