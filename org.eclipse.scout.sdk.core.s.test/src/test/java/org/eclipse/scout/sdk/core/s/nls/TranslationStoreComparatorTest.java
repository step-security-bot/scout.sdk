/*
 * Copyright (c) 2010-2021 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.core.s.nls;

import static org.eclipse.scout.sdk.core.s.nls.TranslationTestsHelper.createStore;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TranslationStoreComparatorTest {

  @Test
  public void testTrivial() {
    var s1 = createStore("test.Class", 100);
    var s2 = createStore("test.Class", 100);
    var comparator = TranslationStoreComparator.INSTANCE;
    assertEquals(0, comparator.compare(s1, s1));
    assertEquals(0, comparator.compare(s1, s2));
    assertEquals(0, comparator.compare(null, null));
    assertEquals(1, comparator.compare(s1, null));
    assertEquals(-1, comparator.compare(null, s1));
  }

  @Test
  public void testWithDifferentOrder() {
    var s1 = createStore("test.Class1", 100);
    var s2 = createStore("test.Class2", 200);
    var comparator = TranslationStoreComparator.INSTANCE;
    assertEquals(-1, comparator.compare(s1, s2));
    assertEquals(1, comparator.compare(s2, s1));
  }

  @Test
  public void testWithDifferentName() {
    var s1 = createStore("test.Class1", 100);
    var s2 = createStore("test.Class2", 100);
    var comparator = TranslationStoreComparator.INSTANCE;
    assertEquals(-1, comparator.compare(s1, s2));
    assertEquals(1, comparator.compare(s2, s1));
  }
}
