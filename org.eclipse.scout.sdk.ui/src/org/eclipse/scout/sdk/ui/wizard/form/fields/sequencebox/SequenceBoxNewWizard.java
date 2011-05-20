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
package org.eclipse.scout.sdk.ui.wizard.form.fields.sequencebox;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.scout.sdk.ScoutSdk;
import org.eclipse.scout.sdk.operation.form.field.SequenceBoxNewOperation;
import org.eclipse.scout.sdk.typecache.IScoutWorkingCopyManager;
import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.extensions.AbstractFormFieldWizard;
import org.eclipse.scout.sdk.ui.fields.proposal.SiblingProposal;
import org.eclipse.scout.sdk.workspace.type.IStructuredType;
import org.eclipse.scout.sdk.workspace.type.IStructuredType.CATEGORIES;
import org.eclipse.scout.sdk.workspace.type.SdkTypeUtility;
import org.eclipse.scout.sdk.workspace.type.TypeUtility;

public class SequenceBoxNewWizard extends AbstractFormFieldWizard {

  private SequenceBoxNewWizardPage m_page1;
  private SequenceBoxTemplateWizardPage m_templatePage;
  private SequenceBoxNewOperation m_operation;

  public SequenceBoxNewWizard() {
    setWindowTitle("New Sequence Box");
  }

  @Override
  public void initWizard(IType declaringType) {
    super.initWizard(declaringType);
    m_page1 = new SequenceBoxNewWizardPage(getDeclaringType());
    addPage(m_page1);
    m_templatePage = new SequenceBoxTemplateWizardPage(getDeclaringType());
    addPage(m_templatePage);
  }

  @Override
  public void setSuperType(IType superType) {
    m_page1.setSuperType(superType);
  }

  @Override
  protected boolean beforeFinish() throws CoreException {
    m_operation = new SequenceBoxNewOperation(getDeclaringType(), true);
    // write back members
    if (m_page1.getNlsName() != null) {
      m_operation.setNlsEntry(m_page1.getNlsName().getNlsEntry());
    }
    m_operation.setTypeName(m_page1.getTypeName());
    if (m_page1.getSuperType() != null) {
      m_operation.setSuperTypeSignature(Signature.createTypeSignature(m_page1.getSuperType().getFullyQualifiedName(), true));
    }
    if (m_page1.getSibling() == SiblingProposal.SIBLING_END) {
      IStructuredType structuredType = SdkTypeUtility.createStructuredCompositeField(getDeclaringType());
      m_operation.setSibling(structuredType.getSibling(CATEGORIES.TYPE_FORM_FIELD));
    }
    else {
      m_operation.setSibling(m_page1.getSibling().getScoutType());
    }

    m_operation.setContentTemplate(m_templatePage.getSelectedTemplate());
    try {
      m_operation.validate();
    }
    catch (IllegalArgumentException e) {
      ScoutSdk.logWarning(e.getMessage(), e);
      return false;
    }
    return true;
  }

  @Override
  protected boolean performFinish(IProgressMonitor monitor, IScoutWorkingCopyManager workingCopyManager) {
    try {
      m_operation.run(monitor, workingCopyManager);
    }
    catch (Exception e) {
      ScoutSdk.logError("could not create sequence box.", e);
      return false;
    }
    return true;
  }

  @Override
  protected void postFinishDisplayThread() {
    IType createdField = m_operation.getCreatedField();
    if (TypeUtility.exists(createdField)) {
      ScoutSdkUi.showJavaElementInEditor(createdField, false);
    }
  }

}
