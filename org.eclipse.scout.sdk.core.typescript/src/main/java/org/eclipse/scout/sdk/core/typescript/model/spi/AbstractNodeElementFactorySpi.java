/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.typescript.model.spi;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.scout.sdk.core.typescript.model.api.IConstantValue;
import org.eclipse.scout.sdk.core.typescript.model.api.IDataType.DataTypeFlavor;
import org.eclipse.scout.sdk.core.typescript.model.api.INodeElementFactory;
import org.eclipse.scout.sdk.core.typescript.model.api.internal.NodeElementFactoryImplementor;
import org.eclipse.scout.sdk.core.util.CompositeObject;

public abstract class AbstractNodeElementFactorySpi extends AbstractNodeElementSpi<INodeElementFactory> implements NodeElementFactorySpi {

  private final Map<Object, Object> m_elements = new ConcurrentHashMap<>();

  protected AbstractNodeElementFactorySpi(NodeModuleSpi module) {
    super(module);
  }

  @Override
  protected INodeElementFactory createApi() {
    return new NodeElementFactoryImplementor(this);
  }

  @SuppressWarnings("unchecked")
  protected <ID, R> R getOrCreate(ID identifier, Function<ID, R> factory) {
    return (R) m_elements.computeIfAbsent(identifier, id -> factory.apply((ID) id));
  }

  @Override
  public SyntheticFieldSpi createSyntheticField(String name, DataTypeSpi dataType, ES6ClassSpi declaringClass) {
    return getOrCreate(new CompositeObject(name, SyntheticFieldSpi.class), id -> new SyntheticFieldSpi(containingModule(), name, dataType, declaringClass));
  }

  @Override
  public ObjectLiteralDataTypeSpi createObjectLiteralDataType(String name, ObjectLiteralSpi objectLiteral) {
    return getOrCreate(new CompositeObject(name, objectLiteral), id -> new ObjectLiteralDataTypeSpi(containingModule(), name, objectLiteral));
  }

  protected DataTypeSpi createCompositeDataType(DataTypeFlavor flavor, Collection<DataTypeSpi> componentDataTypes) {
    return getOrCreate(new CompositeObject(flavor, componentDataTypes), id -> new SimpleCompositeDataTypeSpi(containingModule(), flavor, componentDataTypes, 0));
  }

  protected DataTypeSpi createCompositeDataType(DataTypeFlavor flavor, Collection<DataTypeSpi> componentDataTypes, int arrayDimension) {
    return getOrCreate(new CompositeObject(flavor, componentDataTypes, arrayDimension), id -> new SimpleCompositeDataTypeSpi(containingModule(), flavor, componentDataTypes, arrayDimension));
  }

  @Override
  public DataTypeSpi createClassWithTypeArgumentsDataType(ES6ClassSpi classSpi, List<DataTypeSpi> arguments) {
    return getOrCreate(new SimpleEntry<>(classSpi, arguments), id -> new ES6ClassWithTypeArgumentsSpi(containingModule(), id.getKey(), id.getValue()));
  }

  @Override
  public DataTypeSpi createArrayDataType(DataTypeSpi componentDataType, int arrayDimension) {
    if (arrayDimension < 1) {
      return componentDataType;
    }

    var newDimension = arrayDimension;
    var leafComponentType = componentDataType;
    if (componentDataType != null && componentDataType.flavor() == DataTypeFlavor.Array) {
      newDimension += componentDataType.arrayDimension();
      leafComponentType = componentDataType.componentDataTypes().stream().findAny().orElse(null);
    }
    var componentDataTypes = Optional.ofNullable(leafComponentType)
        .map(Collections::singleton)
        .orElse(Collections.emptySet());

    return createCompositeDataType(DataTypeFlavor.Array, componentDataTypes, newDimension);
  }

  protected DataTypeSpi createUnionOrIntersectionDataType(Collection<DataTypeSpi> componentDataTypes, DataTypeFlavor unionOrIntersection) {
    if (componentDataTypes == null || componentDataTypes.isEmpty()) {
      return null;
    }
    if (componentDataTypes.size() == 1) {
      return componentDataTypes.stream().findFirst().orElse(null);
    }
    if (unionOrIntersection != DataTypeFlavor.Union && unionOrIntersection != DataTypeFlavor.Intersection) {
      return null;
    }

    return createCompositeDataType(unionOrIntersection, componentDataTypes.stream()
        .flatMap(componentDataType -> componentDataType.flavor() == unionOrIntersection ? componentDataType.componentDataTypes().stream() : Stream.of(componentDataType))
        .collect(Collectors.toSet()));
  }

  @Override
  public DataTypeSpi createUnionDataType(Collection<DataTypeSpi> componentDataTypes) {
    return createUnionOrIntersectionDataType(componentDataTypes, DataTypeFlavor.Union);
  }

  @Override
  public DataTypeSpi createIntersectionDataType(Collection<DataTypeSpi> componentDataTypes) {
    return createUnionOrIntersectionDataType(componentDataTypes, DataTypeFlavor.Intersection);
  }

  @Override
  public DataTypeSpi createConstantValueDataType(IConstantValue constantValue) {
    if (constantValue == null) {
      return null;
    }

    return getOrCreate(new CompositeObject(constantValue, ConstantValueDataTypeSpi.class), id -> new ConstantValueDataTypeSpi(containingModule(), constantValue));
  }
}
