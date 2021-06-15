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
package org.eclipse.scout.sdk.core.builder.java.comment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.eclipse.scout.sdk.core.builder.ISourceBuilder;
import org.eclipse.scout.sdk.core.builder.SourceBuilderWrapper;
import org.eclipse.scout.sdk.core.builder.java.IJavaBuilderContext;
import org.eclipse.scout.sdk.core.model.api.IJavaEnvironment;
import org.eclipse.scout.sdk.core.util.CoreUtils;
import org.eclipse.scout.sdk.core.util.Ensure;
import org.eclipse.scout.sdk.core.util.SdkException;
import org.eclipse.scout.sdk.core.util.Strings;

/**
 * <h3>{@link CommentBuilder}</h3>
 *
 * @since 6.1.0
 */
public class CommentBuilder<TYPE extends ICommentBuilder<TYPE>> extends SourceBuilderWrapper<TYPE> implements ICommentBuilder<TYPE> {

  private static final Pattern REGEX_COMMENT_PATTERN1 = Pattern.compile("^s*/\\*\\*?s*$");
  private static final Pattern REGEX_COMMENT_PATTERN2 = Pattern.compile("^s*\\*\\*?/s*$");
  private static final Pattern REGEX_COMMENT_PATTERN3 = Pattern.compile("^s*\\*.*$");

  protected CommentBuilder(ISourceBuilder<?> inner) {
    super(inner);
  }

  /**
   * Creates a new {@link ICommentBuilder} wrapping the given inner {@link ISourceBuilder}.
   * <p>
   * If the context of the inner {@link ISourceBuilder} is an {@link IJavaBuilderContext}, this context and its
   * {@link IJavaEnvironment} is re-used. Otherwise a new {@link IJavaBuilderContext} without a {@link IJavaEnvironment}
   * is created.
   *
   * @param inner
   *          The inner {@link ISourceBuilder}. Must not be {@code null}.
   * @return A new {@link ICommentBuilder}.
   */
  public static ICommentBuilder<?> create(ISourceBuilder<?> inner) {
    return new CommentBuilder<>(inner);
  }

  @Override
  public TYPE appendBlockCommentStart() {
    return append("/*");
  }

  @Override
  public TYPE appendJavaDocStart() {
    return appendBlockCommentStart().append('*');
  }

  @Override
  public TYPE appendBlockCommentEnd() {
    return append("*/");
  }

  @Override
  public TYPE appendTodo(CharSequence toDoMessage) {
    var msg = new StringBuilder("TODO ");
    var username = CoreUtils.getUsername();
    if (Strings.hasText(username)) {
      msg.append('[').append(username).append("] ");
    }
    msg.append(Ensure.notNull(toDoMessage));
    return appendSingleLineComment(msg);
  }

  @Override
  public TYPE appendSingleLineComment(CharSequence msg) {
    return append("// ").append(msg).nl();
  }

  @Override
  public TYPE appendTodoAutoGeneratedMethodStub() {
    return appendTodo("Auto-generated method stub.");
  }

  @Override
  public TYPE appendJavaDocLine(CharSequence comment) {
    return appendJavaDocStart().space().append(comment).space().appendBlockCommentEnd();
  }

  @Override
  public TYPE appendJavaDocComment(String comment) {
    return appendBlockComment(comment, true);
  }

  @Override
  public TYPE appendBlockComment(String comment) {
    return appendBlockComment(comment, false);
  }

  protected TYPE appendBlockComment(String comment, boolean isJavaDoc) {
    if (isJavaDoc) {
      appendJavaDocStart();
    }
    else {
      appendBlockCommentStart();
    }
    nl();

    try (var inputReader = new BufferedReader(new StringReader(comment))) {
      var line = inputReader.readLine();
      while (line != null) {
        if (!REGEX_COMMENT_PATTERN1.matcher(line).matches() && !REGEX_COMMENT_PATTERN2.matcher(line).matches()) {
          if (REGEX_COMMENT_PATTERN3.matcher(line).matches()) {
            append(line);
          }
          else {
            append("* ").append(line);
          }
          nl();
        }
        line = inputReader.readLine();
      }
    }
    catch (IOException e) {
      throw new SdkException("Unable to format comment.", e);
    }

    return appendBlockCommentEnd()
        .nl();
  }
}
