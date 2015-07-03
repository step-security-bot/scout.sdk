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
package org.eclipse.scout.sdk.core.model;

import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.collections4.Predicate;

/**
 * Contains {@link Predicate}s for {@link IField}s.
 */
public final class FieldFilters {

  private FieldFilters() {
  }

  /**
   * Creates and returns a new {@link Predicate} that evaluates to <code>true</code> if a field name (
   * {@link IField#getName()}) matches the given name.
   *
   * @param name
   *          The name for which the {@link Predicate} should evaluate to <code>true</code>
   * @return The new created {@link Predicate} matching the given name.
   */
  public static Predicate<IField> getNameFilter(final String name) {
    return new Predicate<IField>() {
      @Override
      public boolean evaluate(IField field) {
        return Objects.equals(field.getName(), name);
      }
    };
  }

  /**
   * Creates and returns a new {@link Predicate} that evaluates to <code>true</code> if a field has all of the given
   * flags (
   * {@link IField#getFlags()}).
   *
   * @param flags
   *          The flags for which the {@link Predicate} should evaluate to <code>true</code>
   * @return The new created {@link Predicate} matching all the given flags.
   * @see Flags
   */
  public static Predicate<IField> getFlagsFilter(final int flags) {
    return new Predicate<IField>() {
      @Override
      public boolean evaluate(IField field) {
        return (flags & field.getFlags()) == flags;
      }
    };
  }

  /**
   * Creates and returns a new {@link Predicate} that evaluates to <code>true</code> if a field name
   * {@link IField#getName()}) matches the given regular expression.
   * 
   * @param regex
   *          The regex for which the {@link Predicate} should evaluate to <code>true</code>.
   * @return The new created {@link Predicate} matching the given regular expression.
   * @see Pattern
   */
  public static Predicate<IField> getNameRegexFilter(final Pattern regex) {
    return new Predicate<IField>() {
      @Override
      public boolean evaluate(IField field) {
        return regex.matcher(field.getName()).matches();
      }
    };
  }
}
