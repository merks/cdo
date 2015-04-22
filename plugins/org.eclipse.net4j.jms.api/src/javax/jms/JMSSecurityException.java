/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */

package javax.jms;

/**
 * <P> This exception must be thrown when a provider rejects a user
 *     name/password submitted by a client. It may also be thrown for any case
 *     where a security restriction prevents a method from completing.
 *
 * @version     26 August 1998
 * @author      Rahul Sharma
 **/

public class JMSSecurityException extends JMSException
{

  private static final long serialVersionUID = 1L;

  /** Constructs a <CODE>JMSSecurityException</CODE> with the specified
   *  reason and error code.
   *
   *  @param  reason        a description of the exception
   *  @param  errorCode     a string specifying the vendor-specific
   *                        error code
   *
   **/
  public JMSSecurityException(String reason, String errorCode)
  {
    super(reason, errorCode);
  }

  /** Constructs a <CODE>JMSSecurityException</CODE> with the specified
   *  reason. The error code defaults to null.
   *
   *  @param  reason        a description of the exception
   **/
  public JMSSecurityException(String reason)
  {
    super(reason);
  }

}
