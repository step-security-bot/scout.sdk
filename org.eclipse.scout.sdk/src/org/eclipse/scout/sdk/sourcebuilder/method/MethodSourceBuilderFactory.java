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
package org.eclipse.scout.sdk.sourcebuilder.method;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.scout.sdk.sourcebuilder.annotation.AnnotationSourceBuilderFactory;
import org.eclipse.scout.sdk.sourcebuilder.field.IFieldSourceBuilder;
import org.eclipse.scout.sdk.sourcebuilder.type.ITypeSourceBuilder;
import org.eclipse.scout.sdk.util.ScoutUtility;
import org.eclipse.scout.sdk.util.signature.IImportValidator;
import org.eclipse.scout.sdk.util.signature.ITypeGenericMapping;
import org.eclipse.scout.sdk.util.signature.SignatureUtility;
import org.eclipse.scout.sdk.util.type.IMethodFilter;
import org.eclipse.scout.sdk.util.type.MethodFilters;
import org.eclipse.scout.sdk.util.type.MethodParameter;
import org.eclipse.scout.sdk.util.type.TypeUtility;

/**
 * <h3>{@link MethodSourceBuilderFactory}</h3> ...
 * 
 * @author aho
 * @since 3.10.0 07.03.2013
 */
public final class MethodSourceBuilderFactory {
  private MethodSourceBuilderFactory() {
  }

  public static IMethodSourceBuilder createConstructorSourceBuilder(String typeName) {
    return createConstructorSourceBuilder(typeName, Flags.AccPublic, new MethodParameter[0]);
  }

  public static IMethodSourceBuilder createConstructorSourceBuilder(String typeName, int flags, MethodParameter... parameters) {
    MethodSourceBuilder constructorSourceBuilder = new MethodSourceBuilder(typeName);
    constructorSourceBuilder.setFlags(flags);
    if (parameters != null) {
      constructorSourceBuilder.setParameters(parameters);
    }
    return constructorSourceBuilder;
  }

  public static IMethodSourceBuilder createOverrideMethodSourceBuilder(String methodName, IType declaringType) throws CoreException {
    IMethod method = TypeUtility.findMethodInSuperHierarchy(methodName, declaringType, TypeUtility.getSuperTypeHierarchy(declaringType));
    return createOverrideMethodSourceBuilder(method, declaringType);
  }

  public static IMethodSourceBuilder createOverrideMethodSourceBuilder(IMethod methodToOverride, IType declaringType) throws CoreException {
    if (Flags.isInterface(methodToOverride.getDeclaringType().getFlags()) || Flags.isAbstract(methodToOverride.getFlags())) {
      return createMethodSourceBuilder(methodToOverride, declaringType, MethodBodySourceBuilderFactory.createAutoGeneratedMethodBody());
    }
    else {
      return createMethodSourceBuilder(methodToOverride, declaringType, MethodBodySourceBuilderFactory.createSuperCallMethodBody(true));
    }
  }

  public static IMethodSourceBuilder createMethodSourceBuilder(IMethod method, IType contextType) throws CoreException {
    return createMethodSourceBuilder(method, contextType, MethodBodySourceBuilderFactory.createAutoGeneratedMethodBody());
  }

  public static IMethodSourceBuilder createMethodSourceBuilder(IMethod method, IType contextType, IMethodBodySourceBuilder bodySourceBuilder) throws CoreException {
    MethodSourceBuilder sourceBuilder = new MethodSourceBuilder(method.getElementName());
    sourceBuilder.setReturnTypeSignature(SignatureUtility.getReturnTypeSignatureResolved(method, contextType));
    String[] unresolvedExceptionSignatures = method.getExceptionTypes();
    String[] resolvedExceptionSignatures = new String[unresolvedExceptionSignatures.length];
    for (int i = 0; i < unresolvedExceptionSignatures.length; i++) {
      resolvedExceptionSignatures[i] = SignatureUtility.getResolvedSignature(unresolvedExceptionSignatures[i], contextType);
    }
    sourceBuilder.setExceptionSignatures(resolvedExceptionSignatures);
    sourceBuilder.setParameters(TypeUtility.getMethodParameters(method, contextType));
    int flags = method.getFlags() & (~Flags.AccTransient);
    if (!method.getDeclaringType().equals(contextType)) {
      if (!Flags.isAbstract(contextType.getFlags())) {
        flags = flags & (~Flags.AccAbstract);
      }
      if (Flags.isInterface(method.getDeclaringType().getFlags()) && Flags.isPackageDefault(flags)) {
        flags = flags | Flags.AccPublic;
      }
      sourceBuilder.addAnnotationSourceBuilder(AnnotationSourceBuilderFactory.createOverrideAnnotationSourceBuilder());
    }
    sourceBuilder.setFlags(flags);
    sourceBuilder.setMethodBodySourceBuilder(bodySourceBuilder);
    return sourceBuilder;
  }

  public static IMethodSourceBuilder createOverrideMethodSourceBuilder(ITypeSourceBuilder typeSourceBuilder, String methodName) throws CoreException {
    return createOverrideMethodSourceBuilder(typeSourceBuilder, methodName, MethodFilters.getNameFilter(methodName));
  }

  public static IMethodSourceBuilder createOverrideMethodSourceBuilder(ITypeSourceBuilder typeSourceBuilder, String methodName, IMethodFilter methodFilter) throws CoreException {

    String superTypeSignature = typeSourceBuilder.getSuperTypeSignature();
    IType superType = TypeUtility.getTypeBySignature(superTypeSignature);
    if (!TypeUtility.exists(superType)) {
      return null;
    }
    else {
      Map<String, ITypeGenericMapping> genericMapping = new HashMap<String, ITypeGenericMapping>();
      SignatureUtility.resolveGenericParametersInSuperHierarchy(Signature.createTypeSignature(typeSourceBuilder.getElementName(), true), new String[0], typeSourceBuilder.getSuperTypeSignature(), typeSourceBuilder.getInterfaceSignatures().toArray(new String[typeSourceBuilder.getInterfaceSignatures().size()]), genericMapping);
      IMethod methodToOverride = TypeUtility.findMethodInSuperTypeHierarchy(superType, TypeUtility.getSuperTypeHierarchy(superType), methodFilter);
      MethodSourceBuilder builder = new MethodSourceBuilder(methodName);

      // return type
      Map<String, String> localGenericMapping = genericMapping.get(methodToOverride.getDeclaringType().getFullyQualifiedName()).getParameters();
      builder.setReturnTypeSignature(SignatureUtility.getResolvedSignature(methodToOverride.getDeclaringType(), localGenericMapping, methodToOverride.getReturnType()));

      // exceptions
      String[] unresolvedExceptionSignatures = methodToOverride.getExceptionTypes();
      String[] resolvedExceptionSignatures = new String[unresolvedExceptionSignatures.length];
      for (int i = 0; i < unresolvedExceptionSignatures.length; i++) {
        resolvedExceptionSignatures[i] = SignatureUtility.getResolvedSignature(methodToOverride.getDeclaringType(), localGenericMapping, unresolvedExceptionSignatures[i]);
      }

      builder.setExceptionSignatures(resolvedExceptionSignatures);

      // parameters
      builder.setParameters(TypeUtility.getMethodParameters(methodToOverride, localGenericMapping));
      int flags = methodToOverride.getFlags() & (~Flags.AccTransient);
      flags = flags & (~Flags.AccAbstract);
      if (Flags.isInterface(methodToOverride.getDeclaringType().getFlags()) && Flags.isPackageDefault(flags)) {
        flags = flags | Flags.AccPublic;
      }
      builder.setFlags(flags);

      // override annotation
      builder.addAnnotationSourceBuilder(AnnotationSourceBuilderFactory.createOverrideAnnotationSourceBuilder());

      // add default body
      builder.setMethodBodySourceBuilder(MethodBodySourceBuilderFactory.createAutoGeneratedMethodBody());
      return builder;
    }
  }

  public static IMethodSourceBuilder createFieldGetterSourceBuilder(final String fieldSignature) {
    String fieldSimpleName = Signature.getSignatureSimpleName(fieldSignature);
    IMethodSourceBuilder getterBuilder = new MethodSourceBuilder("get" + fieldSimpleName);
    getterBuilder.setFlags(Flags.AccPublic);
    getterBuilder.setReturnTypeSignature(fieldSignature);
    getterBuilder.setMethodBodySourceBuilder(new IMethodBodySourceBuilder() {
      @Override
      public void createSource(IMethodSourceBuilder methodBuilder, StringBuilder source, String lineDelimiter, IJavaProject ownerProject, IImportValidator validator) throws CoreException {
        source.append("return getFieldByClass(");
        source.append(validator.getTypeName(fieldSignature));
        source.append(".class);");
      }
    });
    return getterBuilder;
  }

  public static IMethodSourceBuilder createColumnGetterSourceBuilder(final String columnSignature) {
    String columnSimpleName = Signature.getSignatureSimpleName(columnSignature);
    IMethodSourceBuilder getterBuilder = new MethodSourceBuilder("get" + columnSimpleName);
    getterBuilder.setFlags(Flags.AccPublic);
    getterBuilder.setReturnTypeSignature(columnSignature);
    getterBuilder.setMethodBodySourceBuilder(new IMethodBodySourceBuilder() {
      @Override
      public void createSource(IMethodSourceBuilder methodBuilder, StringBuilder source, String lineDelimiter, IJavaProject ownerProject, IImportValidator validator) throws CoreException {
        source.append("return getColumnSet().getColumnByClass(");
        source.append(validator.getTypeName(columnSignature));
        source.append(".class);");
      }
    });
    return getterBuilder;
  }

  public static IMethodSourceBuilder createGetter(IFieldSourceBuilder fieldSourceBuilder) {
    return createGetter(fieldSourceBuilder.getElementName(), fieldSourceBuilder.getSignature());
  }

  public static IMethodSourceBuilder createGetter(String fieldName, String signature) {
    StringBuilder methodName = new StringBuilder();
    if (Signature.SIG_BOOLEAN.equals(signature)) {
      methodName.append("is");
    }
    else {
      methodName.append("get");
    }
    String field = fieldName.replaceFirst("m\\_", "");
    if (field.length() > 0) {
      methodName.append(Character.toUpperCase(field.charAt(0)));
    }
    if (field.length() > 1) {
      methodName.append(field.substring(1));
    }
    IMethodSourceBuilder getterBuilder = new MethodSourceBuilder(methodName.toString());
    getterBuilder.setReturnTypeSignature(signature);
    getterBuilder.setFlags(Flags.AccPublic);
    getterBuilder.setMethodBodySourceBuilder(MethodBodySourceBuilderFactory.createSimpleMethodBody("return " + fieldName + ";"));
    return getterBuilder;
  }

  public static IMethodSourceBuilder createSetter(IFieldSourceBuilder fieldSourceBuilder) {
    return createSetter(fieldSourceBuilder.getElementName(), fieldSourceBuilder.getSignature());
  }

  public static IMethodSourceBuilder createSetter(String fieldName, String signature) {
    StringBuilder methodName = new StringBuilder();
    methodName.append("set");
    String field = fieldName.replaceFirst("m\\_", "");
    String paramName = ScoutUtility.ensureValidParameterName(field);
    methodName.append(ScoutUtility.ensureStartWithUpperCase(field));
    IMethodSourceBuilder getterBuilder = new MethodSourceBuilder(methodName.toString());
    getterBuilder.setFlags(Flags.AccPublic);
    getterBuilder.setReturnTypeSignature(Signature.SIG_VOID);
    getterBuilder.addParameter(new MethodParameter(paramName, signature));
    getterBuilder.setMethodBodySourceBuilder(MethodBodySourceBuilderFactory.createSimpleMethodBody(fieldName + " = " + paramName + ";"));
    return getterBuilder;
  }
}
