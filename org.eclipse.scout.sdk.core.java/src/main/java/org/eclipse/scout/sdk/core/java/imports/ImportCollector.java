/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.java.imports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.eclipse.scout.sdk.core.java.JavaTypes;
import org.eclipse.scout.sdk.core.java.builder.IJavaBuilderContext;
import org.eclipse.scout.sdk.core.java.model.api.IJavaEnvironment;
import org.eclipse.scout.sdk.core.util.Strings;

public class ImportCollector implements IImportCollector {
  private final IJavaBuilderContext m_context;
  private final Map<String/* simpleName */, ImportElement> m_imports = new HashMap<>();
  private final Map<String/* simpleName */, ImportElement> m_staticImports = new HashMap<>();

  public ImportCollector() {
    this(null);
  }

  public ImportCollector(IJavaBuilderContext context) {
    m_context = context;
  }

  protected static Stream<StringBuilder> getImports(Collection<ImportElement> imports) {
    return imports.stream()
        .map(ImportElement::getImport);
  }

  protected static void addFiltered(Iterable<ImportElement> elements, boolean includeExisting, Collection<ImportElement> collector) {
    for (var e : elements) {
      if (!includeExisting && e.m_fromExisting) {
        continue;
      }

      if (!e.m_static) {
        if (!e.m_used) {
          continue;
        }
        // don't create imports for java.lang.* classes
        if ("java.lang".equals(e.m_packageName)) {
          continue;
        }
      }
      collector.add(e);
    }
  }

  protected static Stream<StringBuilder> organizeImports(Iterable<ImportElement> unsortedList1, Iterable<ImportElement> unsortedList2, boolean includeExisting) {
    Collection<ImportElement> workList = new TreeSet<>(new ImportComparator());
    addFiltered(unsortedList1, includeExisting, workList);
    addFiltered(unsortedList2, includeExisting, workList);

    var lastGroup = -1;
    Collection<StringBuilder> result = new ArrayList<>(workList.size() + 7 /* max number of empty group lines */);
    for (var e : workList) {
      if (lastGroup > 0 && lastGroup != e.m_group) {
        // add empty lines for import grouping
        result.add(new StringBuilder(0));
      }
      result.add(createImportDeclaration(e.m_static, e.getImport()));
      lastGroup = e.m_group;
    }
    return result.stream();
  }

  @Override
  public Optional<IJavaBuilderContext> getContext() {
    return Optional.ofNullable(m_context);
  }

  @Override
  public Optional<IJavaEnvironment> getJavaEnvironment() {
    return getContext().flatMap(IJavaBuilderContext::environment);
  }

  @Override
  public String getQualifier() {
    return null;
  }

  @Override
  public void reserveElement(TypeReferenceDescriptor candidate) {
    registerElementInternal(candidate, false);
  }

  @Override
  public String registerElement(TypeReferenceDescriptor candidate) {
    return registerElementInternal(candidate, true);
  }

  protected String registerElementInternal(TypeReferenceDescriptor candidate, boolean markAsUsed) {
    var elem = m_imports.get(candidate.getSimpleName());
    if (elem == null) {
      m_imports.put(candidate.getSimpleName(), new ImportElement(false, candidate.getQualifier(), candidate.getSimpleName(), markAsUsed, false));
    }
    else {
      elem.m_used = markAsUsed || elem.m_used;
    }
    return candidate.getSimpleName();
  }

  @Override
  public String checkExistingImports(TypeReferenceDescriptor candidate) {
    // handle primitive and void types
    if (candidate.isBaseType() || Strings.isBlank(candidate.getQualifier())) {
      return candidate.getSimpleName();
    }

    var existingElem = m_imports.get(candidate.getSimpleName());
    if (existingElem != null && Objects.equals(existingElem.m_packageName, candidate.getQualifier())) {
      // already used with same package -> simple name possible
      if (existingElem.m_used) {
        return candidate.getSimpleName();
      }
      return null;//must register to use simple name
    }
    if (existingElem != null) {
      // already used with different package -> must qualify
      return candidate.getQualifiedName();
    }

    //may register the import
    return null;
  }

  @Override
  public String checkCurrentScope(TypeReferenceDescriptor candidate) {
    return null;
  }

  @Override
  public void addImport(CharSequence fqn) {
    addImport(fqn, false);
  }

  public void addImport(CharSequence fqn, boolean fromExisting) {
    var packageName = JavaTypes.qualifier(fqn);
    var simpleName = JavaTypes.simpleName(fqn);
    m_imports.put(simpleName, new ImportElement(false, packageName, simpleName, true, fromExisting));
  }

  @Override
  public void addStaticImport(CharSequence fqn) {
    addStaticImport(fqn, false);
  }

  protected void addStaticImport(CharSequence fqn, boolean fromExisting) {
    var packageName = JavaTypes.qualifier(fqn);
    var simpleName = JavaTypes.simpleName(fqn);
    m_staticImports.put(simpleName, new ImportElement(true, packageName, simpleName, true, fromExisting));
  }

  @Override
  public Stream<StringBuilder> getImports() {
    return getImports(m_imports.values());
  }

  @Override
  public Stream<StringBuilder> getStaticImports() {
    return getImports(m_staticImports.values());
  }

  @Override
  public Stream<StringBuilder> createImportDeclarations() {
    return createImportDeclarations(true);
  }

  @Override
  public Stream<StringBuilder> createImportDeclarations(boolean includeExisting) {
    return organizeImports(m_staticImports.values(), m_imports.values(), includeExisting);
  }

  private static final class ImportElement {
    private final boolean m_static;
    private final String m_packageName;
    private final String m_simpleName;
    private final boolean m_fromExisting;
    private final int m_group;

    private boolean m_used;

    private ImportElement(boolean isStatic, String packageName, String simpleName, boolean used, boolean fromExisting) {
      m_static = isStatic;
      m_packageName = packageName;
      m_simpleName = simpleName;
      m_used = used;
      m_fromExisting = fromExisting;
      m_group = calculateGroup(isStatic, packageName);
    }

    private static int calculateGroup(boolean isStaticImport, String packageName) {
      int factor;
      if (isStaticImport) {
        factor = 1;
      }
      else {
        factor = 10;
      }

      if (packageName != null) {
        if (packageName.startsWith("java.")) {
          return factor;
        }
        if (packageName.startsWith("javax.")) {
          return 2 * factor;
        }
        if (packageName.startsWith("org.")) {
          return 3 * factor;
        }
      }
      return 4 * factor;
    }

    public StringBuilder getImport() {
      if (m_packageName == null) {
        return new StringBuilder(m_simpleName);
      }

      return new StringBuilder(m_packageName.length() + m_simpleName.length() + 1)
          .append(m_packageName).append(JavaTypes.C_DOT).append(m_simpleName);
    }
  }

  public static StringBuilder createImportDeclaration(boolean isStatic, CharSequence fullImportExpr) {
    var importPart = "import ";
    var staticPart = "static ";

    var b = new StringBuilder(importPart.length() + staticPart.length() + fullImportExpr.length() + 1);
    b.append(importPart);
    if (isStatic) {
      b.append(staticPart);
    }
    return b.append(fullImportExpr).append(';');
  }

  /**
   * Sort in the following order: java, javax, org, other
   */
  private static final class ImportComparator implements Comparator<ImportElement>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(ImportElement e1, ImportElement e2) {
      var result = Integer.compare(e1.m_group, e2.m_group);
      if (result != 0) {
        return result;
      }

      result = compareStrings(e1.m_packageName, e2.m_packageName);
      if (result != 0) {
        return result;
      }
      return e1.m_simpleName.compareTo(e2.m_simpleName);
    }

    private static int compareStrings(@SuppressWarnings("TypeMayBeWeakened") String a, String b) {
      //noinspection StringEquality
      if (a == b) {
        return 0;
      }
      if (a == null) {
        return 1;
      }
      if (b == null) {
        return -1;
      }
      return a.compareTo(b);
    }
  }
}
