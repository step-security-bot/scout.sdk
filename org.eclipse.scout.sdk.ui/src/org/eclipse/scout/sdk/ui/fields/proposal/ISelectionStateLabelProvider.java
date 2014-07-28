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
package org.eclipse.scout.sdk.ui.fields.proposal;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * <h3>{@link ISelectionStateLabelProvider}</h3>
 *
 * @author Andreas Hoegger
 * @since 3.8.0 07.02.2012
 */
public interface ISelectionStateLabelProvider extends ILabelProvider {

  Image getImageSelected(Object element);

  String getTextSelected(Object element);
}
