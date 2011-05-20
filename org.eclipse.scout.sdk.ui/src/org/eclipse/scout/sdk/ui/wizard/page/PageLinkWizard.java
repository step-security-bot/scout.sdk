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
package org.eclipse.scout.sdk.ui.wizard.page;

import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.ui.fields.proposal.ScoutProposalUtility;
import org.eclipse.scout.sdk.ui.wizard.AbstractWorkspaceWizard;
import org.eclipse.scout.sdk.workspace.IScoutBundle;

public class PageLinkWizard extends AbstractWorkspaceWizard {

  private PageLinkWizardPage m_linkPageWizardPage;

  public PageLinkWizard(IScoutBundle clientBunlde) {
    setWindowTitle("Link Page");
    setLinkPageWizardPage(new PageLinkWizardPage(clientBunlde));
    addPage(getLinkPageWizardPage());
  }

  public void setLinkPageWizardPage(PageLinkWizardPage linkPageWizardPage) {
    m_linkPageWizardPage = linkPageWizardPage;
  }

  public PageLinkWizardPage getLinkPageWizardPage() {
    return m_linkPageWizardPage;
  }

  public void setHolderType(IType type) {
    getLinkPageWizardPage().setHolderType(ScoutProposalUtility.getScoutTypeProposalsFor(type)[0]);
  }

  public void setHolderEnabled(boolean enabled) {
    getLinkPageWizardPage().setHolderTypeEnabled(enabled);
  }

  public void setPageType(IType type) {
    getLinkPageWizardPage().setPageType(ScoutProposalUtility.getScoutTypeProposalsFor(type)[0]);
  }

  public void setPageEnabled(boolean enabled) {
    getLinkPageWizardPage().setPageTypeFieldEnabled(enabled);
  }

}
