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
package org.eclipse.scout.sdk.ui.wizard.form.fields.buttonfield;

import org.eclipse.jdt.core.IType;
import org.eclipse.scout.nls.sdk.model.INlsEntry;
import org.eclipse.scout.nls.sdk.model.workspace.project.INlsProject;
import org.eclipse.scout.sdk.RuntimeClasses;
import org.eclipse.scout.sdk.ScoutSdkCore;
import org.eclipse.scout.sdk.Texts;

public class OkButtonNewWizard extends ButtonFieldNewWizard {

  public OkButtonNewWizard() {
    setWindowTitle(Texts.get("NewOkButton"));
  }

  @Override
  public void initWizard(IType declaringType) {
    super.initWizard(declaringType);
    getButtonFieldWizardPage().setSuperType(RuntimeClasses.getSuperType(RuntimeClasses.AbstractOkButton, declaringType.getJavaProject()));
    INlsProject nlsProject = ScoutSdkCore.getScoutWorkspace().getScoutBundle(declaringType.getJavaProject().getProject()).findBestMatchNlsProject();
    INlsEntry entry = null;
    if (nlsProject != null) {
      entry = nlsProject.getEntry("Ok");
      getButtonFieldWizardPage().setTypeName("OkButton");
    }
    getButtonFieldWizardPage().setNlsName(entry);
  }

}
