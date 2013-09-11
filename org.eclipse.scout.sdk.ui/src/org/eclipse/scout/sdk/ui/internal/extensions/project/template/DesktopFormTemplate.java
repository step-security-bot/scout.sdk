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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.extensions.runtime.classes.IRuntimeClasses;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.operation.project.CreateClientPluginOperation;
import org.eclipse.scout.sdk.operation.project.template.SingleFormTemplateOperation;
import org.eclipse.scout.sdk.ui.extensions.project.template.IProjectTemplate;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.wizard.project.IScoutProjectWizard;
import org.eclipse.scout.sdk.util.type.TypeUtility;

public class DesktopFormTemplate implements IProjectTemplate {
  @Override
  public String getText() {
    return Texts.get("ApplicationWithASingleForm");
  }

  @Override
  public String getDescription() {
    return Texts.get("SingleFormTemplateDesc");
  }

  @Override
  public boolean isApplicable(IScoutProjectWizard wizard) {
    return wizard.getProjectWizardPage().isBundleNodesSelected(CreateClientPluginOperation.BUNDLE_ID);
  }

  @Override
  public String getId() {
    return SingleFormTemplateOperation.TEMPLATE_ID;
  }

  @Override
  public IStatus getStatus() {
    if (TypeUtility.existsType(RuntimeClasses.AbstractFormData) /* check if a shared type is present (don't care which type) */&&
        TypeUtility.existsType(RuntimeClasses.AbstractOkButton) /* check if a client type is present (don't care which type) */) {
      return Status.OK_STATUS;
    }
    else if (Platform.getBundle(IRuntimeClasses.ScoutClientBundleId) != null &&
        Platform.getBundle(IRuntimeClasses.ScoutSharedBundleId) != null) {
      return Status.OK_STATUS;
    }
    else {
      return new Status(IStatus.ERROR, ScoutSdkUi.PLUGIN_ID, Texts.get("TemplateNotPossibleTargetPlatform"));
    }
  }
}
