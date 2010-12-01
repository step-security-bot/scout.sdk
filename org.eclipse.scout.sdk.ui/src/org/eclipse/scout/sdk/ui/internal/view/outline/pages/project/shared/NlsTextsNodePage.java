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
package org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.shared;

import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.view.outline.pages.AbstractPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IScoutPageConstants;
import org.eclipse.scout.sdk.workspace.IScoutBundle;

/**
 * <h3>IconNodePage</h3> a node forks the Icon editor to open on selection.
 */
public class NlsTextsNodePage extends AbstractPage {

  public NlsTextsNodePage(IPage parentPage) {
    setParent(parentPage);
    setName("Texts");
    setImageDescriptor(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.Texts));
  }

  @Override
  public String getPageId() {
    return IScoutPageConstants.NLS_TEXTS_NODE_PAGE;
  }

  /**
   * shared bundle
   */
  @Override
  public IScoutBundle getScoutResource() {
    return (IScoutBundle) super.getScoutResource();
  }

  @Override
  public boolean isChildrenLoaded() {
    return true;
  }

  @Override
  protected void loadChildrenImpl() {
    // void
  }

}
