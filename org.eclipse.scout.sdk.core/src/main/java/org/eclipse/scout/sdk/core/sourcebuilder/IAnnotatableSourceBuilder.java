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
package org.eclipse.scout.sdk.core.sourcebuilder;

import java.util.List;

import org.eclipse.scout.sdk.core.sourcebuilder.annotation.IAnnotationSourceBuilder;
import org.eclipse.scout.sdk.core.util.CompositeObject;

/**
 * <h3>{@link IAnnotatableSourceBuilder}</h3>
 *
 * @author Andreas Hoegger
 * @since 3.10.0 07.03.2013
 */
public interface IAnnotatableSourceBuilder extends IJavaElementSourceBuilder {

  /**
   * Adds the given {@link IAnnotatableSourceBuilder} without a specific order
   * 
   * @param builder
   */
  void addAnnotation(IAnnotationSourceBuilder builder);

  /**
   * Adds the given {@link IAnnotationSourceBuilder} with the giver sort object.
   * 
   * @param sortKey
   * @param builder
   */
  void addSortedAnnotation(CompositeObject sortKey, IAnnotationSourceBuilder builder);

  /**
   * @param childOp
   * @return
   */
  boolean removeAnnotation(String elementName);

  /**
   * @return
   */
  List<IAnnotationSourceBuilder> getAnnotations();

}
