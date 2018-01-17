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

import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.scout.sdk.core.model.api.Flags;
import org.eclipse.scout.sdk.core.model.api.IJavaEnvironment;
import org.eclipse.scout.sdk.core.model.api.IType;
import org.eclipse.scout.sdk.core.s.IScoutRuntimeTypes;
import org.eclipse.scout.sdk.core.s.annotation.FormDataAnnotationDescriptor;
import org.eclipse.scout.sdk.core.s.sourcebuilder.dto.form.FormDataTypeSourceBuilder;
import org.eclipse.scout.sdk.core.s.sourcebuilder.dto.table.TableFieldBeanFormDataSourceBuilder;
import org.eclipse.scout.sdk.core.s.sourcebuilder.dto.table.TableRowDataTypeSourceBuilder;
import org.eclipse.scout.sdk.core.s.util.DtoUtils;
import org.eclipse.scout.sdk.core.signature.Signature;
import org.eclipse.scout.sdk.core.sourcebuilder.RawSourceBuilder;
import org.eclipse.scout.sdk.core.sourcebuilder.SortedMemberKeyFactory;
import org.eclipse.scout.sdk.core.sourcebuilder.comment.CommentSourceBuilderFactory;
import org.eclipse.scout.sdk.core.sourcebuilder.method.IMethodSourceBuilder;
import org.eclipse.scout.sdk.core.sourcebuilder.method.MethodSourceBuilder;
import org.eclipse.scout.sdk.core.sourcebuilder.type.ITypeSourceBuilder;
import org.eclipse.scout.sdk.core.util.CoreUtils;

/**
 * <h3>{@link CompositeFormDataTypeSourceBuilder}</h3>
 *
 * @author Andreas Hoegger
 * @since 3.10.0 2013-08-27
 */
public class CompositeFormDataTypeSourceBuilder extends FormDataTypeSourceBuilder {

  public CompositeFormDataTypeSourceBuilder(IType modelType, FormDataAnnotationDescriptor formDataAnnotation, String targetPackageName, String typeName, IJavaEnvironment env) {
    super(modelType, formDataAnnotation, targetPackageName, typeName, env);
  }

  @Override
  protected void createContent() {
    super.createContent();
    processCompositeField(getModelType());
  }

  protected void processFormField(IType formField) {
    FormDataAnnotationDescriptor fieldAnnotation = DtoUtils.getFormDataAnnotationDescriptor(formField);
    if (FormDataAnnotationDescriptor.isIgnore(fieldAnnotation)) {
      return;
    }

    boolean fieldExtendsTemplateField = false;
    boolean isCompositeField = formField.isInstanceOf(IScoutRuntimeTypes.ICompositeField);
    if (FormDataAnnotationDescriptor.isCreate(fieldAnnotation)) {
      IType formDataType = fieldAnnotation.getFormDataType();
      String formDataTypeName = null;
      if (formDataType == null) {
        formDataTypeName = DtoUtils.removeFieldSuffix(formField.elementName());
      }
      else {
        formDataTypeName = formDataType.elementName();
      }

      ITypeSourceBuilder fieldSourceBuilder = null;
      if (fieldAnnotation.getSuperType().isInstanceOf(IScoutRuntimeTypes.AbstractTableFieldBeanData)) {
        // fill table bean
        fieldSourceBuilder = new TableFieldBeanFormDataSourceBuilder(formField, fieldAnnotation, getTargetPackage(), formDataTypeName, getJavaEnvironment());
      }
      else if (isCompositeField && !formField.isInstanceOf(IScoutRuntimeTypes.IValueField)) {
        // field extends a field template.
        fieldExtendsTemplateField = true;
        fieldSourceBuilder = new CompositeFormDataTypeSourceBuilder(formField, fieldAnnotation, getTargetPackage(), formDataTypeName, getJavaEnvironment());
      }
      else {
        fieldSourceBuilder = new FormDataTypeSourceBuilder(formField, fieldAnnotation, getTargetPackage(), formDataTypeName, getJavaEnvironment());

        // special case if a property has the same name as a form field -> show warning
        for (IMethodSourceBuilder msb : getMethods()) {
          String methodName = CoreUtils.getGetterMethodPrefix(msb.getReturnTypeSignature()) + formDataTypeName;
          if (methodName.equals(msb.getElementName())) {
            fieldSourceBuilder.setComment(CommentSourceBuilderFactory.createCustomCommentBuilder("TODO [everyone] Duplicate names '" + formDataTypeName + "'. Rename property or form field."));
            break;
          }
        }
      }
      fieldSourceBuilder.setFlags(fieldSourceBuilder.getFlags() | Flags.AccStatic);
      addSortedType(SortedMemberKeyFactory.createTypeFormDataPropertyKey(fieldSourceBuilder), fieldSourceBuilder);

      // add interfaces specified on the formdata annotation
      DtoUtils.addFormDataAdditionalInterfaces(fieldAnnotation, fieldSourceBuilder, getJavaEnvironment());

      // Scout RT requires the first char to be upper-case for a getter.
      // See org.eclipse.scout.rt.platform.reflect.FastBeanUtility.BEAN_METHOD_PAT.
      String methodName = CoreUtils.ensureStartWithUpperCase(formDataTypeName);

      // getter for field
      IMethodSourceBuilder getterBuilder = new MethodSourceBuilder("get" + methodName);
      getterBuilder.setFlags(Flags.AccPublic);
      getterBuilder.setReturnTypeSignature(Signature.createTypeSignature(formDataTypeName, false));
      getterBuilder.setBody(new RawSourceBuilder(new StringBuilder("return getFieldByClass(").append(formDataTypeName).append(SuffixConstants.SUFFIX_class).append(");").toString()));
      addSortedMethod(SortedMemberKeyFactory.createMethodPropertyKey(getterBuilder), getterBuilder);
    }

    if (isCompositeField && !fieldExtendsTemplateField) {
      processCompositeField(formField);
    }
  }

  protected void processTableExtension(IType tableExtension) {
    String rowDataName = DtoUtils.getRowDataName(tableExtension.elementName());
    TableRowDataTypeSourceBuilder rowDataSourceBuilder = new TableRowDataTypeSourceBuilder(rowDataName, tableExtension, tableExtension, getJavaEnvironment());
    addSortedType(SortedMemberKeyFactory.createTypeFormDataPropertyKey(rowDataSourceBuilder), rowDataSourceBuilder);
    DtoUtils.addDtoExtendsAnnotation(rowDataSourceBuilder, tableExtension);
  }

  protected void processCompositeField(IType compositeType) {
    // nested form fields
    compositeType.innerTypes()
        .withFlags(Flags.AccPublic)
        .withInstanceOf(IScoutRuntimeTypes.IFormField)
        .list()
        .forEach(this::processFormField);

    // step into formFieldMenus (special menu that may contain more form fields)
    compositeType.innerTypes()
        .withFlags(Flags.AccPublic)
        .withInstanceOf(IScoutRuntimeTypes.IFormFieldMenu)
        .list()
        .forEach(this::processCompositeField);

    // step into extensions
    compositeType.innerTypes()
        .withFlags(Flags.AccPublic)
        .withInstanceOf(IScoutRuntimeTypes.ICompositeFieldExtension)
        .list()
        .forEach(this::processCompositeField);

    // step into table extensions of table-fields
    compositeType.innerTypes()
        .withFlags(Flags.AccPublic)
        .withRecursiveInnerTypes(true)
        .withInstanceOf(IScoutRuntimeTypes.ITableExtension)
        .list()
        .forEach(this::processTableExtension);
  }
}
