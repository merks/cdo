/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

/**
 * Various static helper methods for dealing with {@link Realm realms}.
 *
 * @author Eike Stepper
 */
public final class RealmUtil
{
  private RealmUtil()
  {
  }

  public static User findUser(EList<SecurityItem> items, String userID)
  {
    for (SecurityItem item : items)
    {
      if (item instanceof User)
      {
        User user = (User)item;
        if (ObjectUtil.equals(user.getId(), userID))
        {
          return user;
        }
      }
      else if (item instanceof Directory)
      {
        Directory directory = (Directory)item;
        User user = findUser(directory.getItems(), userID);
        if (user != null)
        {
          return user;
        }
      }
    }

    return null;
  }

  public static Group findGroup(EList<SecurityItem> items, String groupID)
  {
    for (SecurityItem item : items)
    {
      if (item instanceof Group)
      {
        Group group = (Group)item;
        if (ObjectUtil.equals(group.getId(), groupID))
        {
          return group;
        }
      }
      else if (item instanceof Directory)
      {
        Directory directory = (Directory)item;
        Group group = findGroup(directory.getItems(), groupID);
        if (group != null)
        {
          return group;
        }
      }
    }

    return null;
  }

  public static Role findRole(EList<SecurityItem> items, String roleID)
  {
    for (SecurityItem item : items)
    {
      if (item instanceof Role)
      {
        Role role = (Role)item;
        if (ObjectUtil.equals(role.getId(), roleID))
        {
          return role;
        }
      }
      else if (item instanceof Directory)
      {
        Directory directory = (Directory)item;
        Role role = findRole(directory.getItems(), roleID);
        if (role != null)
        {
          return role;
        }
      }
    }

    return null;
  }

  public static BasicEList<User> allUsers(EList<SecurityItem> items)
  {
    BasicEList<User> result = new BasicEList<User>();
    allUsers(items, result);
    return result;
  }

  public static BasicEList<Group> allGroups(EList<SecurityItem> items)
  {
    BasicEList<Group> result = new BasicEList<Group>();
    allGroups(items, result);
    return result;
  }

  public static BasicEList<Role> allRoles(EList<SecurityItem> items)
  {
    BasicEList<Role> result = new BasicEList<Role>();
    allRoles(items, result);
    return result;
  }

  public static BasicEList<Check> allChecks(EList<SecurityItem> items)
  {
    BasicEList<Check> result = new BasicEList<Check>();
    allChecks(items, result);
    return result;
  }

  private static void allUsers(EList<SecurityItem> items, EList<User> result)
  {
    for (SecurityItem item : items)
    {
      if (item instanceof User)
      {
        User user = (User)item;
        result.add(user);
      }
      else if (item instanceof Directory)
      {
        Directory directory = (Directory)item;
        allUsers(directory.getItems(), result);
      }
    }
  }

  private static void allGroups(EList<SecurityItem> items, EList<Group> result)
  {
    for (SecurityItem item : items)
    {
      if (item instanceof Group)
      {
        Group group = (Group)item;
        result.add(group);
      }
      else if (item instanceof Directory)
      {
        Directory directory = (Directory)item;
        allGroups(directory.getItems(), result);
      }
    }
  }

  private static void allRoles(EList<SecurityItem> items, EList<Role> result)
  {
    for (SecurityItem item : items)
    {
      if (item instanceof Role)
      {
        Role role = (Role)item;
        result.add(role);
      }
      else if (item instanceof Directory)
      {
        Directory directory = (Directory)item;
        allRoles(directory.getItems(), result);
      }
    }
  }

  private static void allChecks(EList<SecurityItem> items, EList<Check> result)
  {
    for (SecurityItem item : items)
    {
      if (item instanceof Role)
      {
        Role role = (Role)item;
        result.addAll(role.getChecks());
      }
      else if (item instanceof Directory)
      {
        Directory directory = (Directory)item;
        allChecks(directory.getItems(), result);
      }
    }
  }
}