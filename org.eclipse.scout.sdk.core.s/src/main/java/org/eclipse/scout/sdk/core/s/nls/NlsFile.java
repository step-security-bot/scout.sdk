/*
 * Copyright (c) 2010-2020 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.core.s.nls;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;

import org.eclipse.scout.sdk.core.util.Ensure;
import org.eclipse.scout.sdk.core.util.FinalValue;
import org.eclipse.scout.sdk.core.util.SdkException;
import org.eclipse.scout.sdk.core.util.Strings;

/**
 * Represents an .nls file.
 */
public class NlsFile {

  /**
   * The NLS class key name. The value of this key is the fully qualified name of the primary text provider service the
   * nls file points to.
   */
  public static final String NLS_CLASS_KEY_NAME = "Nls-Class";

  private final Path m_file;
  private final FinalValue<Optional<String>> m_nlsClass = new FinalValue<>();

  public NlsFile(Path file) {
    m_file = Ensure.notNull(file);
  }

  /**
   * @return The fully qualified class name of the text provider service the nls file refers or an empty
   *         {@link Optional} if the property does not exist in the file.
   */
  public Optional<String> nlsClassFqn() {
    return m_nlsClass.computeIfAbsentAndGet(this::parseNlsClass);
  }

  protected Optional<String> parseNlsClass() {
    Properties properties = new Properties();
    try (InputStream in = new BufferedInputStream(Files.newInputStream(path()))) {
      properties.load(in);
    }
    catch (IOException e) {
      throw new SdkException("Unable to read nls file '{}'.", path(), e);
    }
    String nlsClass = properties.getProperty(NLS_CLASS_KEY_NAME);
    if (nlsClass != null) {
      nlsClass = nlsClass.trim();
    }
    return Strings.notBlank(nlsClass);
  }

  /**
   * @return The {@link Path} of the file.
   */
  public Path path() {
    return m_file;
  }

  /**
   * @param stack
   *          The stack in which the store should be searched.
   * @return The {@link ITranslationStore} within the given {@link TranslationStoreStack stack} whose service matches
   *         the one of this nls file.
   */
  public Optional<ITranslationStore> findMatchingStoreIn(TranslationStoreStack stack) {
    return nlsClassFqn()
        .flatMap(fqn -> Optional.ofNullable(stack)
            .map(TranslationStoreStack::allStores)
            .flatMap(stores -> stores
                .filter(store -> store.service().type().name().equals(fqn))
                .findAny()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NlsFile nlsFile = (NlsFile) o;
    return m_file.equals(nlsFile.m_file);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_file);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", NlsFile.class.getSimpleName() + " [", "]").add("file=" + m_file).toString();
  }
}
