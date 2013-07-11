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
package org.eclipse.scout.sdk.operation.form.formdata;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.internal.ScoutSdk;
import org.eclipse.scout.sdk.operation.data.table.AbstractTableBeanSourceContentBuilder;
import org.eclipse.scout.sdk.util.signature.IImportValidator;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchy;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeUtility;

/**
 * Source builder that creates form data classes for an ITableField. The generated form data element stores the data in
 * ordinary java beans (which are implementing {@link RuntimeClasses#AbstractTableRowData}.
 * 
 * @since 3.8.2
 */
public class TableFieldBeanSourceBuilder extends SourceBuilderWithProperties {

  private final IType m_tableField;
  private final ITypeHierarchy m_hierarchy;

  public TableFieldBeanSourceBuilder(IType tableField, ITypeHierarchy hierarchy, IJavaProject targetProject) {
    super(tableField, targetProject);
    m_tableField = tableField;
    m_hierarchy = hierarchy;

  }

  @Override
  public String createSource(IImportValidator validator) throws JavaModelException {
    TableFieldBeanSourceContentBuilder tableBeanBuilder = new TableFieldBeanSourceContentBuilder(m_tableField, m_hierarchy, NL);
    tableBeanBuilder.addContentBuilders(this, validator);
    return super.createSource(validator);
  }

  protected static class TableFieldBeanSourceContentBuilder extends AbstractTableBeanSourceContentBuilder {

    public TableFieldBeanSourceContentBuilder(IType tableContainerType, ITypeHierarchy hierarchy, String nl) {
      super(tableContainerType, hierarchy, nl, TypeUtility.getType(RuntimeClasses.ITableField));
    }

    @Override
    protected String getTableRowDataSuperClassSignature(IImportValidator validator, IType table, ITypeHierarchy hierarchy) {
      try {
        IType parentTable = hierarchy.getSuperclass(table);
        if (TypeUtility.exists(parentTable)) {
          IType declaringType = parentTable.getDeclaringType();
          if (TypeUtility.exists(declaringType)) {
            IType formDataType = ScoutTypeUtility.getFormDataType(declaringType, hierarchy);
            String parentTableRowBeanName = getTableRowBeanName(parentTable);
            IType parentTableBeanData = formDataType.getType(parentTableRowBeanName);
            if (TypeUtility.exists(parentTableBeanData)) {
              validator.addImport(formDataType.getFullyQualifiedName());
              return Signature.createTypeSignature(parentTableBeanData.getTypeQualifiedName(), false);
            }
          }
        }
      }
      catch (JavaModelException e) {
        ScoutSdk.logWarning("error while computing super class signature for [" + table + "]", e);
      }
      return Signature.createTypeSignature(RuntimeClasses.AbstractTableRowData, true);
    }
  }
}
