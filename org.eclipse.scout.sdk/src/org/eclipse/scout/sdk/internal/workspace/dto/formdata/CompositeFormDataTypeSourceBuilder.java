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
package org.eclipse.scout.sdk.internal.workspace.dto.formdata;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.internal.ScoutSdk;
import org.eclipse.scout.sdk.internal.workspace.dto.FormDataUtility;
import org.eclipse.scout.sdk.sourcebuilder.SortedMemberKeyFactory;
import org.eclipse.scout.sdk.sourcebuilder.method.IMethodSourceBuilder;
import org.eclipse.scout.sdk.sourcebuilder.method.MethodBodySourceBuilderFactory;
import org.eclipse.scout.sdk.sourcebuilder.method.MethodSourceBuilder;
import org.eclipse.scout.sdk.sourcebuilder.type.ITypeSourceBuilder;
import org.eclipse.scout.sdk.util.internal.sigcache.SignatureCache;
import org.eclipse.scout.sdk.util.signature.SignatureUtility;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchy;
import org.eclipse.scout.sdk.workspace.dto.formdata.FormDataAnnotation;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeUtility;

/**
 * <h3>{@link CompositeFormDataTypeSourceBuilder}</h3>
 * 
 * @author aho
 * @since 3.10.0 27.08.2013
 */
public class CompositeFormDataTypeSourceBuilder extends FormDataTypeSourceBuilder {

  /**
   * @param modelType
   * @param elementName
   * @param formDataAnnotation
   */
  public CompositeFormDataTypeSourceBuilder(IType modelType, String elementName, FormDataAnnotation formDataAnnotation) {
    super(modelType, elementName, formDataAnnotation);
  }

  @Override
  protected void createContent() {
    super.createContent();
    try {
      createCompositeFieldFormData(getModelType(), getLocalTypeHierarchy());
    }
    catch (JavaModelException e) {
      ScoutSdk.logError("could not build form data for '" + getModelType().getElementName() + "'.", e);
    }
  }

  private void createCompositeFieldFormData(IType compositeType, ITypeHierarchy declaringTypeHierarchy) throws JavaModelException {
    for (IType formField : TypeUtility.getInnerTypes(compositeType, TypeFilters.getSubtypeFilter(TypeUtility.getType(RuntimeClasses.IFormField), declaringTypeHierarchy))) {
      if (Flags.isPublic(formField.getFlags())) {
        FormDataAnnotation fieldAnnotation = ScoutTypeUtility.findFormDataAnnotation(formField, declaringTypeHierarchy);

        if (FormDataAnnotation.isCreate(fieldAnnotation)) {
          String formDataTypeSignature = fieldAnnotation.getFormDataTypeSignature();
          String formDataTypeName = null;
          if (StringUtility.isNullOrEmpty(formDataTypeSignature)) {
            formDataTypeName = FormDataUtility.getFormDataName(formField.getElementName());
          }
          else {
            formDataTypeName = Signature.getSignatureSimpleName(formDataTypeSignature);
          }

          String formDataSuperTypeSignature = fieldAnnotation.getSuperTypeSignature();
          IType superType = TypeUtility.getTypeBySignature(formDataSuperTypeSignature);
          String typeErasure = Signature.getTypeErasure(formDataSuperTypeSignature);
          ITypeHierarchy superTypeHierarchy = TypeUtility.getSuperTypeHierarchy(superType);
          ITypeSourceBuilder fieldSourceBuilder = null;
          if (SignatureUtility.isEqualSignature(typeErasure, SignatureCache.createTypeSignature(RuntimeClasses.AbstractTableFieldData))) {
            fieldSourceBuilder = new TableFieldFormDataSourceBuilder(formField, formDataTypeName, fieldAnnotation);
          }
          else if (superTypeHierarchy != null && superTypeHierarchy.contains(TypeUtility.getType(RuntimeClasses.AbstractTableFieldBeanData))) {
            // fill table bean
            fieldSourceBuilder = new TableFieldBeanFormDataSourceBuilder(formField, formDataTypeName, fieldAnnotation);
          }
          else {
            fieldSourceBuilder = new FormDataTypeSourceBuilder(formField, formDataTypeName, fieldAnnotation);
          }
          fieldSourceBuilder.setFlags(fieldSourceBuilder.getFlags() | Flags.AccStatic);
          addSortedTypeSourceBuilder(SortedMemberKeyFactory.createTypeFormDataPropertyKey(fieldSourceBuilder), fieldSourceBuilder);
          // property getter for field
          IMethodSourceBuilder getterBuilder = new MethodSourceBuilder("get" + formDataTypeName);
          getterBuilder.setFlags(Flags.AccPublic);
          getterBuilder.setReturnTypeSignature(Signature.createTypeSignature(formDataTypeName, false));
          getterBuilder.setMethodBodySourceBuilder(MethodBodySourceBuilderFactory.createSimpleMethodBody("return getFieldByClass(" + formDataTypeName + ".class);"));
          addSortedMethodSourceBuilder(SortedMemberKeyFactory.createMethodPropertyKey(getterBuilder), getterBuilder);
        }
        else if (FormDataAnnotation.isIgnore(fieldAnnotation)) {
          continue;
        }
        else if (declaringTypeHierarchy.isSubtype(TypeUtility.getType(RuntimeClasses.ICompositeField), formField)) {
          createCompositeFieldFormData(formField, declaringTypeHierarchy);
        }
      }
    }
  }
}
