/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.s.dto;

import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertFieldExist;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertFieldType;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertHasFlags;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertHasSuperClass;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertMethodExist;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertMethodReturnType;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertTypeExists;
import static org.eclipse.scout.sdk.core.s.testing.ScoutFixtureHelper.createFormDataAssertNoCompileErrors;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.scout.sdk.core.java.model.api.IType;
import org.junit.jupiter.api.Test;

import formdata.client.ui.forms.MasterFieldTestForm;

/**
 * <h3>{@link MasterFieldFormDataTest}</h3>
 *
 * @since 3.10.0 2013-11-04
 */
public class MasterFieldFormDataTest {
  @Test
  public void testCreateFormData() {
    createFormDataAssertNoCompileErrors(MasterFieldTestForm.class.getName(), MasterFieldFormDataTest::testApiOfMasterFieldTestFormData);
  }

  /**
   * @Generated with org.eclipse.scout.sdk.testing.codegen.ApiTestGenerator
   */
  private static void testApiOfMasterFieldTestFormData(IType masterFieldTestFormData) {
    // type MasterFieldTestFormData
    assertHasFlags(masterFieldTestFormData, 1);
    assertHasSuperClass(masterFieldTestFormData, "org.eclipse.scout.rt.shared.data.form.AbstractFormData");

    // fields of MasterFieldTestFormData
    assertEquals(1, masterFieldTestFormData.fields().stream().count(), "field count of 'MasterFieldTestFormData'");
    var serialVersionUID = assertFieldExist(masterFieldTestFormData, "serialVersionUID");
    assertHasFlags(serialVersionUID, 26);
    assertFieldType(serialVersionUID, "long");

    assertEquals(2, masterFieldTestFormData.methods().stream().count(), "method count of 'MasterFieldTestFormData'");
    var getMyMaster = assertMethodExist(masterFieldTestFormData, "getMyMaster");
    assertMethodReturnType(getMyMaster, "formdata.shared.services.MasterFieldTestFormData$MyMaster");
    var getMySlave = assertMethodExist(masterFieldTestFormData, "getMySlave");
    assertMethodReturnType(getMySlave, "formdata.shared.services.MasterFieldTestFormData$MySlave");

    assertEquals(2, masterFieldTestFormData.innerTypes().stream().count(), "inner types count of 'MasterFieldTestFormData'");
    // type MyMaster
    var myMaster = assertTypeExists(masterFieldTestFormData, "MyMaster");
    assertHasFlags(myMaster, 9);
    assertHasSuperClass(myMaster, "org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData<java.lang.String>");

    // fields of MyMaster
    assertEquals(1, myMaster.fields().stream().count(), "field count of 'MyMaster'");
    var serialVersionUID1 = assertFieldExist(myMaster, "serialVersionUID");
    assertHasFlags(serialVersionUID1, 26);
    assertFieldType(serialVersionUID1, "long");

    assertEquals(0, myMaster.methods().stream().count(), "method count of 'MyMaster'");

    assertEquals(0, myMaster.innerTypes().stream().count(), "inner types count of 'MyMaster'");
    // type MySlave
    var mySlave = assertTypeExists(masterFieldTestFormData, "MySlave");
    assertHasFlags(mySlave, 9);
    assertHasSuperClass(mySlave, "org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData<java.lang.String>");

    // fields of MySlave
    assertEquals(1, mySlave.fields().stream().count(), "field count of 'MySlave'");
    var serialVersionUID2 = assertFieldExist(mySlave, "serialVersionUID");
    assertHasFlags(serialVersionUID2, 26);
    assertFieldType(serialVersionUID2, "long");

    assertEquals(0, mySlave.methods().stream().count(), "method count of 'MySlave'");

    assertEquals(0, mySlave.innerTypes().stream().count(), "inner types count of 'MySlave'");
  }
}
