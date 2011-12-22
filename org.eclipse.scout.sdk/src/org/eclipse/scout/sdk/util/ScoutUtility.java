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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.text.Document;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.sdk.internal.ScoutSdk;
import org.eclipse.scout.sdk.util.jdt.JdtUtility;
import org.eclipse.scout.sdk.util.pde.PluginModelHelper;
import org.eclipse.scout.sdk.util.type.TypeUtility;

/**
 * <h3>BcUtilities</h3> ...
 */
public final class ScoutUtility {

  public static String NL = System.getProperty("line.separator");
  private static ScoutUtility instance = new ScoutUtility();
  public static final String JAVA_MARKER = "java ";

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

  public static String getLineSeparator(Document doc) {
    if (doc != null) {
      return doc.getDefaultLineDelimiter();
    }
    return System.getProperty("line.separator");
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
    return cleanLineSeparatorImpl(buffer, JdtUtility.getLineSeparator(icu));
  }

  public static String cleanLineSeparator(String buffer, Document doc) {
    return cleanLineSeparatorImpl(buffer, getLineSeparator(doc));
  }

  private static String cleanLineSeparatorImpl(String buffer, String separator) {
    buffer = buffer.replaceAll("(\\n)\\r", "$1");
    buffer = buffer.replaceAll("\\r(\\n)", "$1");
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
    return instance.hasExistingChildrenRecursive(packageFragment, includeSubpackages);
  }

  private boolean hasExistingChildrenRecursive(IPackageFragment packageFragment, boolean includeSubpackages) throws JavaModelException {
    for (IJavaElement element : packageFragment.getChildren()) {
      if (element instanceof IPackageFragment && includeSubpackages) {
        return hasExistingChildrenRecursive((IPackageFragment) element, includeSubpackages);
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
    return instance.getReferencedTypeImpl(declaringType, typeName);
  }

  private IType getReferencedTypeImpl(IType declaringType, String typeName) throws JavaModelException {
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
      return Signature.createTypeSignature(fqnBuilder.toString(), true);
    }
    return null;
  }


  public static String sourceCodeToSql(String source) {
    StringBuffer buf = new StringBuffer();
    StringBuffer outsideSqlCode = new StringBuffer();
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
    StringBuffer buf = new StringBuffer();
    StringBuffer currentSqlLine = new StringBuffer();
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
        buf.append("\n");
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
        buf.append("\n");
      }
    }
    return buf.toString();
  }

  public static List<String[]> extractSqlTableNames(String sql) {
    List<String[]> sqlTables = new ArrayList<String[]>();
    Pattern p = Pattern.compile("( *)([A-Za-z_]+)( *)([A-Za-z_]*)");
    try {
      int start = sql.toUpperCase().indexOf("FROM ") + 5;
      int end = sql.toUpperCase().indexOf("WHERE ");
      if (end == -1) {// no where, but maybe into?
        end = sql.toUpperCase().indexOf("INTO ");
      }
      sql = sql.substring(start, end);
      String[] possibleColumns = sql.split(",");
      for (String column : possibleColumns) {
        Matcher m = p.matcher(column);
        if (m.find()) {
          if (m.group(2) != null) {
            sqlTables.add(new String[]{m.group(2), m.group(4)});
          }
        }
      }
    }
    catch (Exception e) {
    }
    return sqlTables;
  }

  public static String extractWhereClause(String sql) {
    int start = sql.toUpperCase().indexOf("WHERE ");
    if (start == -1) {// no where clause...
      return "";
    }
    int end = sql.toUpperCase().indexOf("INTO ");
    if (end < start) {
      end = sql.indexOf(JAVA_MARKER);
    }

    if (end < start) {
      sql = sql.substring(start);
    }
    else {
      sql = sql.substring(start, end);
    }
    return sql;
  }

  public static List<String> extractSqlColumns(String sql) {
    List<String> sqlColumns = new ArrayList<String>();

    // remove all comments
    sql = sql.replaceAll("(/\\*)(.+)?(\\*/)", "");
    // replace all newlines and tabs.
    sql = sql.replaceAll("\\t|\\n", " ");
    sql = sql.replaceAll("  *", " ");
    sql = sql.substring(sql.toUpperCase().indexOf("SELECT") + 6) + " FROM "; /* need at least 1 FROM */
    // mssql legacy
    sql = sql.replaceAll("(TOP|top)\\s+[\\w#]+", "");

    String REGEX_NO_BRACKET = "[^(^)]*";
    String REGEX_NO_BRACKET_NO_COMMA = "[^(^)^,]";
    String REGEX_NO_BRACKET_NO_COMMA_OPTIONAL = REGEX_NO_BRACKET_NO_COMMA + "*";
    String REGEX_NO_BRACKET_NO_COMMA_MANDATORY = REGEX_NO_BRACKET_NO_COMMA + "+";
    String REGEX_FINISH_KEYWORDS = " FROM | INTO | from | into ";
    String REGEX_COLUMN_SYNTAX_END = "(,|" + REGEX_FINISH_KEYWORDS + ")";
    String REGEX_BRACKET_EXP = makeBraketRegex(REGEX_NO_BRACKET);
    String REGEX_BRACKET_EXP_2 = makeBraketRegex(REGEX_BRACKET_EXP);
    String REGEX_COLUMN = "((" + REGEX_NO_BRACKET_NO_COMMA_OPTIONAL + "(\\(" + REGEX_BRACKET_EXP_2 + "\\)))+" + REGEX_NO_BRACKET_NO_COMMA_OPTIONAL + "|" + REGEX_NO_BRACKET_NO_COMMA_MANDATORY + ")";
    String REGEX_COLUMN_INCL_TERM = REGEX_COLUMN + REGEX_COLUMN_SYNTAX_END;

    Pattern patternInclTerm = Pattern.compile(REGEX_COLUMN_INCL_TERM);
    Pattern patternColumnOnly = Pattern.compile(REGEX_COLUMN);
    Pattern patternSplitColumn = Pattern.compile(REGEX_FINISH_KEYWORDS + "\\(" + REGEX_BRACKET_EXP_2 + "\\)");

    Matcher matcher = patternInclTerm.matcher(sql);
    boolean finished = false;
    while (matcher.find() && !finished) {
      String columnInclTerm = matcher.group();
      String column = patternSplitColumn.split(columnInclTerm)[0];
      String replace = columnInclTerm.replace(column, "");
      String REGEX_FINSISH = REGEX_NO_BRACKET_NO_COMMA_OPTIONAL + "(" + REGEX_FINISH_KEYWORDS + ").*";
      finished = replace.matches(REGEX_FINSISH);
      Matcher matcher2 = patternColumnOnly.matcher(column);
      if (matcher2.find()) {
        column = matcher2.group();
        sqlColumns.add(column.trim());
      }
    }
    return sqlColumns;
  }

  private static String makeBraketRegex(String REGEX_NO_BRACKET) {
    return REGEX_NO_BRACKET + "(\\(" + REGEX_NO_BRACKET + "\\)" + REGEX_NO_BRACKET + ")*";
  }

}
