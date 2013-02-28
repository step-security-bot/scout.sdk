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
package org.eclipse.scout.sdk.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.text.Document;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.sdk.extensions.runtime.classes.IRuntimeClasses;
import org.eclipse.scout.sdk.internal.ScoutSdk;
import org.eclipse.scout.sdk.util.internal.sigcache.SignatureCache;
import org.eclipse.scout.sdk.util.pde.PluginModelHelper;
import org.eclipse.scout.sdk.util.resources.ResourceUtility;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.ScoutBundleFilters;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeUtility;

/**
 * <h3>BcUtilities</h3> ...
 */
public final class ScoutUtility {

  public static final String JAVA_MARKER = "java ";

  private static final Pattern REGEX_LINE_SEP_CLEAN = Pattern.compile("(\\n)\\r");
  private static final Pattern REGEX_LINE_SEP_CLEAN2 = Pattern.compile("\\r(\\n)");

  private ScoutUtility() {
  }

  public static String getCommentBlock(String content) {
    StringBuilder builder = new StringBuilder();
    builder.append("//TODO ");
    String username = ScoutSdk.getDefault().getBundle().getBundleContext().getProperty("user.name");
    if (!StringUtility.isNullOrEmpty(username)) {
      builder.append("[" + username + "] ");
    }
    builder.append(content);
    return builder.toString();
  }

  public static String getCommentAutoGeneratedMethodStub() {
    return getCommentBlock("Auto-generated method stub.");
  }

  /**
   * strips a (IMethod) method body from its comments
   * this is needed in order to avoid wrong method property
   * assignments (e.g. if a commented "@BsiCaseBeanProperty"
   * annotation exists, then this method is NO BsiCaseBeanProperty
   * 
   * @param methodBody
   * @return
   */
  public static String removeComments(String methodBody) {
    if (methodBody == null) {
      return null;
    }
    String retVal = methodBody;
    try {
      retVal = methodBody.replaceAll("\\/\\/.*?\\\r\\\n", "");
      retVal = retVal.replaceAll("\\/\\/.*?\\\n", "");
      retVal = retVal.replaceAll("(?s)\\/\\*.*?\\*\\/", "");
    }
    catch (Throwable t) {
      // nop
    }
    return retVal;
  }

  public static String cleanLineSeparator(String buffer, ICompilationUnit icu) {
    return cleanLineSeparatorImpl(buffer, ResourceUtility.getLineSeparator(icu));
  }

  public static String cleanLineSeparator(String buffer, Document doc) {
    return cleanLineSeparatorImpl(buffer, ResourceUtility.getLineSeparator(doc));
  }

  private static String cleanLineSeparatorImpl(String buffer, String separator) {
    buffer = REGEX_LINE_SEP_CLEAN.matcher(buffer).replaceAll("$1");
    buffer = REGEX_LINE_SEP_CLEAN2.matcher(buffer).replaceAll("$1");
    int i;
    for (i = buffer.length(); i > 0; i--) {
      if (buffer.charAt(i - 1) != '\n') break;
    }
    return (buffer.substring(0, i) + "\n").replaceAll("\\n", separator);
  }

  public static String getIndent(IType type) {
    String indent = "";
    if (type.getDeclaringType() != null) {
      IType decType = type.getDeclaringType();
      while (decType != null) {
        decType = decType.getDeclaringType();
        indent += SdkProperties.TAB;
      }
    }
    return indent;
  }

  public static void registerServiceClass(IProject project, String extensionPoint, String elemType, String className, String requiredSessionClass, String serviceFactoryClass, IProgressMonitor monitor) throws CoreException {
    PluginModelHelper h = new PluginModelHelper(project);
    HashMap<String, String> attributes = new HashMap<String, String>();
    attributes.put("class", className);
    if (!h.PluginXml.existsSimpleExtension(extensionPoint, elemType, attributes)) {
      if (requiredSessionClass != null) {
        attributes.put("session", requiredSessionClass);
      }
      if (serviceFactoryClass != null) {
        attributes.put("factory", serviceFactoryClass);
      }
      h.PluginXml.addSimpleExtension(extensionPoint, elemType, attributes);
      h.save();
    }
  }

  public static void unregisterServiceProxy(IType interfaceType, IProgressMonitor monitor) throws CoreException {
    IScoutBundle interfaceBundle = ScoutTypeUtility.getScoutBundle(interfaceType.getJavaProject());
    for (IScoutBundle clientBundle : interfaceBundle.getChildBundles(ScoutBundleFilters.getBundlesOfTypeFilter(IScoutBundle.TYPE_CLIENT), false)) {
      unregisterServiceClass(clientBundle.getProject(), IRuntimeClasses.EXTENSION_POINT_CLIENT_SERVICE_PROXIES, IRuntimeClasses.EXTENSION_ELEMENT_CLIENT_SERVICE_PROXY, interfaceType.getFullyQualifiedName(), monitor);
    }
  }

  public static void unregisterServiceImplementation(IType serviceType, IProgressMonitor monitor) throws CoreException {
    IScoutBundle implementationBundle = ScoutTypeUtility.getScoutBundle(serviceType.getJavaProject());
    ScoutUtility.unregisterServiceClass(implementationBundle.getProject(), IRuntimeClasses.EXTENSION_POINT_SERVICES, IRuntimeClasses.EXTENSION_ELEMENT_SERVICE, serviceType.getFullyQualifiedName(), monitor);
    for (IScoutBundle serverBundle : implementationBundle.getParentBundles(ScoutBundleFilters.getBundlesOfTypeFilter(IScoutBundle.TYPE_SERVER), true)) {
      unregisterServiceClass(serverBundle.getProject(), IRuntimeClasses.EXTENSION_POINT_SERVICES, IRuntimeClasses.EXTENSION_ELEMENT_SERVICE, serviceType.getFullyQualifiedName(), serverBundle.getSymbolicName() + ".ServerSession", monitor);
    }
  }

  public static void unregisterServiceClass(IProject project, String extensionPoint, String elemType, String className, IProgressMonitor monitor) throws CoreException {
    unregisterServiceClass(project, extensionPoint, elemType, className, null, monitor);
  }

  public static void unregisterServiceClass(IProject project, String extensionPoint, String elemType, String className, String requiredSessionClass, IProgressMonitor monitor) throws CoreException {
    PluginModelHelper h = new PluginModelHelper(project);
    HashMap<String, String> attributes = new HashMap<String, String>();
    attributes.put("class", className);
    h.PluginXml.removeSimpleExtension(extensionPoint, elemType, attributes);
    h.save();
  }

  public static String getDefaultValueOf(String parameter) {
    if (parameter.length() == 1) {
      switch (parameter.charAt(0)) {
        case Signature.C_BOOLEAN:
          return "true";
        case Signature.C_BYTE:
          return "0";
        case Signature.C_CHAR:
          return "0";
        case Signature.C_DOUBLE:
          return "0";
        case Signature.C_FLOAT:
          return "0.0f";
        case Signature.C_INT:
          return "0";
        case Signature.C_LONG:
          return "0";
        case Signature.C_SHORT:
          return "0";
        case Signature.C_VOID:
          return null;
      }
    }
    return "null";
  }

  /**
   * Returns true if the package fragment has class files in it.
   * 
   * @param packageFragment
   * @param includeSubpackages
   *          to include all sub packages
   * @return true is there are existing class files found.
   * @throws JavaModelException
   */
  public static boolean hasExistingChildren(IPackageFragment packageFragment, boolean includeSubpackages) throws JavaModelException {
    for (IJavaElement element : packageFragment.getChildren()) {
      if (element instanceof IPackageFragment && includeSubpackages) {
        return hasExistingChildren((IPackageFragment) element, includeSubpackages);
      }
      else if (element.exists()) {
        return true;
      }
    }
    return false;
  }

  /**
   * <xmp>
   * import xx.yy.B;
   * class A{
   * }
   * // getReferencedType(A, "B") returns the type B
   * </xmp>
   * 
   * @param declaringType
   * @param typeName
   * @return
   * @throws JavaModelException
   */
  public static IType getReferencedType(IType declaringType, String typeName) throws JavaModelException {
    String[][] resolvedTypeName = declaringType.resolveType(typeName);
    if (resolvedTypeName != null && resolvedTypeName.length == 1) {
      String fqName = resolvedTypeName[0][0];
      if (fqName != null && fqName.length() > 0) {
        fqName = fqName + ".";
      }
      fqName = fqName + resolvedTypeName[0][1];
      IType foundType = TypeUtility.getType(fqName);
      if (foundType != null) {
        return foundType;
      }
    }
    ScoutSdk.logWarning("could not find referenced type '" + typeName + "' in '" + declaringType.getFullyQualifiedName() + "'.");
    return null;
  }

  public static String getReferencedTypeSignature(IType declaringType, String simpleTypeName) throws JavaModelException {
    String[][] resolvedTypeName = declaringType.resolveType(simpleTypeName);
    if (resolvedTypeName == null) {
      return Signature.createTypeSignature(simpleTypeName, false);
    }
    else if (resolvedTypeName.length > 0) {
      StringBuilder fqnBuilder = new StringBuilder();
      if (!StringUtility.isNullOrEmpty(resolvedTypeName[0][0])) {
        fqnBuilder.append(resolvedTypeName[0][0] + ".");
      }
      fqnBuilder.append(resolvedTypeName[0][1]);
      return SignatureCache.createTypeSignature(fqnBuilder.toString());
    }
    return null;
  }

  public static String sourceCodeToSql(String source) {
    StringBuilder buf = new StringBuilder();
    StringBuilder outsideSqlCode = new StringBuilder();
    // meta levels
    boolean incomment1 = false;// /*...*/
    boolean incomment0 = false;// //...
    boolean instring = false;// "..."
    for (int i = 0; i < source.length(); i++) {
      char ch = source.charAt(i);
      if (ch == '\\') {
        buf.append(ch);
        buf.append(source.charAt(i + 1));
        i++;
      }
      else if ((!incomment1) && (ch == '/' && source.charAt(i + 1) == '*' && source.charAt(i + 2) != '+')) {
        // go into comment 1
        incomment1 = true;
        i++;
        buf.append("/**");
      }
      else if (incomment1 && (ch == '*' && source.charAt(i + 1) == '/')) {
        // go out of comment 1
        i++;
        incomment1 = false;
        buf.append("**/");
      }
      else if ((!incomment1) && (!incomment0) && (ch == '/' && source.charAt(i + 1) == '/')) {
        // go into comment 0
        incomment0 = true;
        i++;
        buf.append("/**");
        if (i + 1 >= source.length()) {
          incomment0 = false;// eot
          buf.append("**/");
        }
      }
      else if ((!incomment1) && (incomment0) && (ch == '\n' || ch == '\r' || i + 1 >= source.length())) {
        // go out of comment 0
        incomment0 = false;
        buf.append("**/");
        buf.append(ch);
      }
      else if ((!incomment1) && (!incomment0) && (!instring) && (ch == '"')) {
        // go into string
        instring = true;
      }
      else if ((!incomment1) && (!incomment0) && (instring) && (ch == '"')) {
        // go out of string
        instring = false;
      }
      else if (incomment1 || incomment0 || instring) {
        // inside meta
        buf.append(ch);
      }
      else if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
        // out of meta: white space
        buf.append(ch);
      }
      else if (ch == '+') {
        // out of meta: concatenation
        if (outsideSqlCode.length() > 0) {
          buf.append("*/");
          outsideSqlCode.setLength(0);
        }
      }
      else {
        // out of string/comment: java code
        if (outsideSqlCode.length() == 0) {
          buf.append("/* " + JAVA_MARKER);
        }
        outsideSqlCode.append(ch);
        buf.append(ch);
      }
    }
    if (outsideSqlCode.length() > 0) {
      buf.append("*/");
      outsideSqlCode.setLength(0);
    }
    return buf.toString();
  }

  public static String sqlToSourceCode(String sql) {
    // ignore empty lines
    sql = sql.replace("[\\n\\r]+", "\\n");
    // meta levels
    boolean incomment = false;// /**...**/
    StringBuilder buf = new StringBuilder();
    StringBuilder currentSqlLine = new StringBuilder();
    for (int i = 0; i < sql.length(); i++) {
      char ch = sql.charAt(i);
      if (ch == '\\') {
        if (incomment) {
          buf.append(ch);
          buf.append(sql.charAt(i + 1));
        }
        else {
          if (currentSqlLine.length() == 0) currentSqlLine.append("\"");
          currentSqlLine.append(ch);
          currentSqlLine.append(sql.charAt(i + 1));
        }
        i++;
      }
      else if ((!incomment) && (ch == '/' && sql.charAt(i + 1) == '*' && sql.charAt(i + 2) == '*')) {
        // go into comment
        incomment = true;
        i = i + 2;
        if (currentSqlLine.length() > 0) {
          String line = currentSqlLine.toString();
          buf.append(line);
          if (!line.endsWith(" ")) {
            buf.append(" ");
          }
          buf.append("\"+");
          currentSqlLine.setLength(0);
        }
        buf.append("/+++");
      }
      else if (incomment && (ch == '*' && sql.charAt(i + 1) == '*' && sql.charAt(i + 2) == '/')) {
        // go out of comment
        i = i + 2;
        incomment = false;
        buf.append("+++/");
      }
      else if (incomment) {
        // inside meta
        buf.append(ch);
      }
      else if (ch == '\r' || ch == '\n') {
        // out of meta: newline
        if (currentSqlLine.length() > 0) {
          String line = currentSqlLine.toString();
          buf.append(line);
          if (!line.endsWith(" ")) {
            buf.append(" ");
          }
          buf.append("\"+");
          currentSqlLine.setLength(0);
        }
        buf.append("\n");
      }
      else {
        // out of string/comment: sql code
        if (currentSqlLine.length() == 0) currentSqlLine.append("\"");
        currentSqlLine.append(ch);
      }
    }
    if (currentSqlLine.length() > 0) {
      String line = currentSqlLine.toString();
      buf.append(line);
      buf.append("\"");
      currentSqlLine.setLength(0);
    }
    String s = buf.toString();
    s = s.replaceAll("/\\*([^+*].*[^*])\\*/", "\"+$1+\"");
    s = s.replaceAll("/\\+\\+\\+", "/*");
    s = s.replaceAll("\\+\\+\\+/", "*/");
    return s;
  }

  public static String[] getSourceCodeLines(String source) {
    if (source == null) return new String[0];
    if (source.indexOf('\n') >= 0) {
      return source.replace("\r", "").split("[\\n]");
    }
    else {
      return source.replace("\n", "").split("[\\r]");
    }
  }

  public static int getSourceCodeIndent(String source, boolean includeFirstLine) {
    String[] a = getSourceCodeLines(source);
    int min = 102400;
    int count = 0;
    for (int i = 0; i < a.length; i++) {
      if (i > 0 || includeFirstLine) {
        String s = a[i];
        if (s.trim().length() > 0) {
          int index = 0;
          while (index < s.length() && (s.charAt(index) == ' ' || s.charAt(index) == '\t')) {
            index++;
          }
          min = Math.min(min, index);
          count++;
        }
      }
    }
    return (count > 0 ? min : 0);
  }

  public static String addSourceCodeIndent(String source, int indent, boolean includeFirstLine) {
    StringBuffer buf = new StringBuffer();
    String[] a = getSourceCodeLines(source);
    char[] prefix = new char[indent];
    Arrays.fill(prefix, ' ');
    for (int i = 0; i < a.length; i++) {
      if (i > 0 || includeFirstLine) {
        buf.append(prefix);
      }
      buf.append(a[i]);
      if (i + 1 < a.length) {
        buf.append('\n');
      }
    }
    return buf.toString();
  }

  public static String removeSourceCodeIndent(String source, int indent) {
    StringBuffer buf = new StringBuffer();
    String[] a = getSourceCodeLines(source);
    for (int i = 0; i < a.length; i++) {
      String s = a[i];
      int index = 0;
      while (index < indent && index < s.length() && (s.charAt(index) == ' ' || s.charAt(index) == '\t')) {
        index++;
      }
      buf.append(s.substring(index));
      if (i + 1 < a.length) {
        buf.append('\n');
      }
    }
    return buf.toString();
  }

  public static String[] getEntities(IScoutBundle p) throws JavaModelException {
    TreeSet<String> ret = new TreeSet<String>();
    for (IScoutBundle b : p.getParentBundles(ScoutBundleFilters.getBundlesOfTypeFilter(IScoutBundle.TYPE_CLIENT, IScoutBundle.TYPE_SERVER, IScoutBundle.TYPE_SHARED), true)) {
      String bundleName = b.getSymbolicName();
      int bundleNameMin = bundleName.length() + 1;
      if (b.getJavaProject() != null) {
        for (IPackageFragmentRoot r : b.getJavaProject().getPackageFragmentRoots()) {
          for (IJavaElement je : r.getChildren()) {
            if (!je.isReadOnly() && je instanceof IPackageFragment) {
              String pckName = je.getElementName();
              if (pckName.startsWith(bundleName) && pckName.length() > bundleNameMin) {
                ret.add(pckName.substring(bundleNameMin));
              }
            }
          }
        }
      }
    }
    return ret.toArray(new String[ret.size()]);
  }
}
