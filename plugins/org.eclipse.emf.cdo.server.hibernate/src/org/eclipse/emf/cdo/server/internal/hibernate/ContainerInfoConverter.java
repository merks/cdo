/*
 * Copyright (c) 2009-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * Is responsible for converting container related information from and to a string representation.
 *
 * @see InternalCDORevision#getContainerID()
 * @see InternalCDORevision#getContainingFeatureID()
 * @see InternalCDORevision#getContainingFeatureID()
 * @author Martin Taal
 */
public class ContainerInfoConverter
{
  private static String SEPARATOR = "_:_"; //$NON-NLS-1$

  private static String CONTAINER_PREFIX = "-"; //$NON-NLS-1$

  private static ContainerInfoConverter instance = new ContainerInfoConverter();

  /**
   * @return the instance
   */
  public static ContainerInfoConverter getInstance()
  {
    return instance;
  }

  /**
   * @param instance
   *          the instance to set
   */
  public static void setInstance(ContainerInfoConverter instance)
  {
    ContainerInfoConverter.instance = instance;
  }

  /**
   * Converts the container information of a {@link InternalCDORevision} to a String representation. The container
   * information is the container id ({@link InternalCDORevision#getContainerID()} and the containingFeatureID (
   * {@link InternalCDORevision#getContainingFeatureID()}).
   *
   * @param cdoRevision
   *          the InternalCDORevision which has the container information
   * @return a String representation of the container information which can be stored in a single varchar in the
   *         database.
   */
  public String convertContainerRelationToString(InternalCDORevision cdoRevision)
  {
    return convertContainerRelationToString(cdoRevision, (CDOID)cdoRevision.getContainerID());
  }

  /**
   * Converts the container information of a {@link InternalCDORevision} to a String representation. The container
   * information is the container id ({@link InternalCDORevision#getContainerID()} and the containingFeatureID (
   * {@link InternalCDORevision#getContainingFeatureID()}).
   *
   * @param cdoRevision
   *          the InternalCDORevision which has the container information
   * @param containerID
   *          the CDOID of the container
   * @return a String representation of the container information which can be stored in a single varchar in the
   *         database.
   */
  public String convertContainerRelationToString(InternalCDORevision cdoRevision, CDOID containerID)
  {
    if (containerID == null || containerID.isNull() || containerID.isTemporary())
    {
      return null;
    }

    final String strCDOID = HibernateUtil.getInstance().convertCDOIDToString(containerID);
    if (!HibernateUtil.getInstance().isStoreCreatedID(containerID))
    {
      // does not support changing models....
      return strCDOID + SEPARATOR + cdoRevision.getContainingFeatureID();
    }

    // get the feature name...
    if (cdoRevision.getContainingFeatureID() < 0)
    {
      final String entityName = HibernateUtil.getInstance().getEntityName(containerID);
      final HibernateStore store = HibernateThreadContext.getCurrentStoreAccessor().getStore();
      final EClass containerEClass = store.getEClass(entityName);
      final int featureID = InternalEObject.EOPPOSITE_FEATURE_BASE - cdoRevision.getContainingFeatureID();
      final EStructuralFeature eFeature = containerEClass.getEStructuralFeature(featureID);
      return strCDOID + SEPARATOR + "-" + eFeature.getName(); //$NON-NLS-1$
    }

    final EClass eContainedEClass = cdoRevision.getEClass();
    final EStructuralFeature eFeature = eContainedEClass.getEStructuralFeature(cdoRevision.getContainingFeatureID());
    return strCDOID + SEPARATOR + eFeature.getName();
  }

  /**
   * Converts the String generated by the method {@link #convertContainerRelationToString(InternalCDORevision)} back to
   * container information and sets this in the cdoRevision.
   *
   * @param cdoRevision
   *          the InternalCDORevision in which the container info is stored.
   * @param containerInfo
   *          the containerInfo coded as a String
   */
  public void setContainerRelationFromString(InternalCDORevision cdoRevision, String containerInfo)
  {
    if (containerInfo == null)
    {
      return;
    }

    final int index = containerInfo.lastIndexOf(SEPARATOR);
    if (index == -1)
    {
      // TODO: error condition?
      return;
    }

    // get/set the container id
    final CDOID containerID = HibernateUtil.getInstance().convertStringToCDOID(containerInfo.substring(0, index));
    cdoRevision.setContainerID(containerID);

    final String containerFeatureStr = containerInfo.substring(index + SEPARATOR.length());
    if (containerID instanceof CDOIDExternal)
    {
      cdoRevision.setContainingFeatureID(Integer.parseInt(containerFeatureStr));
      return;
    }

    if (containerFeatureStr.startsWith(CONTAINER_PREFIX))
    {
      // part of the container eClass
      final CDOClassifierRef classifierRef = CDOIDUtil.getClassifierRef(containerID);
      if (classifierRef == null)
      {
        throw new IllegalArgumentException("This CDOID type of " + containerID + " is not supported by this store."); //$NON-NLS-1$ //$NON-NLS-2$
      }

      final EClass containerEClass = HibernateUtil.getInstance().getEClass(classifierRef);

      // substring 1 because the string starts with a minus
      final EStructuralFeature eFeature = containerEClass.getEStructuralFeature(containerFeatureStr.substring(1));
      final int containerFeatureID = InternalEObject.EOPPOSITE_FEATURE_BASE - containerEClass.getFeatureID(eFeature);
      cdoRevision.setContainingFeatureID(containerFeatureID);
      return;
    }

    final EClass eContainedEClass = cdoRevision.getEClass();
    final EStructuralFeature eFeature = eContainedEClass.getEStructuralFeature(containerFeatureStr);
    cdoRevision.setContainingFeatureID(eContainedEClass.getFeatureID(eFeature));
    return;
  }
}
