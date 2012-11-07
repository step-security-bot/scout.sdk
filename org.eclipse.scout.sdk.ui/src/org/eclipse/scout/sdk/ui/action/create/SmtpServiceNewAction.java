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
package org.eclipse.scout.sdk.ui.action.create;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.ui.action.AbstractWizardAction;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.wizard.services.SmtpServiceNewWizard;
import org.eclipse.scout.sdk.workspace.IScoutBundle;

/**
 *
 */
public class SmtpServiceNewAction extends AbstractWizardAction {

  private IScoutBundle m_bundle;

  public SmtpServiceNewAction() {
    super(Texts.get("Action_newTypeX", "Smtp Service"), ScoutSdkUi.getImageDescriptor(ScoutSdkUi.ServiceAdd), null, false, Category.NEW);
  }

  public void setScoutBundle(IScoutBundle bundle) {
    m_bundle = bundle;
  }

  @Override
  protected IWizard getNewWizardInstance() {
    return new SmtpServiceNewWizard(m_bundle);
  }
}
