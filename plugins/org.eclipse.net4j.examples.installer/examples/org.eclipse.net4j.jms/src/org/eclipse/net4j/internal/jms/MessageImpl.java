/*
 * Copyright (c) 2007, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.internal.jms.messages.Messages;
import org.eclipse.net4j.internal.jms.util.DestinationUtil;
import org.eclipse.net4j.internal.jms.util.TypeUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.Session;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MessageImpl implements Message, Comparable<MessageImpl>
{
  private static final String[] KEYWORDS = { "and", "between", "escape", "in", "is", "like", "false", "null", "or", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
    "not", "true" }; //$NON-NLS-1$ //$NON-NLS-2$

  private Map<String, Object> properties = new HashMap<String, Object>();

  private byte[] correlationID;

  private int deliveryMode;

  private DestinationImpl destination;

  private long expiration;

  private String messageID;

  private int priority;

  private boolean redelivered;

  private DestinationImpl replyTo;

  private long timestamp;

  private String type;

  private SessionImpl receivingSession;

  private MessageConsumerImpl receivingConsumer;

  public MessageImpl()
  {
  }

  public SessionImpl getReceivingSession()
  {
    return receivingSession;
  }

  public void setReceivingSession(SessionImpl receivingSession)
  {
    this.receivingSession = receivingSession;
  }

  public MessageConsumerImpl getReceivingConsumer()
  {
    return receivingConsumer;
  }

  public void setReceivingConsumer(MessageConsumerImpl receivingConsumer)
  {
    this.receivingConsumer = receivingConsumer;
  }

  public String getJMSCorrelationID()
  {
    if (correlationID == null)
    {
      return null;
    }

    return new String(correlationID);
  }

  public void setJMSCorrelationID(String correlationID)
  {
    this.correlationID = correlationID == null ? null : correlationID.getBytes();
  }

  public byte[] getJMSCorrelationIDAsBytes()
  {
    return correlationID;
  }

  public void setJMSCorrelationIDAsBytes(byte[] correlationID)
  {
    this.correlationID = correlationID;
  }

  public int getJMSDeliveryMode()
  {
    return deliveryMode;
  }

  public void setJMSDeliveryMode(int deliveryMode)
  {
    this.deliveryMode = deliveryMode;
  }

  public Destination getJMSDestination()
  {
    return destination;
  }

  public void setJMSDestination(Destination destination) throws JMSException
  {
    this.destination = DestinationUtil.convert(destination);
  }

  public long getJMSExpiration()
  {
    return expiration;
  }

  public void setJMSExpiration(long expiration)
  {
    this.expiration = expiration;
  }

  public String getJMSMessageID()
  {
    return messageID;
  }

  public void setJMSMessageID(String messageID)
  {
    this.messageID = messageID;
  }

  public int getJMSPriority()
  {
    return priority;
  }

  public void setJMSPriority(int priority)
  {
    this.priority = priority;
  }

  public boolean getJMSRedelivered()
  {
    return redelivered;
  }

  public void setJMSRedelivered(boolean redelivered)
  {
    this.redelivered = redelivered;
  }

  public Destination getJMSReplyTo()
  {
    return replyTo;
  }

  public void setJMSReplyTo(Destination replyTo) throws JMSException
  {
    this.replyTo = DestinationUtil.convert(replyTo);
  }

  public long getJMSTimestamp()
  {
    return timestamp;
  }

  public void setJMSTimestamp(long timestamp)
  {
    this.timestamp = timestamp;
  }

  public String getJMSType()
  {
    return type;
  }

  public void setJMSType(String type)
  {
    this.type = type;
  }

  public void clearProperties()
  {
    properties.clear();
  }

  public boolean propertyExists(String name)
  {
    return properties.containsKey(name);
  }

  public boolean getBooleanProperty(String name) throws MessageFormatException
  {
    return TypeUtil.getBoolean(properties.get(name));
  }

  public byte getByteProperty(String name) throws MessageFormatException
  {
    return TypeUtil.getByte(properties.get(name));
  }

  public short getShortProperty(String name) throws MessageFormatException
  {
    return TypeUtil.getShort(properties.get(name));
  }

  public int getIntProperty(String name) throws MessageFormatException
  {
    return TypeUtil.getInt(properties.get(name));
  }

  public long getLongProperty(String name) throws MessageFormatException
  {
    return TypeUtil.getLong(properties.get(name));
  }

  public float getFloatProperty(String name) throws MessageFormatException
  {
    return TypeUtil.getFloat(properties.get(name));
  }

  public double getDoubleProperty(String name) throws MessageFormatException
  {
    return TypeUtil.getDouble(properties.get(name));
  }

  public String getStringProperty(String name) throws MessageFormatException
  {
    return TypeUtil.getString(properties.get(name));
  }

  public Object getObjectProperty(String name)
  {
    return properties.get(name);
  }

  public Enumeration<String> getPropertyNames()
  {
    return Collections.enumeration(properties.keySet());
  }

  public void setBooleanProperty(String name, boolean value) throws JMSException
  {
    setProperty(name, value);
  }

  public void setByteProperty(String name, byte value) throws JMSException
  {
    setProperty(name, value);
  }

  public void setShortProperty(String name, short value) throws JMSException
  {
    setProperty(name, value);
  }

  public void setIntProperty(String name, int value) throws JMSException
  {
    setProperty(name, value);
  }

  public void setLongProperty(String name, long value) throws JMSException
  {
    setProperty(name, value);
  }

  public void setFloatProperty(String name, float value) throws JMSException
  {
    setProperty(name, value);
  }

  public void setDoubleProperty(String name, double value) throws JMSException
  {
    setProperty(name, value);
  }

  public void setStringProperty(String name, String value) throws JMSException
  {
    setProperty(name, value);
  }

  public void setObjectProperty(String name, Object value) throws JMSException
  {
    if (value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer
        || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof String
        || value == null)
    {
      setProperty(name, value);
    }
    else
    {
      throw new MessageFormatException(
          MessageFormat.format(Messages.getString("MessageImpl_11"), value.getClass().getName())); //$NON-NLS-1$
    }
  }

  protected void setProperty(String name, Object value) throws JMSException
  {
    if (name == null)
    {
      throw new JMSException(MessageFormat.format(Messages.getString("MessageImpl_13"), name)); //$NON-NLS-1$
    }

    char[] chars = name.toCharArray();
    if (chars.length == 0)
    {
      throw new JMSException(MessageFormat.format(Messages.getString("MessageImpl_13"), name)); //$NON-NLS-1$
    }

    if (!Character.isJavaIdentifierStart(chars[0]))
    {
      throw new JMSException(MessageFormat.format(Messages.getString("MessageImpl_13"), name)); //$NON-NLS-1$
    }

    for (int i = 1; i < chars.length; ++i)
    {
      if (!Character.isJavaIdentifierPart(chars[i]))
      {
        throw new JMSException(MessageFormat.format(Messages.getString("MessageImpl_13"), name)); //$NON-NLS-1$
      }
    }

    for (int i = 0; i < KEYWORDS.length; ++i)
    {
      if (name.equalsIgnoreCase(KEYWORDS[i]))
      {
        throw new JMSException(MessageFormat.format(Messages.getString("MessageImpl_13"), name)); //$NON-NLS-1$
      }
    }

    properties.put(name, value);
  }

  public void acknowledge()
  {
    if (receivingSession == null)
    {
      return;
    }

    if (!receivingSession.isActive())
    {
      return;
    }

    if (receivingSession.getTransacted())
    {
      return;
    }

    if (receivingSession.getAcknowledgeMode() != Session.CLIENT_ACKNOWLEDGE)
    {
      return;
    }

    receivingSession.acknowledgeMessages(receivingConsumer);
  }

  public void clearBody()
  {
    throw new NotYetImplementedException();
  }

  public int compareTo(MessageImpl obj)
  {
    if (priority < obj.priority)
    {
      return -1;
    }

    if (priority > obj.priority)
    {
      return 1;
    }

    return 0;
  }

  @SuppressWarnings("unchecked")
  public void populate(Message source) throws JMSException
  {
    setJMSMessageID(source.getJMSMessageID());
    setJMSDestination(source.getJMSDestination());
    setJMSTimestamp(source.getJMSTimestamp());
    setJMSPriority(source.getJMSPriority());
    setJMSExpiration(source.getJMSExpiration());
    setJMSDeliveryMode(source.getJMSDeliveryMode());
    setJMSCorrelationID(source.getJMSCorrelationID());
    setJMSType(source.getJMSType());
    setJMSReplyTo(source.getJMSReplyTo());

    Enumeration<String> e = source.getPropertyNames();
    while (e.hasMoreElements())
    {
      String name = e.nextElement();
      Object value = source.getObjectProperty(name);
      setObjectProperty(name, value);
    }
  }

  public void write(ExtendedDataOutputStream out) throws IOException
  {
    out.writeByteArray(correlationID);
    out.writeByte(deliveryMode);
    DestinationUtil.write(out, destination);
    out.writeLong(expiration);
    out.writeString(messageID);
    out.writeByte(priority);
    out.writeBoolean(redelivered);
    DestinationUtil.write(out, replyTo);
    out.writeLong(timestamp);
    out.writeString(type);

    out.writeInt(properties.size());
    for (Entry<String, Object> entry : properties.entrySet())
    {
      out.writeString(entry.getKey());
      TypeUtil.write(out, entry.getValue());
    }
  }

  public void read(ExtendedDataInputStream in) throws IOException
  {
    correlationID = in.readByteArray();
    deliveryMode = in.readByte();
    destination = DestinationUtil.read(in);
    expiration = in.readLong();
    messageID = in.readString();
    priority = in.readByte();
    redelivered = in.readBoolean();
    replyTo = DestinationUtil.read(in);
    timestamp = in.readLong();
    type = in.readString();

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      String key = in.readString();
      Object value = TypeUtil.read(in);
      properties.put(key, value);
    }
  }
}
