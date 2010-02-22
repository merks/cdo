/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package
 * @generated
 */
public interface Model3Factory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Model3Factory eINSTANCE = org.eclipse.emf.cdo.tests.model3.impl.Model3FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Class1</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Class1</em>'.
   * @generated
   */
  Class1 createClass1();

  /**
   * Returns a new object of class '<em>Meta Ref</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Meta Ref</em>'.
   * @generated
   */
  MetaRef createMetaRef();

  /**
   * Returns a new object of class '<em>Polygon</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Polygon</em>'.
   * @generated
   */
  Polygon createPolygon();

  /**
   * Returns a new object of class '<em>Polygon With Duplicates</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Polygon With Duplicates</em>'.
   * @generated
   */
  PolygonWithDuplicates createPolygonWithDuplicates();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  Model3Package getModel3Package();

} // Model3Factory
