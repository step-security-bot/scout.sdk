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
package org.eclipse.scout.sdk.core.model.api.internal;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.scout.sdk.core.builder.BuilderContext;
import org.eclipse.scout.sdk.core.builder.java.JavaSourceBuilder;
import org.eclipse.scout.sdk.core.model.api.IBreadthFirstJavaElementVisitor;
import org.eclipse.scout.sdk.core.model.api.IDepthFirstJavaElementVisitor;
import org.eclipse.scout.sdk.core.model.api.IJavaElement;
import org.eclipse.scout.sdk.core.model.api.IJavaEnvironment;
import org.eclipse.scout.sdk.core.model.api.ISourceRange;
import org.eclipse.scout.sdk.core.model.spi.JavaElementSpi;
import org.eclipse.scout.sdk.core.util.Ensure;
import org.eclipse.scout.sdk.core.util.visitor.DefaultDepthFirstVisitor;
import org.eclipse.scout.sdk.core.util.visitor.DepthFirstVisitorTypeAdapter;
import org.eclipse.scout.sdk.core.util.visitor.IBreadthFirstVisitor;
import org.eclipse.scout.sdk.core.util.visitor.IDepthFirstVisitor;
import org.eclipse.scout.sdk.core.util.visitor.TreeTraversals;
import org.eclipse.scout.sdk.core.util.visitor.TreeVisitResult;

/**
 * <h3>{@link AbstractJavaElementImplementor}</h3>Represents a Java element.
 *
 * @since 5.1.0
 */
public abstract class AbstractJavaElementImplementor<SPI extends JavaElementSpi> implements IJavaElement {
  protected SPI m_spi;

  protected AbstractJavaElementImplementor(SPI spi) {
    m_spi = spi;
  }

  @Override
  public IJavaEnvironment javaEnvironment() {
    return m_spi.getJavaEnvironment().wrap();
  }

  @Override
  public String elementName() {
    return m_spi.getElementName();
  }

  @Override
  public Optional<ISourceRange> source() {
    return Optional.ofNullable(m_spi.getSource());
  }

  @Override
  public SPI unwrap() {
    return m_spi;
  }

  public void internalSetSpi(SPI spi) {
    m_spi = spi;
  }

  @Override
  public TreeVisitResult visit(Consumer<IJavaElement> visitor) {
    return visit(element -> {
      visitor.accept(element);
      return TreeVisitResult.CONTINUE;
    });
  }

  @Override
  public <T extends IJavaElement> void visit(Consumer<T> visitor, Class<T> type) {
    visit(element -> {
      visitor.accept(element);
      return TreeVisitResult.CONTINUE;
    }, type);
  }

  @Override
  public TreeVisitResult visit(Function<IJavaElement, TreeVisitResult> visitor) {
    return visit(visitor, IJavaElement.class);
  }

  @Override
  public <T extends IJavaElement> TreeVisitResult visit(Function<T, TreeVisitResult> visitor, Class<T> type) {
    return visit(new DepthFirstVisitorTypeAdapter<>(visitor, type));
  }

  @Override
  public TreeVisitResult visit(IDepthFirstJavaElementVisitor visitor) {
    return visit(new DefaultDepthFirstVisitor<IJavaElement>() {
      @Override
      public TreeVisitResult preVisit(IJavaElement element, int level, int index) {
        return element.unwrap().acceptPreOrder(visitor, level, index);
      }

      @Override
      public boolean postVisit(IJavaElement element, int level, int index) {
        return element.unwrap().acceptPostOrder(visitor, level, index);
      }
    });
  }

  @Override
  public TreeVisitResult visit(IBreadthFirstJavaElementVisitor visitor) {
    Ensure.notNull(visitor);
    IBreadthFirstVisitor<IJavaElement> v = (element, level, index) -> element.unwrap().acceptLevelOrder(visitor, level, index);
    return TreeTraversals.create(v, IJavaElement::children).traverse(this);
  }

  protected TreeVisitResult visit(IDepthFirstVisitor<IJavaElement> visitor) {
    return TreeTraversals.create(visitor, IJavaElement::children).traverse(this);
  }

  @Override
  public int hashCode() {
    return m_spi.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this;
  }

  @Override
  public String toString() {
    return source()
        .map(ISourceRange::asCharSequence)
        .map(CharSequence::toString)
        .orElseGet(() -> toWorkingCopy().toSource(JavaSourceBuilder::create, new BuilderContext()).toString());
  }
}
