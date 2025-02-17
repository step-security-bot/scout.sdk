/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.s.model.js.prop;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toCollection;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.scout.sdk.core.s.model.js.ScoutJsCoreConstants;
import org.eclipse.scout.sdk.core.s.model.js.ScoutJsModel;
import org.eclipse.scout.sdk.core.s.model.js.enums.ConstantValueUnionScoutEnum;
import org.eclipse.scout.sdk.core.s.model.js.enums.IScoutJsEnum;
import org.eclipse.scout.sdk.core.typescript.TypeScriptTypes;
import org.eclipse.scout.sdk.core.typescript.model.api.DataTypeAssignableEvaluator;
import org.eclipse.scout.sdk.core.typescript.model.api.DataTypeNameEvaluator;
import org.eclipse.scout.sdk.core.typescript.model.api.IDataType;
import org.eclipse.scout.sdk.core.typescript.model.api.IDataType.DataTypeFlavor;
import org.eclipse.scout.sdk.core.typescript.model.api.IES6Class;
import org.eclipse.scout.sdk.core.typescript.model.api.INodeElement;
import org.eclipse.scout.sdk.core.util.Ensure;
import org.eclipse.scout.sdk.core.util.FinalValue;
import org.eclipse.scout.sdk.core.util.visitor.TreeVisitResult;

public class ScoutJsPropertyType {

  @SuppressWarnings("StaticCollection")
  private static final Set<String> DATA_TYPE_NAMES_SUPPORTING_CHILD_MODELS = dataTypeNamesSupportingModels();

  private final IDataType m_dataType; // dataType may be null in case the property is based on a Field and the field has no datatype (cannot be detected. e.g. in JavaScript: this.myField = null)
  private final ScoutJsProperty m_declaringProperty;
  private final ScoutJsPropertySubType m_subType;
  private final FinalValue<Set<IScoutJsEnum>> m_enums;
  private final FinalValue<Boolean> m_isModelSupported;
  private final FinalValue<Collection<ScoutJsProperty>> m_childProperties;

  public ScoutJsPropertyType(IDataType dataType, ScoutJsPropertySubType subType, ScoutJsProperty declaringProperty) {
    m_dataType = dataType;
    m_subType = Ensure.notNull(subType);
    m_declaringProperty = Ensure.notNull(declaringProperty);
    m_enums = new FinalValue<>();
    m_isModelSupported = new FinalValue<>();
    m_childProperties = new FinalValue<>();
  }

  public ScoutJsPropertyType(IDataType dataType, ScoutJsProperty declaringProperty) {
    this(dataType, ScoutJsPropertySubType.NOTHING, declaringProperty);
  }

  private static Set<String> dataTypeNamesSupportingModels() {
    var supportModel = new HashSet<>(ScoutJsCoreConstants.CLASS_NAMES_MODEL_TYPES);
    supportModel.add(ScoutJsCoreConstants.CLASS_NAME_STATUS_OR_MODEL);
    supportModel.add(ScoutJsCoreConstants.CLASS_NAME_GRID_DATA);
    supportModel.add(ScoutJsCoreConstants.CLASS_NAME_WIDGET);
    supportModel.add(ScoutJsCoreConstants.CLASS_NAME_STATUS);
    return unmodifiableSet(supportModel);
  }

  @Override
  public String toString() {
    var toStringBuilder = new StringBuilder(dataType().map(IDataType::name).orElse("unknown"));
    if (subType() != ScoutJsPropertySubType.NOTHING) {
      toStringBuilder.append(" (sub-type=").append(subType()).append(")");
    }
    return toStringBuilder.toString();
  }

  public ScoutJsPropertySubType subType() {
    return m_subType;
  }

  public Optional<IDataType> dataType() {
    return Optional.ofNullable(m_dataType);
  }

  public ScoutJsProperty declaringProperty() {
    return m_declaringProperty;
  }

  public boolean isClassType() {
    return m_dataType != null && m_dataType.visit((childType, l, i) -> childType instanceof IES6Class ? TreeVisitResult.TERMINATE : TreeVisitResult.CONTINUE) == TreeVisitResult.TERMINATE;
  }

  public boolean isChildModelSupported() {
    return m_isModelSupported.computeIfAbsentAndGet(() -> {
      if (m_dataType == null) {
        return false;
      }
      return m_dataType.visit((childType, l, i) -> supportsChildModel(childType)) == TreeVisitResult.TERMINATE;
    });
  }

  /**
   * @return {@link TreeVisitResult#TERMINATE} means the given dataType supports child models. All other values not.
   */
  protected static TreeVisitResult supportsChildModel(INodeElement dataType) {
    if (!(dataType instanceof IES6Class)) {
      return TreeVisitResult.CONTINUE; // step into composites
    }
    var name = dataType.name();
    if (ScoutJsCoreConstants.CLASS_NAME_LOOKUP_CALL_OR_MODEL.equals(name)) {
      // no child model for LookupCalls: use reference to existing class instead
      return TreeVisitResult.SKIP_SUBTREE;
    }
    if (DATA_TYPE_NAMES_SUPPORTING_CHILD_MODELS.contains(name)) {
      // supports child models
      return TreeVisitResult.TERMINATE;
    }
    return TreeVisitResult.CONTINUE;
  }

  public Optional<String> displayName() {
    return dataType()
        .flatMap(ScoutJsCoreDataTypesUnwrapVisitor::unwrap)
        .map(d -> new DataTypeNameEvaluator(ScoutJsCoreDataTypesUnwrapVisitor::unwrappedChildren).eval(d));
  }

  public Stream<ScoutJsProperty> possibleChildProperties() {
    return m_childProperties
        .computeIfAbsentAndGet(() -> collectPossibleProperties(m_dataType, declaringProperty().scoutJsObject().scoutJsModel()).values())
        .stream();
  }

  protected static Map<String, ScoutJsProperty> collectPossibleProperties(IDataType type, ScoutJsModel model) {
    if (type == null) {
      return emptyMap();
    }

    var collector = new LinkedHashMap<String, ScoutJsProperty>();
    type.visit(new ScoutJsCoreDataTypesUnwrapVisitor((childType, l, i) -> {
      if (childType.flavor() == DataTypeFlavor.Intersection) {
        childType.childTypes()
            .map(c -> collectPossibleProperties(c, model))
            .reduce(ScoutJsPropertyType::intersection)
            .ifPresent(collector::putAll);
        return TreeVisitResult.SKIP_SUBTREE;
      }
      if (childType instanceof IES6Class clazz && !clazz.isEnum() && !clazz.isTypeAlias() && !clazz.isInterface()) {
        model.findScoutObjects()
            .withDeclaringClass(clazz)
            .withIncludeDependencies(true)
            .first().stream()
            .flatMap(o -> o.findProperties().withSuperClasses(true).stream())
            .forEach(p -> collector.put(p.name(), p));
      }
      return TreeVisitResult.CONTINUE;
    }));
    return collector;
  }

  protected static Map<String, ScoutJsProperty> intersection(Map<String, ScoutJsProperty> a, Map<String, ScoutJsProperty> b) {
    return a.entrySet().stream()
        .map(elementFromA -> {
          var fromB = b.get(elementFromA.getKey());
          if (fromB == null) {
            return null; // intersection: only keep elements of both maps
          }
          return ScoutJsProperty.choose(elementFromA.getValue(), fromB);
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(ScoutJsProperty::name, Function.identity(), Ensure::failOnDuplicates, LinkedHashMap::new));
  }

  public boolean isAssignableFrom(IDataType child) {
    return dataType()
        .map(dt -> dt.flavor() == DataTypeFlavor.Array ? dt.childTypes().findAny().orElse(null) : dt)
        .flatMap(ScoutJsCoreDataTypesUnwrapVisitor::unwrap)
        .filter(myType -> new DataTypeAssignableEvaluator(child, ScoutJsCoreDataTypesUnwrapVisitor::unwrappedChildren).fulfills(myType))
        .isPresent();
  }

  public boolean isEnumLike() {
    return !getScoutJsEnums().isEmpty();
  }

  public Stream<IScoutJsEnum> scoutJsEnums() {
    return getScoutJsEnums().stream();
  }

  protected Set<IScoutJsEnum> getScoutJsEnums() {
    return m_enums.computeIfAbsentAndGet(() -> dataType()
        .filter(d -> d.flavor() != DataTypeFlavor.Array)
        .stream()
        .flatMap(dataType -> Stream
            .concat(
                declaringProperty().scoutJsObject().scoutJsModel()
                    .findScoutEnums()
                    .withIncludeDependencies(true)
                    .withFulfillsDataType(dataType)
                    .stream(),
                ConstantValueUnionScoutEnum.create(declaringProperty().scoutJsObject().scoutJsModel(), dataType).stream()))
        .collect(toCollection(LinkedHashSet::new)));
  }

  public boolean isArray() {
    return m_dataType != null && m_dataType.flavor() == DataTypeFlavor.Array;
  }

  public boolean isBoolean() {
    return TypeScriptTypes._boolean.equals(dataTypeName());
  }

  public boolean isString() {
    return TypeScriptTypes._string.equals(dataTypeName());
  }

  protected String dataTypeName() {
    return dataType().map(IDataType::name).orElse(null);
  }
}
