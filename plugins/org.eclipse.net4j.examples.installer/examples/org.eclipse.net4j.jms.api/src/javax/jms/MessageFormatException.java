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
 * <P> This exception must be thrown when a JMS client
 *     attempts to use a data type not supported by a message or attempts to
 *     read data in a message as the wrong type. It must also be thrown when
 *     equivalent type errors are made with message property values. For
 *     example, this exception must be thrown if
 *     <CODE>StreamMessage.writeObject</CODE> is given an unsupported class or
 *     if <CODE>StreamMessage.readShort</CODE> is used to read a
 *     <CODE>boolean</CODE> value. Note that the special case of a failure
 *     caused by an attempt to read improperly formatted <CODE>String</CODE>
 *     data as numeric values must throw the
 *     <CODE>java.lang.NumberFormatException</CODE>.
 *
 * @version     26 August 1998
 * @author      Rahul Sharma
 **/

public class MessageFormatException extends JMSException
{

  private static final long serialVersionUID = 1L;

  /** Constructs a <CODE>MessageFormatException</CODE> with the specified
   *  reason and error code.
   *
   *  @param  reason        a description of the exception
   *  @param  errorCode     a string specifying the vendor-specific
   *                        error code
   *
   **/
  public MessageFormatException(String reason, String errorCode)
  {
    super(reason, errorCode);
  }

  /** Constructs a <CODE>MessageFormatException</CODE> with the specified
   *  reason. The error code defaults to null.
   *
   *  @param  reason        a description of the exception
   **/
  public MessageFormatException(String reason)
  {
    super(reason);
  }

}
