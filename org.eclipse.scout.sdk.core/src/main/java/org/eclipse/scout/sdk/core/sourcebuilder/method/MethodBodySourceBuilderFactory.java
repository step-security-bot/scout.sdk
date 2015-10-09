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
package org.eclipse.scout.sdk.core.sourcebuilder.method;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.scout.sdk.core.importvalidator.IImportValidator;
import org.eclipse.scout.sdk.core.signature.ISignatureConstants;
import org.eclipse.scout.sdk.core.sourcebuilder.ISourceBuilder;
import org.eclipse.scout.sdk.core.sourcebuilder.methodparameter.IMethodParameterSourceBuilder;
import org.eclipse.scout.sdk.core.util.CoreUtils;
import org.eclipse.scout.sdk.core.util.PropertyMap;

/**
 * <h3>{@link MethodBodySourceBuilderFactory}</h3>
 *
 * @author Andreas Hoegger
 * @since 3.10.0 07.03.2013
 */
public final class MethodBodySourceBuilderFactory {

  private MethodBodySourceBuilderFactory() {
  }

  public static ISourceBuilder createAutoGenerated(final IMethodSourceBuilder methodBuilder) {
    return new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append(CoreUtils.getCommentAutoGeneratedMethodStub());
        String returnTypeSignature = methodBuilder.getReturnTypeSignature();
        if (!StringUtils.isEmpty(returnTypeSignature) && !ISignatureConstants.SIG_VOID.equals(returnTypeSignature)) {
          source.append(lineDelimiter).append("return " + CoreUtils.getDefaultValueOf(returnTypeSignature) + ";");
        }
      }
    };
  }

  public static ISourceBuilder createSuperCall(final IMethodSourceBuilder methodBuilder, final boolean addAutoGeneratedMethodStubComment) {
    return new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        if (addAutoGeneratedMethodStubComment) {
          source.append(CoreUtils.getCommentAutoGeneratedMethodStub());
          source.append(lineDelimiter);
        }
        String returnTypeSignature = methodBuilder.getReturnTypeSignature();
        boolean isConstructor = StringUtils.isEmpty(returnTypeSignature);
        if (!isConstructor && !ISignatureConstants.SIG_VOID.equals(returnTypeSignature)) {
          source.append("return ");
        }
        source.append("super");
        if (!isConstructor) {
          source.append('.');
          source.append(methodBuilder.getElementName());
        }
        source.append('(');
        List<IMethodParameterSourceBuilder> parameters = methodBuilder.getParameters();
        if (parameters.size() > 0) {
          IMethodParameterSourceBuilder param = parameters.get(0);
          source.append(param.getElementName());
          for (int i = 1; i < parameters.size(); i++) {
            param = parameters.get(i);
            source.append(", ");
            source.append(param.getElementName());
          }
        }
        source.append(");");
      }
    };
  }

  public static ISourceBuilder createReturnClassReference(final String typeSignature) {
    return new ISourceBuilder() {
      @Override
      public void createSource(StringBuilder source, String lineDelimiter, PropertyMap context, IImportValidator validator) {
        source.append("return ").append(validator.useSignature(typeSignature)).append(".class;");
      }
    };
  }
}
