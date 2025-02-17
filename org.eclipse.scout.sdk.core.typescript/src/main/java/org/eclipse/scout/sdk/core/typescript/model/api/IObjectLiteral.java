/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.typescript.model.api;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.scout.sdk.core.typescript.model.spi.ObjectLiteralSpi;

public interface IObjectLiteral extends INodeElement {
  @Override
  ObjectLiteralSpi spi();

  Map<String, IConstantValue> properties();

  Optional<IConstantValue> find(JsonPointer pointer);

  Optional<IConstantValue> find(CharSequence jsonPointer);

  Optional<IConstantValue> property(String name);

  Optional<IObjectLiteral> propertyAsObjectLiteral(String name);

  Optional<String> propertyAsString(String name);

  Optional<IES6Class> propertyAsES6Class(String name);

  <T> Optional<T> propertyAs(String name, Class<T> type);

  Stream<IObjectLiteral> childObjectLiterals();

  IDataType createDataType(String name);
}
