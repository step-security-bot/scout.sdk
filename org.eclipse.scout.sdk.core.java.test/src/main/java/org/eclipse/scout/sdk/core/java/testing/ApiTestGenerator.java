/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.java.testing;

import static java.util.stream.Collectors.joining;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.scout.sdk.core.java.JavaTypes;
import org.eclipse.scout.sdk.core.java.JavaUtils;
import org.eclipse.scout.sdk.core.java.imports.IImportCollector;
import org.eclipse.scout.sdk.core.java.imports.IImportValidator;
import org.eclipse.scout.sdk.core.java.imports.ImportCollector;
import org.eclipse.scout.sdk.core.java.imports.ImportValidator;
import org.eclipse.scout.sdk.core.java.model.api.Flags;
import org.eclipse.scout.sdk.core.java.model.api.IAnnotatable;
import org.eclipse.scout.sdk.core.java.model.api.IField;
import org.eclipse.scout.sdk.core.java.model.api.IMethod;
import org.eclipse.scout.sdk.core.java.model.api.IType;
import org.eclipse.scout.sdk.core.util.SdkException;
import org.eclipse.scout.sdk.core.util.Strings;
import org.junit.jupiter.api.Assertions;

/**
 * <h3>{@link ApiTestGenerator}</h3>
 *
 * @since 3.10.0 2013-08-26
 */
@SuppressWarnings("HardcodedLineSeparator")
public class ApiTestGenerator {
  public static final String NL = "\n";

  private final IType m_element;
  private final Set<String> m_usedMemberNames;

  public ApiTestGenerator(IType element) {
    m_element = element;
    m_usedMemberNames = new HashSet<>();
  }

  protected static void buildField(IField field, String fieldVarName, StringBuilder source, String flagsRef) {
    source.append("assertHasFlags(").append(fieldVarName).append(", ").append(getFlagsSource(field.flags(), flagsRef)).append(");").append(NL);
    source.append("assertFieldType(").append(fieldVarName).append(", ").append(JavaUtils.toStringLiteral(field.dataType().reference())).append(");").append(NL);
    createAnnotationsAsserts(field, source, fieldVarName);
  }

  protected static String getFlagsSource(int flags, String flagsRef) {
    var flagSrc = Arrays.stream(Flags.class.getDeclaredFields())
        .filter(f -> f.getType() == int.class)
        .filter(f -> (intValueOf(f) & flags) != 0)
        .map(f -> flagsRef + JavaTypes.C_DOT + f.getName())
        .collect(joining(" | "));
    if (Strings.isBlank(flagSrc)) {
      return flagsRef + JavaTypes.C_DOT + "AccDefault";
    }
    return flagSrc;
  }

  protected static int intValueOf(Field f) {
    try {
      return f.getInt(null);
    }
    catch (IllegalArgumentException | IllegalAccessException e) {
      throw new SdkException(e);
    }
  }

  public static void createAnnotationsAsserts(IAnnotatable annotatable, StringBuilder source, String annotatableRef) {
    var annotations = annotatable.annotations().stream().toList();
    var string = annotations.stream()
        .map(a -> a.type().reference())
        .map(annotationRef -> "assertAnnotation(" + annotatableRef + ", \"" + annotationRef + "\");" + NL)
        .collect(joining("", "assertEquals(" + annotations.size() + ", " + annotatableRef + ".annotations().stream().count(), \"annotation count\");" + NL, ""));
    source.append(string);
  }

  public String buildSource() {
    IImportCollector collector = new ImportCollector();
    IImportValidator validator = new ImportValidator(collector);
    var sourceBuilder = new StringBuilder();
    var typeVarName = getMemberName(m_element.elementName());

    sourceBuilder.append("/**").append(NL);
    sourceBuilder.append("* @Generated with ").append(getClass().getName()).append(NL);
    sourceBuilder.append("*/").append(NL);

    sourceBuilder.append("private static void testApiOf").append(m_element.elementName()).append('(').append("IType ").append(typeVarName).append(") {").append(NL);
    buildType(m_element, typeVarName, sourceBuilder, validator);
    sourceBuilder.append('}');

    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertAnnotation");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertFieldExist");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertFieldType");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertHasFlags");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertHasSuperClass");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertHasSuperInterfaces");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertMethodExist");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertMethodReturnType");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertNoCompileErrors");
    collector.addStaticImport(SdkJavaAssertions.class.getName() + ".assertTypeExists");
    collector.addStaticImport(Assertions.class.getName() + ".assertArrayEquals");
    collector.addStaticImport(Assertions.class.getName() + ".assertEquals");
    collector.addStaticImport(Assertions.class.getName() + ".assertTrue");

    var result = new StringBuilder();
    collector.createImportDeclarations()
        .forEach(i -> result.append(i).append(NL));
    result.append(NL);
    result.append(sourceBuilder);
    return result.toString();
  }

  protected void buildType(IType type, String typeVarName, StringBuilder source, IImportValidator validator) {
    var flagsRef = validator.useReference(Flags.class.getName());

    source.append("assertHasFlags(").append(typeVarName).append(", ").append(getFlagsSource(type.flags(), flagsRef)).append(");").append(NL);

    // super type
    type.superClass()
        .map(IType::reference)
        .ifPresent(ref -> source
            .append("assertHasSuperClass(")
            .append(typeVarName).append(", \"")
            .append(ref)
            .append("\");")
            .append(NL));

    // interfaces
    var interfaces = type.superInterfaces().toList();
    if (!interfaces.isEmpty()) {
      source.append("assertHasSuperInterfaces(").append(typeVarName).append(", new String[]{");
      for (var i = 0; i < interfaces.size(); i++) {
        source.append(JavaUtils.toStringLiteral(interfaces.get(i).reference()));
        if (i < interfaces.size() - 1) {
          source.append(", ");
        }
      }
      source.append("});").append(NL);
    }
    createAnnotationsAsserts(type, source, typeVarName);
    source.append(NL);

    // fields
    source.append("// fields of ").append(type.elementName()).append(NL);
    var fields = type.fields().stream().toList();
    source.append("assertEquals(").append(fields.size()).append(", ").append(typeVarName).append(".fields().stream().count(), \"field count of '").append(type.name()).append("'\");").append(NL);
    for (var f : fields) {
      var fieldVarName = getMemberName(f.elementName());
      source.append("var ").append(fieldVarName).append(" = ").append("assertFieldExist(").append(typeVarName).append(", \"").append(f.elementName()).append("\");").append(NL);
      buildField(f, fieldVarName, source, flagsRef);
    }
    source.append(NL);

    // methods
    var methods = type.methods().stream().toList();
    source.append("assertEquals(").append(methods.size()).append(", ").append(typeVarName).append(".methods().stream().count(), \"method count of '").append(type.name()).append("'\");").append(NL);
    for (var method : methods) {
      var methodVarName = getMemberName(method.elementName());
      source.append("var ").append(methodVarName).append(" = ").append("assertMethodExist(").append(typeVarName).append(", \"").append(method.elementName()).append('"');
      var parameterTypes = method.parameters().stream().toList();
      if (!parameterTypes.isEmpty()) {
        source.append(parameterTypes.stream()
            .map(parameterType -> JavaUtils.toStringLiteral(parameterType.dataType().reference()))
            .collect(joining(", ", ", new String[]{", "}")));
      }
      source.append(");").append(NL);
      buildMethod(method, methodVarName, source);
    }
    source.append(NL);

    // inner types
    var innerTypes = type.innerTypes().stream().toList();
    source.append("assertEquals(").append(innerTypes.size()).append(", ")
        .append(typeVarName).append(".innerTypes().stream().count(), \"inner types count of '").append(type.elementName()).append("'\");").append(NL);
    for (var innerType : innerTypes) {
      var innerTypeVarName = getMemberName(innerType.elementName());
      source.append("// type ").append(innerType.elementName()).append(NL);
      source.append("var ").append(innerTypeVarName).append(" = ");
      source.append("assertTypeExists(").append(typeVarName).append(", \"").append(innerType.elementName()).append("\");").append(NL);
      buildType(innerType, innerTypeVarName, source, validator);
    }
  }

  protected static void buildMethod(IMethod method, String methodVarName, StringBuilder source) {
    if (method.isConstructor()) {
      source.append("assertTrue(").append(methodVarName).append(".isConstructor());").append(NL);
    }

    method.returnType()
        .map(IType::reference)
        .ifPresent(ref -> source.append("assertMethodReturnType(")
            .append(methodVarName)
            .append(", \"")
            .append(ref)
            .append("\");")
            .append(NL));

    createAnnotationsAsserts(method, source, methodVarName);
  }

  private String getMemberName(String e) {
    var memberName = Introspector.decapitalize(e);
    if (m_usedMemberNames.contains(memberName)) {
      var counter = 1;
      var workingName = memberName + counter;
      while (m_usedMemberNames.contains(workingName)) {
        counter++;
        workingName = memberName + counter;
      }
      memberName = workingName;
    }
    m_usedMemberNames.add(memberName);
    return memberName;
  }

}
