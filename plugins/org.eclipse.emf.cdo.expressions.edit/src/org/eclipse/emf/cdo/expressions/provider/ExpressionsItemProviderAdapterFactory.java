/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.provider;

import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.util.ExpressionsAdapterFactory;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ChildCreationExtenderManager;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemColorProvider;
import org.eclipse.emf.edit.provider.IItemFontProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITableItemColorProvider;
import org.eclipse.emf.edit.provider.ITableItemFontProvider;
import org.eclipse.emf.edit.provider.ITableItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ExpressionsItemProviderAdapterFactory extends ExpressionsAdapterFactory
    implements ComposeableAdapterFactory, IChangeNotifier, IDisposable, IChildCreationExtender
{
  /**
   * This keeps track of the root adapter factory that delegates to this adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ComposedAdapterFactory parentAdapterFactory;

  /**
   * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IChangeNotifier changeNotifier = new ChangeNotifier();

  /**
   * This helps manage the child creation extenders.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ChildCreationExtenderManager childCreationExtenderManager = new ChildCreationExtenderManager(
      ExpressionsEditPlugin.INSTANCE, ExpressionsPackage.eNS_URI);

  /**
   * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<Object> supportedTypes = new ArrayList<Object>();

  /**
   * This constructs an instance.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionsItemProviderAdapterFactory()
  {
    supportedTypes.add(IEditingDomainItemProvider.class);
    supportedTypes.add(IStructuredItemContentProvider.class);
    supportedTypes.add(ITreeItemContentProvider.class);
    supportedTypes.add(IItemLabelProvider.class);
    supportedTypes.add(IItemPropertySource.class);
    supportedTypes.add(ITableItemLabelProvider.class);
    supportedTypes.add(ITableItemColorProvider.class);
    supportedTypes.add(ITableItemFontProvider.class);
    supportedTypes.add(IItemColorProvider.class);
    supportedTypes.add(IItemFontProvider.class);
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.BooleanValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BooleanValueItemProvider booleanValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.BooleanValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createBooleanValueAdapter()
  {
    if (booleanValueItemProvider == null)
    {
      booleanValueItemProvider = new BooleanValueItemProvider(this);
    }

    return booleanValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.ByteValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ByteValueItemProvider byteValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.ByteValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createByteValueAdapter()
  {
    if (byteValueItemProvider == null)
    {
      byteValueItemProvider = new ByteValueItemProvider(this);
    }

    return byteValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.ShortValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ShortValueItemProvider shortValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.ShortValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createShortValueAdapter()
  {
    if (shortValueItemProvider == null)
    {
      shortValueItemProvider = new ShortValueItemProvider(this);
    }

    return shortValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.IntValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IntValueItemProvider intValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.IntValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createIntValueAdapter()
  {
    if (intValueItemProvider == null)
    {
      intValueItemProvider = new IntValueItemProvider(this);
    }

    return intValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.LongValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LongValueItemProvider longValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.LongValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createLongValueAdapter()
  {
    if (longValueItemProvider == null)
    {
      longValueItemProvider = new LongValueItemProvider(this);
    }

    return longValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.FloatValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FloatValueItemProvider floatValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.FloatValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createFloatValueAdapter()
  {
    if (floatValueItemProvider == null)
    {
      floatValueItemProvider = new FloatValueItemProvider(this);
    }

    return floatValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.DoubleValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DoubleValueItemProvider doubleValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.DoubleValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createDoubleValueAdapter()
  {
    if (doubleValueItemProvider == null)
    {
      doubleValueItemProvider = new DoubleValueItemProvider(this);
    }

    return doubleValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.CharValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CharValueItemProvider charValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.CharValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createCharValueAdapter()
  {
    if (charValueItemProvider == null)
    {
      charValueItemProvider = new CharValueItemProvider(this);
    }

    return charValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.StringValue} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected StringValueItemProvider stringValueItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.StringValue}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createStringValueAdapter()
  {
    if (stringValueItemProvider == null)
    {
      stringValueItemProvider = new StringValueItemProvider(this);
    }

    return stringValueItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.FunctionInvocation} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FunctionInvocationItemProvider functionInvocationItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.FunctionInvocation}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createFunctionInvocationAdapter()
  {
    if (functionInvocationItemProvider == null)
    {
      functionInvocationItemProvider = new FunctionInvocationItemProvider(this);
    }

    return functionInvocationItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.MemberInvocation} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MemberInvocationItemProvider memberInvocationItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.MemberInvocation}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createMemberInvocationAdapter()
  {
    if (memberInvocationItemProvider == null)
    {
      memberInvocationItemProvider = new MemberInvocationItemProvider(this);
    }

    return memberInvocationItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.StaticAccess} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected StaticAccessItemProvider staticAccessItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.StaticAccess}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createStaticAccessAdapter()
  {
    if (staticAccessItemProvider == null)
    {
      staticAccessItemProvider = new StaticAccessItemProvider(this);
    }

    return staticAccessItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.MemberAccess} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MemberAccessItemProvider memberAccessItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.MemberAccess}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createMemberAccessAdapter()
  {
    if (memberAccessItemProvider == null)
    {
      memberAccessItemProvider = new MemberAccessItemProvider(this);
    }

    return memberAccessItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.ContextAccess} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ContextAccessItemProvider contextAccessItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.ContextAccess}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createContextAccessAdapter()
  {
    if (contextAccessItemProvider == null)
    {
      contextAccessItemProvider = new ContextAccessItemProvider(this);
    }

    return contextAccessItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.ContainedObject} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ContainedObjectItemProvider containedObjectItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.ContainedObject}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createContainedObjectAdapter()
  {
    if (containedObjectItemProvider == null)
    {
      containedObjectItemProvider = new ContainedObjectItemProvider(this);
    }

    return containedObjectItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.LinkedObject} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LinkedObjectItemProvider linkedObjectItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.LinkedObject}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createLinkedObjectAdapter()
  {
    if (linkedObjectItemProvider == null)
    {
      linkedObjectItemProvider = new LinkedObjectItemProvider(this);
    }

    return linkedObjectItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.LinkedExpression} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LinkedExpressionItemProvider linkedExpressionItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.LinkedExpression}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createLinkedExpressionAdapter()
  {
    if (linkedExpressionItemProvider == null)
    {
      linkedExpressionItemProvider = new LinkedExpressionItemProvider(this);
    }

    return linkedExpressionItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.expressions.ListConstruction} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ListConstructionItemProvider listConstructionItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.expressions.ListConstruction}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createListConstructionAdapter()
  {
    if (listConstructionItemProvider == null)
    {
      listConstructionItemProvider = new ListConstructionItemProvider(this);
    }

    return listConstructionItemProvider;
  }

  /**
   * This returns the root adapter factory that contains this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComposeableAdapterFactory getRootAdapterFactory()
  {
    return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
  }

  /**
   * This sets the composed adapter factory that contains this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory)
  {
    this.parentAdapterFactory = parentAdapterFactory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object type)
  {
    return supportedTypes.contains(type) || super.isFactoryForType(type);
  }

  /**
   * This implementation substitutes the factory itself as the key for the adapter.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter adapt(Notifier notifier, Object type)
  {
    return super.adapt(notifier, this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object adapt(Object object, Object type)
  {
    if (isFactoryForType(type))
    {
      Object adapter = super.adapt(object, type);
      if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter)))
      {
        return adapter;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<IChildCreationExtender> getChildCreationExtenders()
  {
    return childCreationExtenderManager.getChildCreationExtenders();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain)
  {
    return childCreationExtenderManager.getNewChildDescriptors(object, editingDomain);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ResourceLocator getResourceLocator()
  {
    return childCreationExtenderManager;
  }

  /**
   * This adds a listener.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void addListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.addListener(notifyChangedListener);
  }

  /**
   * This removes a listener.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void removeListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.removeListener(notifyChangedListener);
  }

  /**
   * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void fireNotifyChanged(Notification notification)
  {
    changeNotifier.fireNotifyChanged(notification);

    if (parentAdapterFactory != null)
    {
      parentAdapterFactory.fireNotifyChanged(notification);
    }
  }

  /**
   * This disposes all of the item providers created by this factory. 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void dispose()
  {
    if (booleanValueItemProvider != null)
      booleanValueItemProvider.dispose();
    if (byteValueItemProvider != null)
      byteValueItemProvider.dispose();
    if (shortValueItemProvider != null)
      shortValueItemProvider.dispose();
    if (intValueItemProvider != null)
      intValueItemProvider.dispose();
    if (longValueItemProvider != null)
      longValueItemProvider.dispose();
    if (floatValueItemProvider != null)
      floatValueItemProvider.dispose();
    if (doubleValueItemProvider != null)
      doubleValueItemProvider.dispose();
    if (charValueItemProvider != null)
      charValueItemProvider.dispose();
    if (stringValueItemProvider != null)
      stringValueItemProvider.dispose();
    if (functionInvocationItemProvider != null)
      functionInvocationItemProvider.dispose();
    if (memberInvocationItemProvider != null)
      memberInvocationItemProvider.dispose();
    if (staticAccessItemProvider != null)
      staticAccessItemProvider.dispose();
    if (memberAccessItemProvider != null)
      memberAccessItemProvider.dispose();
    if (contextAccessItemProvider != null)
      contextAccessItemProvider.dispose();
    if (containedObjectItemProvider != null)
      containedObjectItemProvider.dispose();
    if (linkedObjectItemProvider != null)
      linkedObjectItemProvider.dispose();
    if (linkedExpressionItemProvider != null)
      linkedExpressionItemProvider.dispose();
    if (listConstructionItemProvider != null)
      listConstructionItemProvider.dispose();
  }

}
