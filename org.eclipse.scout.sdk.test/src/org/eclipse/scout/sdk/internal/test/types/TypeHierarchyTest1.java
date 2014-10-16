/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.internal.test.types;

import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.scout.commons.holders.IntegerHolder;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.extensions.targetpackage.IDefaultTargetPackage;
import org.eclipse.scout.sdk.internal.test.AbstractScoutSdkTest;
import org.eclipse.scout.sdk.operation.form.FormNewOperation;
import org.eclipse.scout.sdk.operation.service.ServiceNewOperation;
import org.eclipse.scout.sdk.operation.service.ServiceRegistrationDescription;
import org.eclipse.scout.sdk.testing.SdkAssert;
import org.eclipse.scout.sdk.util.signature.SignatureCache;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ICachedTypeHierarchy;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchy;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchyChangedListener;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeComparators;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeFilters;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeUtility;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <h1>TypeHierarchyTest</h1>
 * <p>
 */
public class TypeHierarchyTest1 extends AbstractScoutSdkTest {

  private static final String BUNDLE_NAME_CLIENT = "test.client";
  private static final String BUNDLE_NAME_SHARED = "test.shared";
  private static final String BUNDLE_NAME_SERVER = "test.server";

  @BeforeClass
  public static void setUpWorkspace() throws Exception {
    setupWorkspace("resources/util/typeCache", BUNDLE_NAME_CLIENT, BUNDLE_NAME_SHARED, BUNDLE_NAME_SERVER);
  }

  @Test
  public void testPrimaryTypeHierarchy() {
    IType companyForm = SdkAssert.assertTypeExists("test.client.ui.forms.CompanyForm");
    IType iformField = TypeUtility.getType(RuntimeClasses.IFormField);
    ITypeHierarchy hierarchy = TypeUtility.getLocalTypeHierarchy(companyForm);

    IType mainBox = SdkAssert.assertTypeExists(companyForm, "MainBox");
    Set<IType> ff = TypeUtility.getInnerTypes(mainBox, TypeFilters.getSubtypeFilter(iformField, hierarchy), ScoutTypeComparators.getOrderAnnotationComparator());
    IType[] formFields = ff.toArray(new IType[ff.size()]);
    Assert.assertTrue(formFields.length == 3);
    Assert.assertEquals(formFields[0].getElementName(), "NameField");
    Assert.assertEquals(formFields[1].getElementName(), "SinceField");
    Assert.assertEquals(formFields[2].getElementName(), "DetailsGroup");
  }

  @Test
  public void testFormHierarchy() throws Exception {
    final IJavaProject project = JavaCore.create(getProject(BUNDLE_NAME_CLIENT));
    final IScoutBundle sb = ScoutTypeUtility.getScoutBundle(project);
    final IType iForm = TypeUtility.getType(RuntimeClasses.IForm);
    final ICachedTypeHierarchy formHierarchy = TypeUtility.getPrimaryTypeHierarchy(iForm);
    Set<IType> subtypes = formHierarchy.getAllSubtypes(iForm, ScoutTypeFilters.getClassesInScoutBundles(sb));
    Assert.assertEquals(1, subtypes.size());
    final IntegerHolder formCountHolder = new IntegerHolder(-1);
    ITypeHierarchyChangedListener listener = new ITypeHierarchyChangedListener() {
      @Override
      public void hierarchyInvalidated() {
        formCountHolder.setValue(formHierarchy.getAllSubtypes(iForm, ScoutTypeFilters.getClassesInScoutBundles(sb)).size());
        synchronized (formCountHolder) {
          formCountHolder.notifyAll();
        }
      }
    };
    try {
      formHierarchy.addHierarchyListener(listener);
      IScoutBundle client = ScoutTypeUtility.getScoutBundle(project.getProject());
      SdkAssert.assertNotNull(client);
      FormNewOperation formOp = new FormNewOperation("ANewForm", client.getPackageName(".ui.forms"), client.getJavaProject());
      formOp.setSuperTypeSignature(RuntimeClasses.getSuperTypeSignature(RuntimeClasses.IForm, project));
      executeBuildAssertNoCompileErrors(formOp);
      synchronized (formCountHolder) {
        while (formCountHolder.getValue() == -1) {
          formCountHolder.wait();
        }
      }
      // expect created form
      Assert.assertEquals(2, formCountHolder.getValue().intValue());
    }
    finally {
      formHierarchy.removeHierarchyListener(listener);
    }
  }

  @Test
  public void testCreateNewService() throws Exception {
    final IType iService = TypeUtility.getType(RuntimeClasses.IService);
    final ICachedTypeHierarchy serviceHierarchy = TypeUtility.getPrimaryTypeHierarchy(iService);
    Set<IType> subtypes = serviceHierarchy.getAllSubtypes(iService, TypeFilters.getInWorkspaceFilter());
    Assert.assertEquals(2, subtypes.size());
    final IntegerHolder serviceCountHolder = new IntegerHolder(-1);
    ITypeHierarchyChangedListener listener = new ITypeHierarchyChangedListener() {
      @Override
      public void hierarchyInvalidated() {
        serviceCountHolder.setValue(serviceHierarchy.getAllSubtypes(iService, TypeFilters.getInWorkspaceFilter()).size());
        synchronized (serviceCountHolder) {
          serviceCountHolder.notifyAll();
        }
      }
    };
    try {
      serviceHierarchy.addHierarchyListener(listener);
      IScoutBundle clientBundle = ScoutTypeUtility.getScoutBundle(getProject(BUNDLE_NAME_CLIENT));
      SdkAssert.assertNotNull(clientBundle);
      IScoutBundle sharedBundle = ScoutTypeUtility.getScoutBundle(getProject(BUNDLE_NAME_SHARED));
      SdkAssert.assertNotNull(sharedBundle);
      IScoutBundle serverBundle = ScoutTypeUtility.getScoutBundle(getProject(BUNDLE_NAME_SERVER));
      SdkAssert.assertNotNull(serverBundle);
      ServiceNewOperation serviceOp = new ServiceNewOperation("ITestService", "TestService");
      serviceOp.addProxyRegistrationProject(clientBundle.getJavaProject());
      serviceOp.addServiceRegistration(new ServiceRegistrationDescription(serverBundle.getJavaProject()));
      serviceOp.setImplementationProject(serverBundle.getJavaProject());
      serviceOp.setInterfaceProject(sharedBundle.getJavaProject());
      serviceOp.addInterfaceInterfaceSignature(SignatureCache.createTypeSignature(RuntimeClasses.IService));
      serviceOp.setInterfacePackageName(sharedBundle.getDefaultPackage(IDefaultTargetPackage.SHARED_SERVICES) + ".notexisting");
      serviceOp.setImplementationPackageName(serverBundle.getDefaultPackage(IDefaultTargetPackage.SERVER_SERVICES) + ".notexisting");
      serviceOp.setImplementationSuperTypeSignature(RuntimeClasses.getSuperTypeSignature(RuntimeClasses.IService, serverBundle.getJavaProject()));
      executeBuildAssertNoCompileErrors(serviceOp);
      synchronized (serviceCountHolder) {
        while (serviceCountHolder.getValue() == -1) {
          serviceCountHolder.wait();
        }
      }
      // expect created form
      Assert.assertEquals(4, serviceCountHolder.getValue().intValue());
    }
    finally {
      serviceHierarchy.removeHierarchyListener(listener);
    }
  }

  @AfterClass
  public static void cleanUp() throws Exception {
    clearWorkspace();
  }
}
