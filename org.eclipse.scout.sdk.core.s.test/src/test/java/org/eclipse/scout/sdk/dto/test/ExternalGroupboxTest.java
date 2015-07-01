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
package org.eclipse.scout.sdk.dto.test;

import org.eclipse.scout.sdk.core.model.IField;
import org.eclipse.scout.sdk.core.model.IMethod;
import org.eclipse.scout.sdk.core.model.IType;
import org.eclipse.scout.sdk.core.testing.SdkAssert;
import org.eclipse.scout.sdk.dto.test.util.CoreScoutTestingUtils;
import org.junit.Assert;
import org.junit.Test;

public class ExternalGroupboxTest {

  @Test
  public void testCreateFormData() {
    String formName = "AbstractExternalGroupBox";
    IType dto = CoreScoutTestingUtils.createFormDataAssertNoCompileErrors("formdata.client.ui.template.formfield." + formName);
    testApiOfAbstractExternalGroupBoxData(dto);
  }

  /**
   * @Generated with org.eclipse.scout.sdk.testing.codegen.ApiTestGenerator
   */
  private static void testApiOfAbstractExternalGroupBoxData(IType abstractExternalGroupBoxData) {
    // type AbstractExternalGroupBoxData
    SdkAssert.assertHasFlags(abstractExternalGroupBoxData, 1025);
    SdkAssert.assertHasSuperTypeSignature(abstractExternalGroupBoxData, "QAbstractFormFieldData;");

    // fields of AbstractExternalGroupBoxData
    Assert.assertEquals("field count of 'AbstractExternalGroupBoxData'", 1, abstractExternalGroupBoxData.getFields().size());
    IField serialVersionUID = SdkAssert.assertFieldExist(abstractExternalGroupBoxData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID, 26);
    SdkAssert.assertFieldSignature(serialVersionUID, "J");

    Assert.assertEquals("method count of 'AbstractExternalGroupBoxData'", 2, abstractExternalGroupBoxData.getMethods().size());
    IMethod abstractExternalGroupBoxData1 = SdkAssert.assertMethodExist(abstractExternalGroupBoxData, "AbstractExternalGroupBoxData", new String[]{});
    Assert.assertTrue(abstractExternalGroupBoxData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(abstractExternalGroupBoxData1, null);
    IMethod getExternalString = SdkAssert.assertMethodExist(abstractExternalGroupBoxData, "getExternalString", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getExternalString, "QExternalString;");

    Assert.assertEquals("inner types count of 'AbstractExternalGroupBoxData'", 1, abstractExternalGroupBoxData.getTypes().size());
    // type ExternalString
    IType externalString = SdkAssert.assertTypeExists(abstractExternalGroupBoxData, "ExternalString");
    SdkAssert.assertHasFlags(externalString, 9);
    SdkAssert.assertHasSuperTypeSignature(externalString, "QAbstractValueFieldData<QString;>;");

    // fields of ExternalString
    Assert.assertEquals("field count of 'ExternalString'", 1, externalString.getFields().size());
    IField serialVersionUID1 = SdkAssert.assertFieldExist(externalString, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID1, 26);
    SdkAssert.assertFieldSignature(serialVersionUID1, "J");

    Assert.assertEquals("method count of 'ExternalString'", 1, externalString.getMethods().size());
    IMethod externalString1 = SdkAssert.assertMethodExist(externalString, "ExternalString", new String[]{});
    Assert.assertTrue(externalString1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(externalString1, null);

    Assert.assertEquals("inner types count of 'ExternalString'", 0, externalString.getTypes().size());
  }
}
