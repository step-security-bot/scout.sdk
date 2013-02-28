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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.internal.ScoutSdk;
import org.eclipse.scout.sdk.util.type.ITypeFilter;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchy;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeComparators;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeUtility;

public class CompositePrimaryTypeSourceBuilder extends SourceBuilderWithProperties {
  final IType iFormField = TypeUtility.getType(RuntimeClasses.IFormField);
  final IType iTableField = TypeUtility.getType(RuntimeClasses.ITableField);
  final IType iComposerField = TypeUtility.getType(RuntimeClasses.IComposerField);
  final IType iCompositeField = TypeUtility.getType(RuntimeClasses.ICompositeField);
  final IType iRadioButtonGroup = TypeUtility.getType(RuntimeClasses.IRadioButtonGroup);

  public CompositePrimaryTypeSourceBuilder(IType type, IJavaProject targetProject) {
    this(type, targetProject, TypeUtility.getLocalTypeHierarchy(type));
  }

  public CompositePrimaryTypeSourceBuilder(IType type, IJavaProject targetProject, ITypeHierarchy formFieldHierarchy) {
    super(type, targetProject);
    visitFormFields(type, formFieldHierarchy, targetProject);
  }

  protected void visitFormFields(IType declaringType, ITypeHierarchy formFieldHierarchy, IJavaProject targetProject) {
    try {
      if (declaringType.getTypes().length > 0) {
        if (formFieldHierarchy == null) {
          formFieldHierarchy = TypeUtility.getLocalTypeHierarchy(declaringType);
        }
        ITypeFilter formFieldFilter = TypeFilters.getMultiTypeFilter(TypeFilters.getSubtypeFilter(iFormField, formFieldHierarchy));//, TypeFilters.getClassFilter());
        for (IType t : TypeUtility.getInnerTypes(declaringType, formFieldFilter, ScoutTypeComparators.getOrderAnnotationComparator())) {
          try {
            addFormField(t, formFieldHierarchy, targetProject);
          }
          catch (JavaModelException e) {
            ScoutSdk.logError("could not add form field '" + declaringType.getElementName() + "' to form data.", e);
          }
        }
      }
    }
    catch (JavaModelException e) {
      ScoutSdk.logError("error during visiting type '" + declaringType.getElementName() + "'", e);
    }
  }

  protected void addFormField(IType formField, ITypeHierarchy formFieldHierarchy, IJavaProject targetProject) throws JavaModelException {
    FormDataAnnotation formDataAnnotation = ScoutTypeUtility.findFormDataAnnotation(formField, formFieldHierarchy);
    if (formDataAnnotation != null) {
      if (FormDataAnnotation.isCreate(formDataAnnotation)) {
        String formDataElementName = FormDataUtility.getBeanName(FormDataUtility.getFieldNameWithoutSuffix(formField.getElementName()), true);
        String superTypeSignature = null;
        IType superType = null;

        boolean replaceAnnotationPresent = ScoutTypeUtility.isReplaceAnnotationPresent(formField);
        if (replaceAnnotationPresent) {
          IType replacedType = formFieldHierarchy.getSuperclass(formField);
          IType replacedFormFieldDataType = ScoutTypeUtility.getFormDataType(replacedType, formFieldHierarchy);
          if (replacedFormFieldDataType != null) {
            superTypeSignature = Signature.createTypeSignature(replacedFormFieldDataType.getTypeQualifiedName(), false);
            superType = replacedFormFieldDataType;
          }
        }

        if (superTypeSignature == null) {
          superTypeSignature = formDataAnnotation.getSuperTypeSignature();
          superType = TypeUtility.getTypeBySignature(superTypeSignature);
          if (formDataAnnotation.getGenericOrdinal() >= 0) {
            if (TypeUtility.isGenericType(superType)) {
              String genericTypeSig = org.eclipse.scout.sdk.operation.form.formdata.FormDataUtility.computeFormFieldGenericType(formField, formFieldHierarchy);
              if (genericTypeSig != null) {
                superTypeSignature = superTypeSignature.replaceAll("\\;$", "<" + genericTypeSig + ">;");
              }
            }
          }
        }

        ITypeSourceBuilder builder = FormDataUtility.getInnerTypeFormDataSourceBuilder(superType, superTypeSignature, formField, formFieldHierarchy, targetProject);
        builder.setElementName(formDataElementName);
        builder.setSuperTypeSignature(superTypeSignature);
        builder.setFlags(Flags.AccPublic | Flags.AccStatic);
        if (replaceAnnotationPresent) {
          builder.addAnnotation(new AnnotationSourceBuilder(Signature.createTypeSignature(RuntimeClasses.Replace, true)));
        }
        addBuilder(builder, CATEGORY_TYPE_FIELD);
        MethodSourceBuilder getterBuilder = new MethodSourceBuilder(NL);
        getterBuilder.setElementName("get" + formDataElementName);
        getterBuilder.setReturnSignature(Signature.createTypeSignature(formDataElementName, false));
        getterBuilder.setSimpleBody("return getFieldByClass(" + formDataElementName + ".class);");
        addBuilder(getterBuilder, CATEGORY_METHOD_FIELD_GETTER);
      }
      else if (FormDataAnnotation.isIgnore(formDataAnnotation)) {
        return;
      }
    }
    // visit children
    if (formFieldHierarchy.isSubtype(iCompositeField, formField)) {
      visitFormFields(formField, formFieldHierarchy, targetProject);
    }
  }
}
