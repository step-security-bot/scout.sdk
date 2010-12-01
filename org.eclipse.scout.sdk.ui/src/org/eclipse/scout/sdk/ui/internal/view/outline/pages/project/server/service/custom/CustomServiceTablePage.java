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
package org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.server.service.custom;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.action.WizardAction;
import org.eclipse.scout.sdk.ui.type.PackageContentChangedListener;
import org.eclipse.scout.sdk.ui.view.outline.pages.AbstractPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IScoutPageConstants;
import org.eclipse.scout.sdk.ui.wizard.services.CustomServiceNewPackageWizard;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.type.TypeUtility;

public class CustomServiceTablePage extends AbstractPage {

  private PackageContentChangedListener m_changedListener;

  public CustomServiceTablePage(IPage parent) {
    setParent(parent);
    setName("Custom Services");
    setImageDescriptor(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.Services));

  }

  @Override
  public void unloadPage() {
    if (m_changedListener != null) {
      JavaCore.removeElementChangedListener(m_changedListener);
      m_changedListener = null;
    }
  }

  @Override
  public String getPageId() {
    return IScoutPageConstants.CUSTOM_SERVICE_TABLE_PAGE;
  }

  /**
   * server bundle
   */
  @Override
  public IScoutBundle getScoutResource() {
    return (IScoutBundle) super.getScoutResource();
  }

  @Override
  public boolean isFolder() {
    return true;
  }

  @Override
  protected void loadChildrenImpl() {
    IPackageFragment servicePackage = getScoutResource().getPackageFragment(getScoutResource().getPackageName(IScoutBundle.SERVER_PACKAGE_APPENDIX_SERVICES_CUSTOM));
    if (m_changedListener == null) {
      m_changedListener = new PackageContentChangedListener(this, servicePackage);
      JavaCore.addElementChangedListener(m_changedListener);
    }
    for (IPackageFragment pFrag : TypeUtility.getSubPackages(servicePackage)) {
      String appendix = pFrag.getElementName().replaceFirst(servicePackage.getElementName() + ".", "");
      CustomServicePackageNodePage node = new CustomServicePackageNodePage(this, pFrag);
      node.setName(appendix);

    }

  }

  @Override
  public Action createNewAction() {
    CustomServiceNewPackageWizard wizard;
    try {
      wizard = new CustomServiceNewPackageWizard(getScoutResource());
      return new WizardAction(Texts.get("Action_newTypeX", "Custom Service Package"), ScoutSdkUi.getImageDescriptor(ScoutSdkUi.ToolAdd),
          wizard);
    }
    catch (JavaModelException e) {
      ScoutSdkUi.logError(e);
    }
    return null;
  }

}
