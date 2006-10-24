/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal;

import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.util.BufferInputStream;
import org.eclipse.net4j.transport.util.BufferOutputStream;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class RequestWithConfirmation<RESULT> extends SignalActor<RESULT>
{
  protected RequestWithConfirmation(Channel channel)
  {
    super(channel);
  }

  @Override
  protected final void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    System.out.println("================ Requesting " + this);
    requesting(new ExtendedDataOutputStream(out));
    out.flush();

    System.out.println("================ Confirming " + this);
    setResult(confirming(new ExtendedDataInputStream(in)));
  }

  protected abstract void requesting(ExtendedDataOutputStream out) throws IOException;

  protected abstract RESULT confirming(ExtendedDataInputStream in) throws IOException;
}
