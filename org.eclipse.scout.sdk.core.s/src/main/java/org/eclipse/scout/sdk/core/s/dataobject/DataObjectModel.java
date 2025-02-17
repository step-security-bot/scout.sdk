/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.s.dataobject;

import static java.util.Collections.unmodifiableList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import org.eclipse.scout.sdk.core.java.model.annotation.GeneratedAnnotation;
import org.eclipse.scout.sdk.core.java.model.api.Flags;
import org.eclipse.scout.sdk.core.java.model.api.IAnnotatable;
import org.eclipse.scout.sdk.core.java.model.api.IMethod;
import org.eclipse.scout.sdk.core.java.model.api.IType;
import org.eclipse.scout.sdk.core.s.dataobject.DataObjectNode.DataObjectNodeKind;
import org.eclipse.scout.sdk.core.s.java.annotation.IgnoreConvenienceMethodGenerationAnnotation;
import org.eclipse.scout.sdk.core.s.java.apidef.IScoutApi;
import org.eclipse.scout.sdk.core.util.Ensure;

/**
 * Parsed model for DataObjects containing the declared nodes (DoValue/DoList)
 */
public class DataObjectModel {

  private final IType m_source;
  private final List<DataObjectNode> m_nodes;

  protected DataObjectModel(IType source, List<DataObjectNode> nodes) {
    m_source = Ensure.notNull(source);
    m_nodes = nodes;
  }

  /**
   * Wraps the {@link IType} given into an {@link Optional} {@link DataObjectModel}.
   * 
   * @param dataObject
   *          The {@link IType} to parse (including super types). May be {@code null}.
   * @return The model or an empty {@link Optional} if the given {@link IType} is no data object.
   */
  public static Optional<DataObjectModel> wrap(IType dataObject) {
    return Optional.ofNullable(dataObject)
        .filter(DataObjectModel::isValid)
        .map(DataObjectModel::parse);
  }

  protected static boolean isValid(IType candidate) {
    var flags = candidate.flags();
    if (Flags.isInterface(flags) || !Flags.isPublic(flags) || Flags.isEnum(flags)) {
      return false;
    }
    var scoutApi = candidate.javaEnvironment().api(IScoutApi.class);
    if (scoutApi.isEmpty()) {
      return false;
    }
    var iDataObject = scoutApi.orElseThrow().IDataObject();
    return !isIgnored(candidate) && candidate.isInstanceOf(iDataObject);
  }

  protected static boolean isIgnored(IAnnotatable annotatable) {
    return annotatable.annotations()
        .withManagedWrapper(IgnoreConvenienceMethodGenerationAnnotation.class)
        .existsAny();
  }

  protected static DataObjectModel parse(IType dataObject) {
    var nodes = dataObject.methods()
        .withSuperTypes(true)
        .withFlags(Flags.AccPublic)
        .stream()
        .flatMap(m -> parseDoMethod(dataObject, m).stream())
        .collect(toMap(DataObjectNode::name, identity(), DataObjectModel::preferInheritedNodes, LinkedHashMap::new));
    return new DataObjectModel(dataObject, new ArrayList<>(nodes.values()));
  }

  /**
   * If a node exists in a super class and the root class: ignore the one on the root class as only the chained setters
   * are required.
   */
  protected static DataObjectNode preferInheritedNodes(DataObjectNode a, DataObjectNode b) {
    if (b.isInherited() && !a.isInherited()) {
      return b;
    }
    return a;
  }

  protected static Optional<DataObjectNode> parseDoMethod(IType source, IMethod method) {
    var flags = method.flags();
    if (method.isConstructor() || Flags.isStatic(flags)) {
      return Optional.empty();
    }
    if (Flags.isBridge(flags) || Flags.isSynthetic(flags)) {
      return Optional.empty();
    }
    var declaringTypeFlags = method.requireDeclaringType().flags();
    if (Flags.isInterface(declaringTypeFlags)) {
      return Optional.empty();
    }
    var hasParameters = method.parameters().first().isPresent();
    if (hasParameters || isIgnored(method)) {
      return Optional.empty();
    }
    if (method.annotations().withManagedWrapper(GeneratedAnnotation.class).existsAny()) {
      return Optional.empty();
    }

    var returnType = method.requireReturnType();

    // kind (DoValue/DoList/DoSet/DoCollection)
    var optKind = DataObjectNodeKind.valueOf(returnType);
    if (optKind.isEmpty()) {
      return Optional.empty();
    }

    // value type
    var optDoValue = parseValueType(returnType);
    if (optDoValue.isEmpty()) {
      return Optional.empty();
    }

    var declaringType = method.declaringType().orElse(null);
    var hasJavaDoc = method.javaDoc().isPresent();
    var isInherited = declaringType != source || isImplementedInSuperHierarchy(method);
    return Optional.of(new DataObjectNode(optKind.orElseThrow(), method, optDoValue.orElseThrow(), isInherited, hasJavaDoc));
  }

  protected static boolean isImplementedInSuperHierarchy(IMethod method) {
    return method.superMethods().withSelf(false).stream().anyMatch(sm -> !Flags.isAbstract(sm.flags()) && !Flags.isInterface(sm.flags()));
  }

  protected static Optional<IType> parseValueType(IType returnType) {
    return returnType.typeArguments().findAny();
  }

  /**
   * @return An unmodifiable {@link List} holding all nodes in declaration order.
   */
  public List<DataObjectNode> nodes() {
    return unmodifiableList(m_nodes);
  }

  /**
   * @return The source {@link IType} this model was built on. Is never {@code null}.
   */
  public IType unwrap() {
    return m_source;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", DataObjectModel.class.getSimpleName() + " [", "]")
        .add("source=" + m_source)
        .add("nodes=" + m_nodes)
        .toString();
  }
}
