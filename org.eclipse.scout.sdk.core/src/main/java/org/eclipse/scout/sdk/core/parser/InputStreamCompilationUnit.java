/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.core.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.scout.sdk.core.util.CoreUtils;
import org.eclipse.scout.sdk.core.util.SdkException;

/**
 * <h3>{@link InputStreamCompilationUnit}</h3>
 *
 * @author Matthias Villiger
 * @since 5.1.0
 */
class InputStreamCompilationUnit implements ICompilationUnit {

  private final InputStream m_src;
  private final Charset m_charset;
  private final char[] m_mainTypeName;
  private final char[] m_fileName;

  InputStreamCompilationUnit(InputStream javaSource, char[] mainTypeName, char[] fileName) {
    this(javaSource, mainTypeName, fileName, null);
  }

  InputStreamCompilationUnit(InputStream javaSource, char[] mainTypeName, char[] fileName, Charset charset) {
    m_src = Validate.notNull(javaSource);
    m_mainTypeName = Validate.notNull(mainTypeName);
    m_fileName = Validate.notNull(fileName);
    if (charset == null) {
      m_charset = StandardCharsets.UTF_8;
    }
    else {
      m_charset = charset;
    }
  }

  @Override
  public char[] getFileName() {
    return m_fileName;
  }

  @Override
  public char[] getContents() {
    try {
      StringBuilder content = CoreUtils.inputStreamToString(m_src, m_charset.name());
      char[] result = new char[content.length()];
      content.getChars(0, content.length(), result, 0);
      return result;
    }
    catch (IOException e) {
      throw new SdkException("Unable to read content.", e);
    }
  }

  @Override
  public char[] getMainTypeName() {
    return m_mainTypeName;
  }

  @Override
  public char[][] getPackageName() {
    return null; // skip package consistency checks
  }

  @Override
  public boolean ignoreOptionalProblems() {
    return false;
  }
}
