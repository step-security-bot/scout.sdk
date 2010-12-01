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
package org.eclipse.scout.sdk.ui.action.delete;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.Action;
import org.eclipse.scout.sdk.jobs.OperationJob;
import org.eclipse.scout.sdk.operation.util.JavaElementDeleteOperation;
import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.dialog.MemberSelectionDialog;
import org.eclipse.scout.sdk.workspace.type.SdkTypeUtility;
import org.eclipse.scout.sdk.workspace.type.TypeUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class WizardStepDeleteAction extends Action {
  private Shell m_shell;
  private MemberSelectionDialog m_confirmDialog;
  private final IType m_wizardStep;

  public WizardStepDeleteAction(IType wizardStep, Shell shell) {
    super("Delete...");
    m_wizardStep = wizardStep;
    m_shell = shell;
    setImageDescriptor(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.WizardStepRemove));
  }

  @Override
  public void run() {
    MessageBox box = new MessageBox(m_shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
    box.setMessage("Are you sure you want to delete '" + getWizardStep().getElementName() + "'?");
    if (box.open() == SWT.OK) {
      JavaElementDeleteOperation delOp = new JavaElementDeleteOperation();
      delOp.addMember(getWizardStep());
      IMethod getter = SdkTypeUtility.getWizardStepGetterMethod(getWizardStep());
      if (TypeUtility.exists(getter)) {
        delOp.addMember(getter);
      }
      OperationJob job = new OperationJob(delOp);
      job.schedule();
    }
  }

  public IType getWizardStep() {
    return m_wizardStep;
  }

}
