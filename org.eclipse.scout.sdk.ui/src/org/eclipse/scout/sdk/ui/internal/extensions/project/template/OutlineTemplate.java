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
package org.eclipse.scout.sdk.ui.internal.extensions.project.template;

import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.operation.project.CreateClientPluginOperation;
import org.eclipse.scout.sdk.operation.project.template.OutlineTemplateOperation;
import org.eclipse.scout.sdk.ui.extensions.project.template.IProjectTemplate;
import org.eclipse.scout.sdk.ui.wizard.project.IScoutProjectWizard;

public class OutlineTemplate implements IProjectTemplate {

  public static final String ID = "org.eclipse.scout.sdk.ui.outlineTemplate";

  @Override
  public String getText() {
    return Texts.get("OutlineTreeAndTableForm");
  }

  @Override
  public String getDescription() {
    return Texts.get("OutlineTemplateDesc");
  }

  @Override
  public String getId() {
    return OutlineTemplateOperation.TEMPLATE_ID;
  }

  @Override
  public boolean isApplicable(IScoutProjectWizard wizard) {
    return wizard.getProjectWizardPage().isBundleNodesSelected(CreateClientPluginOperation.BUNDLE_ID);
  }

}
