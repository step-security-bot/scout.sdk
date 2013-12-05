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
package org.eclipse.scout.sdk.sourcebuilder.annotation;

import java.util.ArrayList;

import javax.annotation.Generated;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.annotations.FormData;
import org.eclipse.scout.commons.annotations.InjectFieldTo;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.util.internal.sigcache.SignatureCache;
import org.eclipse.scout.sdk.util.signature.IImportValidator;
import org.eclipse.scout.sdk.util.signature.SignatureUtility;

/**
 * <h3>{@link AnnotationSourceBuilderFactory}</h3> ...
 * 
 * @author Andreas Hoegger
 * @since 3.10.0 07.03.2013
 */
public final class AnnotationSourceBuilderFactory {

  private AnnotationSourceBuilderFactory() {
  }

  public static IAnnotationSourceBuilder createOverrideAnnotationSourceBuilder() {
    return new AnnotationSourceBuilder(SignatureCache.createTypeSignature(Override.class.getName()));
  }

  public static IAnnotationSourceBuilder createOrderAnnotation(double orderNr) {
    AnnotationSourceBuilder orderAnnoation = new AnnotationSourceBuilder(SignatureCache.createTypeSignature(RuntimeClasses.Order));
    orderAnnoation.addParameter(Double.toString(orderNr));
    return orderAnnoation;
  }

  public static IAnnotationSourceBuilder createFormDataAnnotation() {
    return createFormDataAnnotation(null, null, null);
  }

  public static IAnnotationSourceBuilder createGeneratedAnnotation(String classThatGeneratedTheCode) {
    return createGeneratedAnnotation(classThatGeneratedTheCode, null);
  }

  public static IAnnotationSourceBuilder createGeneratedAnnotation(final String classThatGeneratedTheCode, final String comments) {
    return new AnnotationSourceBuilder(SignatureCache.createTypeSignature(Generated.class.getName())) {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, IJavaProject ownerProject, IImportValidator validator) throws CoreException {
        source.append('@').append(SignatureUtility.getTypeReference(getSignature(), validator)).append('(');
        source.append("value = \"").append(classThatGeneratedTheCode).append("\"");
        if (StringUtility.hasText(comments)) {
          source.append(", comments = \"").append(comments).append("\"");
        }
        source.append(")");
      }
    };
  }

  public static IAnnotationSourceBuilder createPageDataAnnotation(final String pageDataTypeSignature) {
    return new AnnotationSourceBuilder(SignatureCache.createTypeSignature(RuntimeClasses.PageData)) {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, IJavaProject ownerProject, IImportValidator validator) throws CoreException {
        source.append('@').append(SignatureUtility.getTypeReference(getSignature(), validator)).append('(');
        source.append(SignatureUtility.getTypeReference(pageDataTypeSignature, validator));
        source.append(".class)");
      }
    };
  }

  public static IAnnotationSourceBuilder createFormDataAnnotation(final String formDataSignature, final FormData.SdkCommand sdkCommand, final FormData.DefaultSubtypeSdkCommand defaultSubtypeCommand) {
    return new AnnotationSourceBuilder(SignatureCache.createTypeSignature(RuntimeClasses.FormData)) {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, IJavaProject ownerProject, IImportValidator validator) throws CoreException {
        String formDataTypeRef = SignatureUtility.getTypeReference(getSignature(), validator);
        source.append("@").append(formDataTypeRef);
        ArrayList<String> args = new ArrayList<String>(3);
        if (formDataSignature != null) {
          args.add("value = " + SignatureUtility.getTypeReference(formDataSignature, validator) + ".class");
        }
        if (sdkCommand != null) {
          StringBuilder b = new StringBuilder();
          b.append("sdkCommand = ");
          b.append(formDataTypeRef).append(".");
          b.append(SignatureUtility.getTypeReference(SignatureCache.createTypeSignature(sdkCommand.getDeclaringClass().getName()), validator));
          b.append(".").append(sdkCommand.name());
          args.add(b.toString());
        }
        if (defaultSubtypeCommand != null) {
          StringBuilder b = new StringBuilder();
          b.append("defaultSubtypeSdkCommand = ");
          b.append(formDataTypeRef).append(".");
          b.append(SignatureUtility.getTypeReference(SignatureCache.createTypeSignature(defaultSubtypeCommand.getDeclaringClass().getName()), validator));
          b.append(".").append(defaultSubtypeCommand.name());
          args.add(b.toString());
        }
        if (args.size() > 0) {
          source.append("(");
          for (int i = 0; i < args.size(); i++) {
            source.append(args.get(i));
            if (i < args.size() - 1) {
              source.append(", ");
            }
          }
          source.append(")");
        }
      }
    };
  }

  public static IAnnotationSourceBuilder createValidationStrategyProcess() {
    AnnotationSourceBuilder sourceBuilder = new AnnotationSourceBuilder(SignatureCache.createTypeSignature(RuntimeClasses.InputValidation)) {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, IJavaProject ownerProject, IImportValidator validator) throws CoreException {
        source.append("@" + SignatureUtility.getTypeReference(getSignature(), validator));
        source.append("(").append(validator.getTypeName(SignatureCache.createTypeSignature(RuntimeClasses.IValidationStrategy)));
        source.append(".PROCESS.class)");
      }
    };
    return sourceBuilder;
  }

  public static IAnnotationSourceBuilder createSupressWarningUnchecked() {
    AnnotationSourceBuilder sourceBuilder = new AnnotationSourceBuilder(SignatureCache.createTypeSignature(SuppressWarnings.class.getName()));
    sourceBuilder.addParameter("\"unchecked\"");
    return sourceBuilder;
  }

  public static IAnnotationSourceBuilder createInjectFieldTo(String param) {
    AnnotationSourceBuilder sourceBuilder = new AnnotationSourceBuilder(SignatureCache.createTypeSignature(InjectFieldTo.class.getName()));
    sourceBuilder.addParameter(param);
    return sourceBuilder;
  }

  public static IAnnotationSourceBuilder createReplaceAnnotationBuilder() {
    AnnotationSourceBuilder sourceBuilder = new AnnotationSourceBuilder(SignatureCache.createTypeSignature(RuntimeClasses.Replace));
    return sourceBuilder;
  }
}
