/*
 * Copyright (c) 2010, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @generated
 */
public class AcoreNavigatorGroup extends AcoreAbstractNavigatorItem
{

  /**
   * @generated
   */
  private String myGroupName;

  /**
   * @generated
   */
  private String myIcon;

  /**
   * @generated
   */
  private Collection myChildren = new LinkedList();

  /**
   * @generated
   */
  AcoreNavigatorGroup(String groupName, String icon, Object parent)
  {
    super(parent);
    myGroupName = groupName;
    myIcon = icon;
  }

  /**
   * @generated
   */
  public String getGroupName()
  {
    return myGroupName;
  }

  /**
   * @generated
   */
  public String getIcon()
  {
    return myIcon;
  }

  /**
   * @generated
   */
  public Object[] getChildren()
  {
    return myChildren.toArray();
  }

  /**
   * @generated
   */
  public void addChildren(Collection children)
  {
    myChildren.addAll(children);
  }

  /**
   * @generated
   */
  public void addChild(Object child)
  {
    myChildren.add(child);
  }

  /**
   * @generated
   */
  public boolean isEmpty()
  {
    return myChildren.size() == 0;
  }

  /**
   * @generated
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreNavigatorGroup)
    {
      org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreNavigatorGroup anotherGroup = (org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreNavigatorGroup)obj;
      if (getGroupName().equals(anotherGroup.getGroupName()))
      {
        return getParent().equals(anotherGroup.getParent());
      }
    }
    return super.equals(obj);
  }

  /**
   * @generated
   */
  @Override
  public int hashCode()
  {
    return getGroupName().hashCode();
  }

}
