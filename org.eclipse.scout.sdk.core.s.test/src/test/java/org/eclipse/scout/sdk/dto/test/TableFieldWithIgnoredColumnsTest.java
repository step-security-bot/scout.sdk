/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
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
import org.junit.Test;

/**
 * <h3>{@link TableFieldWithIgnoredColumnsTest}</h3>
 *
 * @author Andreas Hoegger
 * @since 3.10.0 19.08.2013
 */
public class TableFieldWithIgnoredColumnsTest {

  public static final String TableFieldWithIgnoredColumnsBaseForm = "formdata.client.ui.forms.replace.TableFieldWithIgnoredColumnsBaseForm";
  public static final String TableFieldWithIgnoredColumnsDefaultExForm = "formdata.client.ui.forms.replace.TableFieldWithIgnoredColumnsDefaultExForm";
  public static final String TableFieldWithIgnoredColumnsCreateExForm = "formdata.client.ui.forms.replace.TableFieldWithIgnoredColumnsCreateExForm";
  public static final String TableFieldWithIgnoredColumnsIgnoreExForm = "formdata.client.ui.forms.replace.TableFieldWithIgnoredColumnsIgnoreExForm";
  public static final String TableFieldWithIgnoredColumnsDefaultCreateExForm = "formdata.client.ui.forms.replace.TableFieldWithIgnoredColumnsDefaultCreateExForm";

  @Test
  public void testTableFieldWithIgnoredColumnsBaseForm() throws Exception {
    IType dto = CoreScoutTestingUtils.createFormDataAssertNoCompileErrors(TableFieldWithIgnoredColumnsBaseForm);
    testApiOfTableFieldWithIgnoredColumnsBaseFormData(dto);
  }

  @Test
  public void testTableFieldWithIgnoredColumnsDefaultExForm() throws Exception {
    IType dto = CoreScoutTestingUtils.createFormDataAssertNoCompileErrors(TableFieldWithIgnoredColumnsDefaultExForm);
    testApiOfTableFieldWithIgnoredColumnsDefaultExFormData(dto);
  }

  @Test
  public void testTableFieldWithIgnoredColumnsCreateExForm() throws Exception {
    IType dto = CoreScoutTestingUtils.createFormDataAssertNoCompileErrors(TableFieldWithIgnoredColumnsCreateExForm);
    testApiOfTableFieldWithIgnoredColumnsCreateExFormData(dto);
  }

  @Test
  public void testTableFieldWithIgnoredColumnsIgnoreExForm() throws Exception {
    IType dto = CoreScoutTestingUtils.createFormDataAssertNoCompileErrors(TableFieldWithIgnoredColumnsIgnoreExForm);
    testApiOfTableFieldWithIgnoredColumnsIgnoreExFormData(dto);
  }

  @Test
  public void testTableFieldWithIgnoredColumnsDefaultCreateExForm() throws Exception {
    IType dto = CoreScoutTestingUtils.createFormDataAssertNoCompileErrors(TableFieldWithIgnoredColumnsDefaultCreateExForm);
    testApiOfTableFieldWithIgnoredColumnsDefaultCreateExFormData(dto);
  }

  /**
   * @Generated with org.eclipse.scout.sdk.testing.codegen.ApiTestGenerator
   */
  private void testApiOfTableFieldWithIgnoredColumnsDefaultExFormData(IType tableFieldWithIgnoredColumnsDefaultExFormData) throws Exception {
    // type TableFieldWithIgnoredColumnsDefaultExFormData
    SdkAssert.assertHasFlags(tableFieldWithIgnoredColumnsDefaultExFormData, 1);
    SdkAssert.assertHasSuperTypeSignature(tableFieldWithIgnoredColumnsDefaultExFormData, "QTableFieldWithIgnoredColumnsBaseFormData;");
    SdkAssert.assertAnnotation(tableFieldWithIgnoredColumnsDefaultExFormData, "javax.annotation.Generated");

    // fields of TableFieldWithIgnoredColumnsDefaultExFormData
    SdkAssert.assertEquals("field count of 'TableFieldWithIgnoredColumnsDefaultExFormData'", 1, tableFieldWithIgnoredColumnsDefaultExFormData.getFields().size());
    IField serialVersionUID = SdkAssert.assertFieldExist(tableFieldWithIgnoredColumnsDefaultExFormData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID, 26);
    SdkAssert.assertFieldSignature(serialVersionUID, "J");

    SdkAssert.assertEquals("method count of 'TableFieldWithIgnoredColumnsDefaultExFormData'", 2, tableFieldWithIgnoredColumnsDefaultExFormData.getMethods().size());
    IMethod tableFieldWithIgnoredColumnsDefaultExFormData1 = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsDefaultExFormData, "TableFieldWithIgnoredColumnsDefaultExFormData", new String[]{});
    SdkAssert.assertTrue(tableFieldWithIgnoredColumnsDefaultExFormData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableFieldWithIgnoredColumnsDefaultExFormData1, null);
    IMethod getTableDefaultEx = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsDefaultExFormData, "getTableDefaultEx", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getTableDefaultEx, "QTableDefaultEx;");

    SdkAssert.assertEquals("inner types count of 'TableFieldWithIgnoredColumnsDefaultExFormData'", 1, tableFieldWithIgnoredColumnsDefaultExFormData.getTypes().size());
    // type TableDefaultEx
    IType tableDefaultEx = SdkAssert.assertTypeExists(tableFieldWithIgnoredColumnsDefaultExFormData, "TableDefaultEx");
    SdkAssert.assertHasFlags(tableDefaultEx, 9);
    SdkAssert.assertHasSuperTypeSignature(tableDefaultEx, "QTableBase;");
    SdkAssert.assertAnnotation(tableDefaultEx, "org.eclipse.scout.commons.annotations.Replace");

    // fields of TableDefaultEx
    SdkAssert.assertEquals("field count of 'TableDefaultEx'", 1, tableDefaultEx.getFields().size());
    IField serialVersionUID1 = SdkAssert.assertFieldExist(tableDefaultEx, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID1, 26);
    SdkAssert.assertFieldSignature(serialVersionUID1, "J");

    SdkAssert.assertEquals("method count of 'TableDefaultEx'", 8, tableDefaultEx.getMethods().size());
    IMethod tableDefaultEx1 = SdkAssert.assertMethodExist(tableDefaultEx, "TableDefaultEx", new String[]{});
    SdkAssert.assertTrue(tableDefaultEx1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableDefaultEx1, null);
    IMethod addRow = SdkAssert.assertMethodExist(tableDefaultEx, "addRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(addRow, "QTableDefaultExRowData;");
    SdkAssert.assertAnnotation(addRow, "java.lang.Override");
    IMethod addRow1 = SdkAssert.assertMethodExist(tableDefaultEx, "addRow", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(addRow1, "QTableDefaultExRowData;");
    SdkAssert.assertAnnotation(addRow1, "java.lang.Override");
    IMethod createRow = SdkAssert.assertMethodExist(tableDefaultEx, "createRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(createRow, "QTableDefaultExRowData;");
    SdkAssert.assertAnnotation(createRow, "java.lang.Override");
    IMethod getRowType = SdkAssert.assertMethodExist(tableDefaultEx, "getRowType", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRowType, "QClass<+QAbstractTableRowData;>;");
    SdkAssert.assertAnnotation(getRowType, "java.lang.Override");
    IMethod getRows = SdkAssert.assertMethodExist(tableDefaultEx, "getRows", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRows, "[QTableDefaultExRowData;");
    SdkAssert.assertAnnotation(getRows, "java.lang.Override");
    IMethod rowAt = SdkAssert.assertMethodExist(tableDefaultEx, "rowAt", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(rowAt, "QTableDefaultExRowData;");
    SdkAssert.assertAnnotation(rowAt, "java.lang.Override");
    IMethod setRows = SdkAssert.assertMethodExist(tableDefaultEx, "setRows", new String[]{"[QTableDefaultExRowData;"});
    SdkAssert.assertMethodReturnTypeSignature(setRows, "V");

    SdkAssert.assertEquals("inner types count of 'TableDefaultEx'", 1, tableDefaultEx.getTypes().size());
    // type TableDefaultExRowData
    IType tableDefaultExRowData = SdkAssert.assertTypeExists(tableDefaultEx, "TableDefaultExRowData");
    SdkAssert.assertHasFlags(tableDefaultExRowData, 9);
    SdkAssert.assertHasSuperTypeSignature(tableDefaultExRowData, "QTableBaseRowData;");

    // fields of TableDefaultExRowData
    SdkAssert.assertEquals("field count of 'TableDefaultExRowData'", 1, tableDefaultExRowData.getFields().size());
    IField serialVersionUID2 = SdkAssert.assertFieldExist(tableDefaultExRowData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID2, 26);
    SdkAssert.assertFieldSignature(serialVersionUID2, "J");

    SdkAssert.assertEquals("method count of 'TableDefaultExRowData'", 1, tableDefaultExRowData.getMethods().size());
    IMethod tableDefaultExRowData1 = SdkAssert.assertMethodExist(tableDefaultExRowData, "TableDefaultExRowData", new String[]{});
    SdkAssert.assertTrue(tableDefaultExRowData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableDefaultExRowData1, null);

    SdkAssert.assertEquals("inner types count of 'TableDefaultExRowData'", 0, tableDefaultExRowData.getTypes().size());
  }

  /**
   * @Generated with org.eclipse.scout.sdk.testing.codegen.ApiTestGenerator
   */
  private void testApiOfTableFieldWithIgnoredColumnsBaseFormData(IType tableFieldWithIgnoredColumnsBaseFormData) throws Exception {
    // type TableFieldWithIgnoredColumnsBaseFormData
    SdkAssert.assertHasFlags(tableFieldWithIgnoredColumnsBaseFormData, 1);
    SdkAssert.assertHasSuperTypeSignature(tableFieldWithIgnoredColumnsBaseFormData, "QAbstractFormData;");
    SdkAssert.assertAnnotation(tableFieldWithIgnoredColumnsBaseFormData, "javax.annotation.Generated");

    // fields of TableFieldWithIgnoredColumnsBaseFormData
    SdkAssert.assertEquals("field count of 'TableFieldWithIgnoredColumnsBaseFormData'", 1, tableFieldWithIgnoredColumnsBaseFormData.getFields().size());
    IField serialVersionUID = SdkAssert.assertFieldExist(tableFieldWithIgnoredColumnsBaseFormData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID, 26);
    SdkAssert.assertFieldSignature(serialVersionUID, "J");

    SdkAssert.assertEquals("method count of 'TableFieldWithIgnoredColumnsBaseFormData'", 2, tableFieldWithIgnoredColumnsBaseFormData.getMethods().size());
    IMethod tableFieldWithIgnoredColumnsBaseFormData1 = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsBaseFormData, "TableFieldWithIgnoredColumnsBaseFormData", new String[]{});
    SdkAssert.assertTrue(tableFieldWithIgnoredColumnsBaseFormData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableFieldWithIgnoredColumnsBaseFormData1, null);
    IMethod getTableBase = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsBaseFormData, "getTableBase", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getTableBase, "QTableBase;");

    SdkAssert.assertEquals("inner types count of 'TableFieldWithIgnoredColumnsBaseFormData'", 1, tableFieldWithIgnoredColumnsBaseFormData.getTypes().size());
    // type TableBase
    IType tableBase = SdkAssert.assertTypeExists(tableFieldWithIgnoredColumnsBaseFormData, "TableBase");
    SdkAssert.assertHasFlags(tableBase, 9);
    SdkAssert.assertHasSuperTypeSignature(tableBase, "QAbstractTableFieldBeanData;");

    // fields of TableBase
    SdkAssert.assertEquals("field count of 'TableBase'", 1, tableBase.getFields().size());
    IField serialVersionUID1 = SdkAssert.assertFieldExist(tableBase, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID1, 26);
    SdkAssert.assertFieldSignature(serialVersionUID1, "J");

    SdkAssert.assertEquals("method count of 'TableBase'", 8, tableBase.getMethods().size());
    IMethod tableBase1 = SdkAssert.assertMethodExist(tableBase, "TableBase", new String[]{});
    SdkAssert.assertTrue(tableBase1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableBase1, null);
    IMethod addRow = SdkAssert.assertMethodExist(tableBase, "addRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(addRow, "QTableBaseRowData;");
    SdkAssert.assertAnnotation(addRow, "java.lang.Override");
    IMethod addRow1 = SdkAssert.assertMethodExist(tableBase, "addRow", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(addRow1, "QTableBaseRowData;");
    SdkAssert.assertAnnotation(addRow1, "java.lang.Override");
    IMethod createRow = SdkAssert.assertMethodExist(tableBase, "createRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(createRow, "QTableBaseRowData;");
    SdkAssert.assertAnnotation(createRow, "java.lang.Override");
    IMethod getRowType = SdkAssert.assertMethodExist(tableBase, "getRowType", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRowType, "QClass<+QAbstractTableRowData;>;");
    SdkAssert.assertAnnotation(getRowType, "java.lang.Override");
    IMethod getRows = SdkAssert.assertMethodExist(tableBase, "getRows", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRows, "[QTableBaseRowData;");
    SdkAssert.assertAnnotation(getRows, "java.lang.Override");
    IMethod rowAt = SdkAssert.assertMethodExist(tableBase, "rowAt", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(rowAt, "QTableBaseRowData;");
    SdkAssert.assertAnnotation(rowAt, "java.lang.Override");
    IMethod setRows = SdkAssert.assertMethodExist(tableBase, "setRows", new String[]{"[QTableBaseRowData;"});
    SdkAssert.assertMethodReturnTypeSignature(setRows, "V");

    SdkAssert.assertEquals("inner types count of 'TableBase'", 1, tableBase.getTypes().size());
    // type TableBaseRowData
    IType tableBaseRowData = SdkAssert.assertTypeExists(tableBase, "TableBaseRowData");
    SdkAssert.assertHasFlags(tableBaseRowData, 9);
    SdkAssert.assertHasSuperTypeSignature(tableBaseRowData, "QAbstractTableRowData;");

    // fields of TableBaseRowData
    SdkAssert.assertEquals("field count of 'TableBaseRowData'", 5, tableBaseRowData.getFields().size());
    IField serialVersionUID2 = SdkAssert.assertFieldExist(tableBaseRowData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID2, 26);
    SdkAssert.assertFieldSignature(serialVersionUID2, "J");
    IField default_ = SdkAssert.assertFieldExist(tableBaseRowData, "default_");
    SdkAssert.assertHasFlags(default_, 25);
    SdkAssert.assertFieldSignature(default_, "QString;");
    IField create = SdkAssert.assertFieldExist(tableBaseRowData, "create");
    SdkAssert.assertHasFlags(create, 25);
    SdkAssert.assertFieldSignature(create, "QString;");
    IField m_default = SdkAssert.assertFieldExist(tableBaseRowData, "m_default");
    SdkAssert.assertHasFlags(m_default, 2);
    SdkAssert.assertFieldSignature(m_default, "QString;");
    IField m_create = SdkAssert.assertFieldExist(tableBaseRowData, "m_create");
    SdkAssert.assertHasFlags(m_create, 2);
    SdkAssert.assertFieldSignature(m_create, "QString;");

    SdkAssert.assertEquals("method count of 'TableBaseRowData'", 5, tableBaseRowData.getMethods().size());
    IMethod tableBaseRowData1 = SdkAssert.assertMethodExist(tableBaseRowData, "TableBaseRowData", new String[]{});
    SdkAssert.assertTrue(tableBaseRowData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableBaseRowData1, null);
    IMethod getDefault = SdkAssert.assertMethodExist(tableBaseRowData, "getDefault", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getDefault, "QString;");
    IMethod setDefault = SdkAssert.assertMethodExist(tableBaseRowData, "setDefault", new String[]{"QString;"});
    SdkAssert.assertMethodReturnTypeSignature(setDefault, "V");
    IMethod getCreate = SdkAssert.assertMethodExist(tableBaseRowData, "getCreate", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getCreate, "QString;");
    IMethod setCreate = SdkAssert.assertMethodExist(tableBaseRowData, "setCreate", new String[]{"QString;"});
    SdkAssert.assertMethodReturnTypeSignature(setCreate, "V");

    SdkAssert.assertEquals("inner types count of 'TableBaseRowData'", 0, tableBaseRowData.getTypes().size());
  }

  /**
   * @Generated with org.eclipse.scout.sdk.testing.codegen.ApiTestGenerator
   */
  private void testApiOfTableFieldWithIgnoredColumnsIgnoreExFormData(IType tableFieldWithIgnoredColumnsIgnoreExFormData) throws Exception {
    // type TableFieldWithIgnoredColumnsIgnoreExFormData
    SdkAssert.assertHasFlags(tableFieldWithIgnoredColumnsIgnoreExFormData, 1);
    SdkAssert.assertHasSuperTypeSignature(tableFieldWithIgnoredColumnsIgnoreExFormData, "QTableFieldWithIgnoredColumnsBaseFormData;");
    SdkAssert.assertAnnotation(tableFieldWithIgnoredColumnsIgnoreExFormData, "javax.annotation.Generated");

    // fields of TableFieldWithIgnoredColumnsIgnoreExFormData
    SdkAssert.assertEquals("field count of 'TableFieldWithIgnoredColumnsIgnoreExFormData'", 1, tableFieldWithIgnoredColumnsIgnoreExFormData.getFields().size());
    IField serialVersionUID = SdkAssert.assertFieldExist(tableFieldWithIgnoredColumnsIgnoreExFormData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID, 26);
    SdkAssert.assertFieldSignature(serialVersionUID, "J");

    SdkAssert.assertEquals("method count of 'TableFieldWithIgnoredColumnsIgnoreExFormData'", 2, tableFieldWithIgnoredColumnsIgnoreExFormData.getMethods().size());
    IMethod tableFieldWithIgnoredColumnsIgnoreExFormData1 = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsIgnoreExFormData, "TableFieldWithIgnoredColumnsIgnoreExFormData", new String[]{});
    SdkAssert.assertTrue(tableFieldWithIgnoredColumnsIgnoreExFormData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableFieldWithIgnoredColumnsIgnoreExFormData1, null);
    IMethod getTableIgnoreEx = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsIgnoreExFormData, "getTableIgnoreEx", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getTableIgnoreEx, "QTableIgnoreEx;");

    SdkAssert.assertEquals("inner types count of 'TableFieldWithIgnoredColumnsIgnoreExFormData'", 1, tableFieldWithIgnoredColumnsIgnoreExFormData.getTypes().size());
    // type TableIgnoreEx
    IType tableIgnoreEx = SdkAssert.assertTypeExists(tableFieldWithIgnoredColumnsIgnoreExFormData, "TableIgnoreEx");
    SdkAssert.assertHasFlags(tableIgnoreEx, 9);
    SdkAssert.assertHasSuperTypeSignature(tableIgnoreEx, "QTableBase;");
    SdkAssert.assertAnnotation(tableIgnoreEx, "org.eclipse.scout.commons.annotations.Replace");

    // fields of TableIgnoreEx
    SdkAssert.assertEquals("field count of 'TableIgnoreEx'", 1, tableIgnoreEx.getFields().size());
    IField serialVersionUID1 = SdkAssert.assertFieldExist(tableIgnoreEx, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID1, 26);
    SdkAssert.assertFieldSignature(serialVersionUID1, "J");

    SdkAssert.assertEquals("method count of 'TableIgnoreEx'", 8, tableIgnoreEx.getMethods().size());
    IMethod tableIgnoreEx1 = SdkAssert.assertMethodExist(tableIgnoreEx, "TableIgnoreEx", new String[]{});
    SdkAssert.assertTrue(tableIgnoreEx1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableIgnoreEx1, null);
    IMethod addRow = SdkAssert.assertMethodExist(tableIgnoreEx, "addRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(addRow, "QTableIgnoreExRowData;");
    SdkAssert.assertAnnotation(addRow, "java.lang.Override");
    IMethod addRow1 = SdkAssert.assertMethodExist(tableIgnoreEx, "addRow", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(addRow1, "QTableIgnoreExRowData;");
    SdkAssert.assertAnnotation(addRow1, "java.lang.Override");
    IMethod createRow = SdkAssert.assertMethodExist(tableIgnoreEx, "createRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(createRow, "QTableIgnoreExRowData;");
    SdkAssert.assertAnnotation(createRow, "java.lang.Override");
    IMethod getRowType = SdkAssert.assertMethodExist(tableIgnoreEx, "getRowType", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRowType, "QClass<+QAbstractTableRowData;>;");
    SdkAssert.assertAnnotation(getRowType, "java.lang.Override");
    IMethod getRows = SdkAssert.assertMethodExist(tableIgnoreEx, "getRows", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRows, "[QTableIgnoreExRowData;");
    SdkAssert.assertAnnotation(getRows, "java.lang.Override");
    IMethod rowAt = SdkAssert.assertMethodExist(tableIgnoreEx, "rowAt", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(rowAt, "QTableIgnoreExRowData;");
    SdkAssert.assertAnnotation(rowAt, "java.lang.Override");
    IMethod setRows = SdkAssert.assertMethodExist(tableIgnoreEx, "setRows", new String[]{"[QTableIgnoreExRowData;"});
    SdkAssert.assertMethodReturnTypeSignature(setRows, "V");

    SdkAssert.assertEquals("inner types count of 'TableIgnoreEx'", 1, tableIgnoreEx.getTypes().size());
    // type TableIgnoreExRowData
    IType tableIgnoreExRowData = SdkAssert.assertTypeExists(tableIgnoreEx, "TableIgnoreExRowData");
    SdkAssert.assertHasFlags(tableIgnoreExRowData, 9);
    SdkAssert.assertHasSuperTypeSignature(tableIgnoreExRowData, "QTableBaseRowData;");

    // fields of TableIgnoreExRowData
    SdkAssert.assertEquals("field count of 'TableIgnoreExRowData'", 1, tableIgnoreExRowData.getFields().size());
    IField serialVersionUID2 = SdkAssert.assertFieldExist(tableIgnoreExRowData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID2, 26);
    SdkAssert.assertFieldSignature(serialVersionUID2, "J");

    SdkAssert.assertEquals("method count of 'TableIgnoreExRowData'", 1, tableIgnoreExRowData.getMethods().size());
    IMethod tableIgnoreExRowData1 = SdkAssert.assertMethodExist(tableIgnoreExRowData, "TableIgnoreExRowData", new String[]{});
    SdkAssert.assertTrue(tableIgnoreExRowData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableIgnoreExRowData1, null);

    SdkAssert.assertEquals("inner types count of 'TableIgnoreExRowData'", 0, tableIgnoreExRowData.getTypes().size());
  }

  /**
   * @Generated with org.eclipse.scout.sdk.testing.codegen.ApiTestGenerator
   */
  private void testApiOfTableFieldWithIgnoredColumnsDefaultCreateExFormData(IType tableFieldWithIgnoredColumnsDefaultCreateExFormData) throws Exception {
    // type TableFieldWithIgnoredColumnsDefaultCreateExFormData
    SdkAssert.assertHasFlags(tableFieldWithIgnoredColumnsDefaultCreateExFormData, 1);
    SdkAssert.assertHasSuperTypeSignature(tableFieldWithIgnoredColumnsDefaultCreateExFormData, "QTableFieldWithIgnoredColumnsBaseFormData;");
    SdkAssert.assertAnnotation(tableFieldWithIgnoredColumnsDefaultCreateExFormData, "javax.annotation.Generated");

    // fields of TableFieldWithIgnoredColumnsDefaultCreateExFormData
    SdkAssert.assertEquals("field count of 'TableFieldWithIgnoredColumnsDefaultCreateExFormData'", 1, tableFieldWithIgnoredColumnsDefaultCreateExFormData.getFields().size());
    IField serialVersionUID = SdkAssert.assertFieldExist(tableFieldWithIgnoredColumnsDefaultCreateExFormData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID, 26);
    SdkAssert.assertFieldSignature(serialVersionUID, "J");

    SdkAssert.assertEquals("method count of 'TableFieldWithIgnoredColumnsDefaultCreateExFormData'", 2, tableFieldWithIgnoredColumnsDefaultCreateExFormData.getMethods().size());
    IMethod tableFieldWithIgnoredColumnsDefaultCreateExFormData1 = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsDefaultCreateExFormData, "TableFieldWithIgnoredColumnsDefaultCreateExFormData", new String[]{});
    SdkAssert.assertTrue(tableFieldWithIgnoredColumnsDefaultCreateExFormData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableFieldWithIgnoredColumnsDefaultCreateExFormData1, null);
    IMethod getTableDefaultCreateEx = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsDefaultCreateExFormData, "getTableDefaultCreateEx", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getTableDefaultCreateEx, "QTableDefaultCreateEx;");

    SdkAssert.assertEquals("inner types count of 'TableFieldWithIgnoredColumnsDefaultCreateExFormData'", 1, tableFieldWithIgnoredColumnsDefaultCreateExFormData.getTypes().size());
    // type TableDefaultCreateEx
    IType tableDefaultCreateEx = SdkAssert.assertTypeExists(tableFieldWithIgnoredColumnsDefaultCreateExFormData, "TableDefaultCreateEx");
    SdkAssert.assertHasFlags(tableDefaultCreateEx, 9);
    SdkAssert.assertHasSuperTypeSignature(tableDefaultCreateEx, "QTableBase;");
    SdkAssert.assertAnnotation(tableDefaultCreateEx, "org.eclipse.scout.commons.annotations.Replace");

    // fields of TableDefaultCreateEx
    SdkAssert.assertEquals("field count of 'TableDefaultCreateEx'", 1, tableDefaultCreateEx.getFields().size());
    IField serialVersionUID1 = SdkAssert.assertFieldExist(tableDefaultCreateEx, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID1, 26);
    SdkAssert.assertFieldSignature(serialVersionUID1, "J");

    SdkAssert.assertEquals("method count of 'TableDefaultCreateEx'", 8, tableDefaultCreateEx.getMethods().size());
    IMethod tableDefaultCreateEx1 = SdkAssert.assertMethodExist(tableDefaultCreateEx, "TableDefaultCreateEx", new String[]{});
    SdkAssert.assertTrue(tableDefaultCreateEx1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableDefaultCreateEx1, null);
    IMethod addRow = SdkAssert.assertMethodExist(tableDefaultCreateEx, "addRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(addRow, "QTableDefaultCreateExRowData;");
    SdkAssert.assertAnnotation(addRow, "java.lang.Override");
    IMethod addRow1 = SdkAssert.assertMethodExist(tableDefaultCreateEx, "addRow", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(addRow1, "QTableDefaultCreateExRowData;");
    SdkAssert.assertAnnotation(addRow1, "java.lang.Override");
    IMethod createRow = SdkAssert.assertMethodExist(tableDefaultCreateEx, "createRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(createRow, "QTableDefaultCreateExRowData;");
    SdkAssert.assertAnnotation(createRow, "java.lang.Override");
    IMethod getRowType = SdkAssert.assertMethodExist(tableDefaultCreateEx, "getRowType", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRowType, "QClass<+QAbstractTableRowData;>;");
    SdkAssert.assertAnnotation(getRowType, "java.lang.Override");
    IMethod getRows = SdkAssert.assertMethodExist(tableDefaultCreateEx, "getRows", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRows, "[QTableDefaultCreateExRowData;");
    SdkAssert.assertAnnotation(getRows, "java.lang.Override");
    IMethod rowAt = SdkAssert.assertMethodExist(tableDefaultCreateEx, "rowAt", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(rowAt, "QTableDefaultCreateExRowData;");
    SdkAssert.assertAnnotation(rowAt, "java.lang.Override");
    IMethod setRows = SdkAssert.assertMethodExist(tableDefaultCreateEx, "setRows", new String[]{"[QTableDefaultCreateExRowData;"});
    SdkAssert.assertMethodReturnTypeSignature(setRows, "V");

    SdkAssert.assertEquals("inner types count of 'TableDefaultCreateEx'", 1, tableDefaultCreateEx.getTypes().size());
    // type TableDefaultCreateExRowData
    IType tableDefaultCreateExRowData = SdkAssert.assertTypeExists(tableDefaultCreateEx, "TableDefaultCreateExRowData");
    SdkAssert.assertHasFlags(tableDefaultCreateExRowData, 9);
    SdkAssert.assertHasSuperTypeSignature(tableDefaultCreateExRowData, "QTableBaseRowData;");

    // fields of TableDefaultCreateExRowData
    SdkAssert.assertEquals("field count of 'TableDefaultCreateExRowData'", 3, tableDefaultCreateExRowData.getFields().size());
    IField serialVersionUID2 = SdkAssert.assertFieldExist(tableDefaultCreateExRowData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID2, 26);
    SdkAssert.assertFieldSignature(serialVersionUID2, "J");
    IField ignoreDefaultCreate = SdkAssert.assertFieldExist(tableDefaultCreateExRowData, "ignoreDefaultCreate");
    SdkAssert.assertHasFlags(ignoreDefaultCreate, 25);
    SdkAssert.assertFieldSignature(ignoreDefaultCreate, "QString;");
    IField m_ignoreDefaultCreate = SdkAssert.assertFieldExist(tableDefaultCreateExRowData, "m_ignoreDefaultCreate");
    SdkAssert.assertHasFlags(m_ignoreDefaultCreate, 2);
    SdkAssert.assertFieldSignature(m_ignoreDefaultCreate, "QString;");

    SdkAssert.assertEquals("method count of 'TableDefaultCreateExRowData'", 3, tableDefaultCreateExRowData.getMethods().size());
    IMethod tableDefaultCreateExRowData1 = SdkAssert.assertMethodExist(tableDefaultCreateExRowData, "TableDefaultCreateExRowData", new String[]{});
    SdkAssert.assertTrue(tableDefaultCreateExRowData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableDefaultCreateExRowData1, null);
    IMethod getIgnoreDefaultCreate = SdkAssert.assertMethodExist(tableDefaultCreateExRowData, "getIgnoreDefaultCreate", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getIgnoreDefaultCreate, "QString;");
    IMethod setIgnoreDefaultCreate = SdkAssert.assertMethodExist(tableDefaultCreateExRowData, "setIgnoreDefaultCreate", new String[]{"QString;"});
    SdkAssert.assertMethodReturnTypeSignature(setIgnoreDefaultCreate, "V");

    SdkAssert.assertEquals("inner types count of 'TableDefaultCreateExRowData'", 0, tableDefaultCreateExRowData.getTypes().size());
  }

  /**
   * @Generated with org.eclipse.scout.sdk.testing.codegen.ApiTestGenerator
   */
  private void testApiOfTableFieldWithIgnoredColumnsCreateExFormData(IType tableFieldWithIgnoredColumnsCreateExFormData) throws Exception {
    // type TableFieldWithIgnoredColumnsCreateExFormData
    SdkAssert.assertHasFlags(tableFieldWithIgnoredColumnsCreateExFormData, 1);
    SdkAssert.assertHasSuperTypeSignature(tableFieldWithIgnoredColumnsCreateExFormData, "QTableFieldWithIgnoredColumnsBaseFormData;");
    SdkAssert.assertAnnotation(tableFieldWithIgnoredColumnsCreateExFormData, "javax.annotation.Generated");

    // fields of TableFieldWithIgnoredColumnsCreateExFormData
    SdkAssert.assertEquals("field count of 'TableFieldWithIgnoredColumnsCreateExFormData'", 1, tableFieldWithIgnoredColumnsCreateExFormData.getFields().size());
    IField serialVersionUID = SdkAssert.assertFieldExist(tableFieldWithIgnoredColumnsCreateExFormData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID, 26);
    SdkAssert.assertFieldSignature(serialVersionUID, "J");

    SdkAssert.assertEquals("method count of 'TableFieldWithIgnoredColumnsCreateExFormData'", 2, tableFieldWithIgnoredColumnsCreateExFormData.getMethods().size());
    IMethod tableFieldWithIgnoredColumnsCreateExFormData1 = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsCreateExFormData, "TableFieldWithIgnoredColumnsCreateExFormData", new String[]{});
    SdkAssert.assertTrue(tableFieldWithIgnoredColumnsCreateExFormData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableFieldWithIgnoredColumnsCreateExFormData1, null);
    IMethod getTableCreateEx = SdkAssert.assertMethodExist(tableFieldWithIgnoredColumnsCreateExFormData, "getTableCreateEx", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getTableCreateEx, "QTableCreateEx;");

    SdkAssert.assertEquals("inner types count of 'TableFieldWithIgnoredColumnsCreateExFormData'", 1, tableFieldWithIgnoredColumnsCreateExFormData.getTypes().size());
    // type TableCreateEx
    IType tableCreateEx = SdkAssert.assertTypeExists(tableFieldWithIgnoredColumnsCreateExFormData, "TableCreateEx");
    SdkAssert.assertHasFlags(tableCreateEx, 9);
    SdkAssert.assertHasSuperTypeSignature(tableCreateEx, "QTableBase;");
    SdkAssert.assertAnnotation(tableCreateEx, "org.eclipse.scout.commons.annotations.Replace");

    // fields of TableCreateEx
    SdkAssert.assertEquals("field count of 'TableCreateEx'", 1, tableCreateEx.getFields().size());
    IField serialVersionUID1 = SdkAssert.assertFieldExist(tableCreateEx, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID1, 26);
    SdkAssert.assertFieldSignature(serialVersionUID1, "J");

    SdkAssert.assertEquals("method count of 'TableCreateEx'", 8, tableCreateEx.getMethods().size());
    IMethod tableCreateEx1 = SdkAssert.assertMethodExist(tableCreateEx, "TableCreateEx", new String[]{});
    SdkAssert.assertTrue(tableCreateEx1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableCreateEx1, null);
    IMethod addRow = SdkAssert.assertMethodExist(tableCreateEx, "addRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(addRow, "QTableCreateExRowData;");
    SdkAssert.assertAnnotation(addRow, "java.lang.Override");
    IMethod addRow1 = SdkAssert.assertMethodExist(tableCreateEx, "addRow", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(addRow1, "QTableCreateExRowData;");
    SdkAssert.assertAnnotation(addRow1, "java.lang.Override");
    IMethod createRow = SdkAssert.assertMethodExist(tableCreateEx, "createRow", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(createRow, "QTableCreateExRowData;");
    SdkAssert.assertAnnotation(createRow, "java.lang.Override");
    IMethod getRowType = SdkAssert.assertMethodExist(tableCreateEx, "getRowType", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRowType, "QClass<+QAbstractTableRowData;>;");
    SdkAssert.assertAnnotation(getRowType, "java.lang.Override");
    IMethod getRows = SdkAssert.assertMethodExist(tableCreateEx, "getRows", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getRows, "[QTableCreateExRowData;");
    SdkAssert.assertAnnotation(getRows, "java.lang.Override");
    IMethod rowAt = SdkAssert.assertMethodExist(tableCreateEx, "rowAt", new String[]{"I"});
    SdkAssert.assertMethodReturnTypeSignature(rowAt, "QTableCreateExRowData;");
    SdkAssert.assertAnnotation(rowAt, "java.lang.Override");
    IMethod setRows = SdkAssert.assertMethodExist(tableCreateEx, "setRows", new String[]{"[QTableCreateExRowData;"});
    SdkAssert.assertMethodReturnTypeSignature(setRows, "V");

    SdkAssert.assertEquals("inner types count of 'TableCreateEx'", 1, tableCreateEx.getTypes().size());
    // type TableCreateExRowData
    IType tableCreateExRowData = SdkAssert.assertTypeExists(tableCreateEx, "TableCreateExRowData");
    SdkAssert.assertHasFlags(tableCreateExRowData, 9);
    SdkAssert.assertHasSuperTypeSignature(tableCreateExRowData, "QTableBaseRowData;");

    // fields of TableCreateExRowData
    SdkAssert.assertEquals("field count of 'TableCreateExRowData'", 3, tableCreateExRowData.getFields().size());
    IField serialVersionUID2 = SdkAssert.assertFieldExist(tableCreateExRowData, "serialVersionUID");
    SdkAssert.assertHasFlags(serialVersionUID2, 26);
    SdkAssert.assertFieldSignature(serialVersionUID2, "J");
    IField ignoreCreate = SdkAssert.assertFieldExist(tableCreateExRowData, "ignoreCreate");
    SdkAssert.assertHasFlags(ignoreCreate, 25);
    SdkAssert.assertFieldSignature(ignoreCreate, "QString;");
    IField m_ignoreCreate = SdkAssert.assertFieldExist(tableCreateExRowData, "m_ignoreCreate");
    SdkAssert.assertHasFlags(m_ignoreCreate, 2);
    SdkAssert.assertFieldSignature(m_ignoreCreate, "QString;");

    SdkAssert.assertEquals("method count of 'TableCreateExRowData'", 3, tableCreateExRowData.getMethods().size());
    IMethod tableCreateExRowData1 = SdkAssert.assertMethodExist(tableCreateExRowData, "TableCreateExRowData", new String[]{});
    SdkAssert.assertTrue(tableCreateExRowData1.isConstructor());
    SdkAssert.assertMethodReturnTypeSignature(tableCreateExRowData1, null);
    IMethod getIgnoreCreate = SdkAssert.assertMethodExist(tableCreateExRowData, "getIgnoreCreate", new String[]{});
    SdkAssert.assertMethodReturnTypeSignature(getIgnoreCreate, "QString;");
    IMethod setIgnoreCreate = SdkAssert.assertMethodExist(tableCreateExRowData, "setIgnoreCreate", new String[]{"QString;"});
    SdkAssert.assertMethodReturnTypeSignature(setIgnoreCreate, "V");

    SdkAssert.assertEquals("inner types count of 'TableCreateExRowData'", 0, tableCreateExRowData.getTypes().size());
  }
}
