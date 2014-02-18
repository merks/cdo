/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.predicates.impl;

import org.eclipse.emf.cdo.releng.predicates.AndPredicate;
import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.cdo.releng.predicates.PredicatesPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.resources.IProject;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>And Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.predicates.impl.AndPredicateImpl#getOperands <em>Operands</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AndPredicateImpl extends PredicateImpl implements AndPredicate
{
  /**
   * The cached value of the '{@link #getOperands() <em>Operands</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOperands()
   * @generated
   * @ordered
   */
  protected EList<Predicate> operands;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AndPredicateImpl()
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
    return PredicatesPackage.Literals.AND_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Predicate> getOperands()
  {
    if (operands == null)
    {
      operands = new EObjectContainmentEList<Predicate>(Predicate.class, this,
          PredicatesPackage.AND_PREDICATE__OPERANDS);
    }
    return operands;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean matches(IProject project)
  {
    for (Predicate operand : getOperands())
    {
      if (!operand.matches(project))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case PredicatesPackage.AND_PREDICATE__OPERANDS:
      return ((InternalEList<?>)getOperands()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case PredicatesPackage.AND_PREDICATE__OPERANDS:
      return getOperands();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case PredicatesPackage.AND_PREDICATE__OPERANDS:
      getOperands().clear();
      getOperands().addAll((Collection<? extends Predicate>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case PredicatesPackage.AND_PREDICATE__OPERANDS:
      getOperands().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case PredicatesPackage.AND_PREDICATE__OPERANDS:
      return operands != null && !operands.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // AndPredicateImpl
