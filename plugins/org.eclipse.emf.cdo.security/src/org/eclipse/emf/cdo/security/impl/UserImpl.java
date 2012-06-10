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
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.security.Check;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.HashSet;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>User</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getGroups <em>Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getFirstName <em>First Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getLastName <em>Last Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getEmail <em>Email</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getDefaultPermissionOverride <em>Default Permission Override</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getDefaultPermission <em>Default Permission</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#isLocked <em>Locked</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getAllGroups <em>All Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getAllRoles <em>All Roles</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getAllChecks <em>All Checks</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getUnassignedRoles <em>Unassigned Roles</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UserImpl extends AssigneeImpl implements User
{
  private EList<Group> allGroups = new CachedList<Group>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return UserImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.USER__ALL_GROUPS;
    }

    @Override
    protected Object[] getData()
    {
      Set<Group> result = new HashSet<Group>();

      for (Group group : getGroups())
      {
        result.add(group);
        result.addAll(group.getAllInheritedGroups());
      }

      return result.toArray();
    }
  };

  private EList<Role> allRoles = new CachedList<Role>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return UserImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.USER__ALL_ROLES;
    }

    @Override
    protected Object[] getData()
    {
      Set<Role> result = new HashSet<Role>();
      result.addAll(getRoles());

      for (Group group : getAllGroups())
      {
        result.addAll(group.getAllRoles());
      }

      return result.toArray();
    }
  };

  private EList<Check> allChecks = new CachedList<Check>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return UserImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.USER__ALL_CHECKS;
    }

    @Override
    protected Object[] getData()
    {
      BasicEList<Check> result = new BasicEList<Check>();

      for (Role role : getAllRoles())
      {
        result.addAll(role.getChecks());
      }

      return result.toArray();
    }
  };

  private EList<Role> unassignedRoles = new CachedList<Role>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return UserImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.USER__UNASSIGNED_ROLES;
    }

    @Override
    protected Object[] getData()
    {
      Set<Role> result = new HashSet<Role>();

      Realm realm = getRealm();
      if (realm != null)
      {
        result.addAll(realm.getAllRoles());
        result.removeAll(getAllRoles());
      }

      return result.toArray();
    }
  };

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected UserImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SecurityPackage.Literals.USER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Group> getGroups()
  {
    return (EList<Group>)eGet(SecurityPackage.Literals.USER__GROUPS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Group> getAllGroups()
  {
    return allGroups;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Role> getAllRoles()
  {
    return allRoles;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Check> getAllChecks()
  {
    return allChecks;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Role> getUnassignedRoles()
  {
    return unassignedRoles;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLabel()
  {
    return (String)eGet(SecurityPackage.Literals.USER__LABEL, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getFirstName()
  {
    return (String)eGet(SecurityPackage.Literals.USER__FIRST_NAME, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFirstName(String newFirstName)
  {
    eSet(SecurityPackage.Literals.USER__FIRST_NAME, newFirstName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLastName()
  {
    return (String)eGet(SecurityPackage.Literals.USER__LAST_NAME, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLastName(String newLastName)
  {
    eSet(SecurityPackage.Literals.USER__LAST_NAME, newLastName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getEmail()
  {
    return (String)eGet(SecurityPackage.Literals.USER__EMAIL, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEmail(String newEmail)
  {
    eSet(SecurityPackage.Literals.USER__EMAIL, newEmail);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Permission getDefaultPermissionOverride()
  {
    return (Permission)eGet(SecurityPackage.Literals.USER__DEFAULT_PERMISSION_OVERRIDE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDefaultPermissionOverride(Permission newDefaultPermissionOverride)
  {
    eSet(SecurityPackage.Literals.USER__DEFAULT_PERMISSION_OVERRIDE, newDefaultPermissionOverride);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Permission getDefaultPermission()
  {
    Permission defaultPermissionOverride = getDefaultPermissionOverride();
    if (defaultPermissionOverride != null)
    {
      return defaultPermissionOverride;
    }

    Realm realm = getRealm();
    if (realm != null)
    {
      return realm.getDefaultPermission();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isLocked()
  {
    return (Boolean)eGet(SecurityPackage.Literals.USER__LOCKED, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLocked(boolean newLocked)
  {
    eSet(SecurityPackage.Literals.USER__LOCKED, newLocked);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UserPassword getPassword()
  {
    return (UserPassword)eGet(SecurityPackage.Literals.USER__PASSWORD, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPassword(UserPassword newPassword)
  {
    eSet(SecurityPackage.Literals.USER__PASSWORD, newPassword);
  }

} // UserImpl