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

import formdata.client.ui.forms.IgnoredFieldsForm;

public class IgnoredFieldsFormTest {

  @Test
  public void testCreateFormData() {
    createFormDataAssertNoCompileErrors(IgnoredFieldsForm.class.getName(), IgnoredFieldsFormTest::testApiOfIgnoredFieldsFormData);
  }

  /**
   * @Generated with org.eclipse.scout.sdk.testing.codegen.ApiTestGenerator
   */
  private static void testApiOfIgnoredFieldsFormData(IType ignoredFieldsFormData) {
    // type IgnoredFieldsFormData
    assertHasFlags(ignoredFieldsFormData, 1);
    assertHasSuperClass(ignoredFieldsFormData, "org.eclipse.scout.rt.shared.data.form.AbstractFormData");

    // fields of IgnoredFieldsFormData
    assertEquals(1, ignoredFieldsFormData.fields().stream().count(), "field count of 'IgnoredFieldsFormData'");
    var serialVersionUID = assertFieldExist(ignoredFieldsFormData, "serialVersionUID");
    assertHasFlags(serialVersionUID, 26);
    assertFieldType(serialVersionUID, "long");

    assertEquals(1, ignoredFieldsFormData.methods().stream().count(), "method count of 'IgnoredFieldsFormData'");
    var getNotIgnored = assertMethodExist(ignoredFieldsFormData, "getNotIgnored");
    assertMethodReturnType(getNotIgnored, "formdata.shared.services.process.IgnoredFieldsFormData$NotIgnored");

    assertEquals(1, ignoredFieldsFormData.innerTypes().stream().count(), "inner types count of 'IgnoredFieldsFormData'");
    // type NotIgnored
    var notIgnored = assertTypeExists(ignoredFieldsFormData, "NotIgnored");
    assertHasFlags(notIgnored, 9);
    assertHasSuperClass(notIgnored, "org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData<java.lang.String>");

    // fields of NotIgnored
    assertEquals(1, notIgnored.fields().stream().count(), "field count of 'NotIgnored'");
    var serialVersionUID1 = assertFieldExist(notIgnored, "serialVersionUID");
    assertHasFlags(serialVersionUID1, 26);
    assertFieldType(serialVersionUID1, "long");

    assertEquals(0, notIgnored.methods().stream().count(), "method count of 'NotIgnored'");

    assertEquals(0, notIgnored.innerTypes().stream().count(), "inner types count of 'NotIgnored'");
  }
}
