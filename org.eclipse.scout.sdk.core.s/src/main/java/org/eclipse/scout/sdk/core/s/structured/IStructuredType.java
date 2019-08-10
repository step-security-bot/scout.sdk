/*
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.core.s.structured;

import java.util.List;

import org.eclipse.scout.sdk.core.model.api.IJavaElement;

/**
 * <h3>{@link IStructuredType}</h3>
 *
 * @since 3.0.0
 */
public interface IStructuredType {

  IJavaElement getSiblingMethodFieldGetter(String methodName);

  IJavaElement getSibling(Categories category);

  List<IJavaElement> getElements(Categories category);

  <T extends IJavaElement> List<T> getElements(Categories category, Class<T> clazz);

  enum Categories {
    FIELD_LOGGER,
    FIELD_STATIC,
    FIELD_MEMBER,
    FIELD_UNKNOWN,
    ENUM,
    METHOD_CONSTRUCTOR,
    METHOD_CONFIG_PROPERTY,
    METHOD_CONFIG_EXEC,
    METHOD_FORM_DATA_BEAN,
    METHOD_OVERRIDDEN,
    METHOD_START_HANDLER,
    METHOD_INNER_TYPE_GETTER,
    METHOD_LOCAL_BEAN,
    METHOD_UNCATEGORIZED,
    TYPE_FORM_FIELD,
    TYPE_COLUMN,
    TYPE_CODE,
    TYPE_FORM,
    TYPE_TABLE,
    TYPE_TREE,
    TYPE_CALENDAR,
    TYPE_CALENDAR_ITEM_PROVIDER,
    TYPE_WIZARD,
    TYPE_WIZARD_STEP,
    TYPE_MENU,
    TYPE_VIEW_BUTTON,
    TYPE_KEYSTROKE,
    TYPE_COMPOSER_ATTRIBUTE,
    TYPE_COMPOSER_ENTRY,
    TYPE_FORM_HANDLER,
    TYPE_UNCATEGORIZED
  }
}
