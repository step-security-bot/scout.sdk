/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.core.s.sourcebuilder.dto;

import java.util.function.Predicate;

import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.scout.sdk.core.importvalidator.IImportValidator;
import org.eclipse.scout.sdk.core.model.api.Flags;
import org.eclipse.scout.sdk.core.model.api.IJavaEnvironment;
import org.eclipse.scout.sdk.core.model.api.IMethod;
import org.eclipse.scout.sdk.core.model.api.IType;
import org.eclipse.scout.sdk.core.s.IScoutRuntimeTypes;
import org.eclipse.scout.sdk.core.s.sourcebuilder.dto.table.TableRowDataTypeSourceBuilder;
import org.eclipse.scout.sdk.core.s.util.DtoUtils;
import org.eclipse.scout.sdk.core.signature.ISignatureConstants;
import org.eclipse.scout.sdk.core.signature.Signature;
import org.eclipse.scout.sdk.core.sourcebuilder.ISourceBuilder;
import org.eclipse.scout.sdk.core.sourcebuilder.SortedMemberKeyFactory;
import org.eclipse.scout.sdk.core.sourcebuilder.method.IMethodSourceBuilder;
import org.eclipse.scout.sdk.core.sourcebuilder.method.MethodSourceBuilder;
import org.eclipse.scout.sdk.core.sourcebuilder.method.MethodSourceBuilderFactory;
import org.eclipse.scout.sdk.core.sourcebuilder.methodparameter.MethodParameterSourceBuilder;
import org.eclipse.scout.sdk.core.sourcebuilder.type.ITypeSourceBuilder;
import org.eclipse.scout.sdk.core.util.CoreUtils;
import org.eclipse.scout.sdk.core.util.PropertyMap;
import org.eclipse.scout.sdk.core.util.TypeFilters;

/**
 * <h3>{@link AbstractTableBeanSourceBuilder}</h3>
 *
 * @author Andreas Hoegger
 * @since 3.10.0 2013-08-27
 */
public abstract class AbstractTableBeanSourceBuilder extends AbstractDtoTypeSourceBuilder {

  /**
   * @param elementName
   */
  public AbstractTableBeanSourceBuilder(IType modelType, String targetPackageName, String typeName, IJavaEnvironment env, boolean setup) {
    super(modelType, targetPackageName, typeName, env, setup);
  }

  @Override
  protected void createContent() {
    super.createContent();
    IType table = CoreUtils.findInnerTypeInSuperHierarchy(getModelType(), TypeFilters.instanceOf(IScoutRuntimeTypes.ITable));
    if (table != null) {
      visitTableBean(table);
    }
    else {
      addAbstractMethodImplementations();
    }
  }

  protected void visitTableBean(IType table) {
    // inner row data class
    String rowDataName = DtoUtils.getRowDataName(getElementName());
    ITypeSourceBuilder tableRowDataBuilder = new TableRowDataTypeSourceBuilder(rowDataName, table, getModelType(), getJavaEnvironment());
    addSortedType(SortedMemberKeyFactory.createTypeTableKey(tableRowDataBuilder), tableRowDataBuilder);

    // row access methods
    final String tableRowSignature = Signature.createTypeSignature(rowDataName, false);
    // getRows
    IMethodSourceBuilder getRowsMethodBuilder = MethodSourceBuilderFactory.createOverride(this, getTargetPackage(), getJavaEnvironment(), "getRows");
    getRowsMethodBuilder.setReturnTypeSignature(Signature.createArraySignature(tableRowSignature, 1));
    getRowsMethodBuilder.setBody(new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("return (").append(validator.useSignature(Signature.createArraySignature(tableRowSignature, 1))).append(") super.getRows();");
      }
    });
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(getRowsMethodBuilder), getRowsMethodBuilder);

    // setRows
    IMethodSourceBuilder setRowsMethodBuilder = new MethodSourceBuilder("setRows");
    setRowsMethodBuilder.setFlags(Flags.AccPublic);
    setRowsMethodBuilder.setReturnTypeSignature(ISignatureConstants.SIG_VOID);
    setRowsMethodBuilder.addParameter(new MethodParameterSourceBuilder("rows", Signature.createArraySignature(tableRowSignature, 1)));
    setRowsMethodBuilder.setBody(new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("super.setRows(rows);");
      }
    });
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(setRowsMethodBuilder), setRowsMethodBuilder);

    // addRow
    final String addRowMethodName = "addRow";
    IMethodSourceBuilder addRowMethodBuilder = MethodSourceBuilderFactory.createOverride(this, getTargetPackage(), getJavaEnvironment(), addRowMethodName, new Predicate<IMethod>() {
      @Override
      public boolean test(IMethod candidate) {
        // choose the narrowed overload from the abstract super class instead of the method defined in the interface
        return !candidate.parameters().existsAny();
      }
    });
    addRowMethodBuilder.setReturnTypeSignature(tableRowSignature);
    addRowMethodBuilder.setBody(new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("return (").append(validator.useSignature(tableRowSignature)).append(") super.addRow();");
      }
    });
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(addRowMethodBuilder), addRowMethodBuilder);

    // addRow(int state)
    final IMethodSourceBuilder addRowWithStateMethodBuilder = MethodSourceBuilderFactory.createOverride(this, getTargetPackage(), getJavaEnvironment(), addRowMethodName, new Predicate<IMethod>() {
      @Override
      public boolean test(IMethod candidate) {
        return candidate.parameters().list().size() == 1;
      }
    });
    addRowWithStateMethodBuilder.getParameters().get(0).setElementName("rowState"); // in case the param name cannot be parsed from the class file
    addRowWithStateMethodBuilder.setReturnTypeSignature(tableRowSignature);
    addRowWithStateMethodBuilder.setBody(new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("return (").append(validator.useSignature(tableRowSignature)).append(") super.addRow(");
        source.append(addRowWithStateMethodBuilder.getParameters().get(0).getElementName()).append(");");
      }
    });
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(addRowWithStateMethodBuilder), addRowWithStateMethodBuilder);

    // rowAt
    final IMethodSourceBuilder rowAtMethodBuilder = MethodSourceBuilderFactory.createOverride(this, getTargetPackage(), getJavaEnvironment(), "rowAt");
    rowAtMethodBuilder.setReturnTypeSignature(tableRowSignature);
    rowAtMethodBuilder.getParameters().get(0).setElementName("index"); // in case the param name cannot be parsed from the class file
    rowAtMethodBuilder.setBody(new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("return (").append(validator.useSignature(tableRowSignature)).append(") super.rowAt(").append(rowAtMethodBuilder.getParameters().get(0).getElementName()).append(");");
      }
    });
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(rowAtMethodBuilder), rowAtMethodBuilder);

    // createRow
    IMethodSourceBuilder createRowMethodBuilder = MethodSourceBuilderFactory.createOverride(this, getTargetPackage(), getJavaEnvironment(), "createRow");
    createRowMethodBuilder.setReturnTypeSignature(tableRowSignature);
    if (Flags.isAbstract(table.flags()) || Flags.isAbstract(getModelType().flags())) {
      createRowMethodBuilder.setFlags(createRowMethodBuilder.getFlags() | Flags.AccAbstract);
    }
    else {
      createRowMethodBuilder.setBody(new ISourceBuilder() {
        @Override
        public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
          source.append("return new ").append(validator.useSignature(tableRowSignature)).append("();");
        }
      });
    }
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(createRowMethodBuilder), createRowMethodBuilder);

    // getRowType
    IMethodSourceBuilder getRowTypeMethodBuilder = MethodSourceBuilderFactory.createOverride(this, getTargetPackage(), getJavaEnvironment(), "getRowType");
    getRowTypeMethodBuilder.setBody(new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("return ").append(validator.useSignature(tableRowSignature)).append(SuffixConstants.SUFFIX_class).append(';');
      }
    });
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(getRowTypeMethodBuilder), getRowTypeMethodBuilder);
  }

  protected void addAbstractMethodImplementations() {
    // createRow
    IMethodSourceBuilder createRowSourceBuilder = MethodSourceBuilderFactory.createOverride(this, getTargetPackage(), getJavaEnvironment(), "createRow");

    createRowSourceBuilder.setReturnTypeSignature(Signature.createTypeSignature(IScoutRuntimeTypes.AbstractTableRowData));
    createRowSourceBuilder.setBody(new ISourceBuilder() {

      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("return new ").append(validator.useName(IScoutRuntimeTypes.AbstractTableRowData));
        source.append("(){").append(lineDelimiter).append("private static final long serialVersionUID = 1L;").append(lineDelimiter).append("};");
      }
    });
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(createRowSourceBuilder), createRowSourceBuilder);

    IMethodSourceBuilder getRowTypeSourceBuilder = MethodSourceBuilderFactory.createOverride(this, getTargetPackage(), getJavaEnvironment(), "getRowType");
    getRowTypeSourceBuilder.setReturnTypeSignature(Signature.createTypeSignature(Class.class.getName() + ISignatureConstants.C_GENERIC_START + "? extends " + IScoutRuntimeTypes.AbstractTableRowData + ISignatureConstants.C_GENERIC_END));
    getRowTypeSourceBuilder.setBody(new ISourceBuilder() {

      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("return ").append(validator.useName(IScoutRuntimeTypes.AbstractTableRowData)).append(SuffixConstants.SUFFIX_class).append(';');
      }
    });
    addSortedMethod(SortedMemberKeyFactory.createMethodAnyKey(getRowTypeSourceBuilder), getRowTypeSourceBuilder);
  }

}
