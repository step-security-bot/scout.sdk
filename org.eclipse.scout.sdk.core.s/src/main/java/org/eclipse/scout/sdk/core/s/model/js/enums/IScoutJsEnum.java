/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.s.model.js.enums;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.scout.sdk.core.s.model.js.IScoutJsElement;
import org.eclipse.scout.sdk.core.s.model.js.prop.IScoutJsPropertyValue;
import org.eclipse.scout.sdk.core.s.model.js.prop.ScoutJsEnumPropertyValue;
import org.eclipse.scout.sdk.core.s.model.js.prop.ScoutJsProperty;
import org.eclipse.scout.sdk.core.typescript.model.api.IDataType;
import org.eclipse.scout.sdk.core.typescript.model.api.INodeElement;

public interface IScoutJsEnum extends IScoutJsElement {

  default String referenceName() {
    return name();
  }

  INodeElement topLevelReference();

  List<String> constants();

  default Stream<? extends IScoutJsPropertyValue> createPropertyValues(ScoutJsProperty property) {
    return constants().stream().map(c -> new ScoutJsEnumPropertyValue(this, c, property));
  }

  boolean fulfills(IDataType dataType);
}
