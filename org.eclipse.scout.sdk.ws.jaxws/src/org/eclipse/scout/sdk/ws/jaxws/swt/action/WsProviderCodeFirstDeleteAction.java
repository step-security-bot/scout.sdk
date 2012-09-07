/*******************************************************************************
 * Copyright (c) 2011 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Daniel Wiehl (BSI Business Systems Integration AG) - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.ws.jaxws.swt.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.ws.jaxws.Texts;
import org.eclipse.scout.sdk.ws.jaxws.swt.dialog.ScoutWizardDialogEx;
import org.eclipse.scout.sdk.ws.jaxws.swt.model.SunJaxWsBean;
import org.eclipse.scout.sdk.ws.jaxws.swt.wizard.WsProviderCodeFirstDeleteWizard;
import org.eclipse.swt.widgets.Shell;

public class WsProviderCodeFirstDeleteAction extends AbstractLinkAction {

  private IScoutBundle m_bundle;
  private SunJaxWsBean m_sunJaxWsBean;

  public WsProviderCodeFirstDeleteAction() {
    super("Delete...", ScoutSdkUi.getImageDescriptor(ScoutSdkUi.ToolRemove));
  }

  public void init(IScoutBundle bundle, SunJaxWsBean sunJaxWsBean) {
    setLabel(Texts.get("Action_deleteTypeX", "'" + sunJaxWsBean.getAlias() + "'"));
    m_bundle = bundle;
    m_sunJaxWsBean = sunJaxWsBean;
  }

  @Override
  public Object execute(Shell shell, IPage[] selection, ExecutionEvent event) throws ExecutionException {
    WsProviderCodeFirstDeleteWizard wizard = new WsProviderCodeFirstDeleteWizard();
    wizard.setBundle(m_bundle);
    wizard.setSunJaxWsBean(m_sunJaxWsBean);
    ScoutWizardDialogEx wizardDialog = new ScoutWizardDialogEx(wizard);
    wizardDialog.open();
    return null;
  }
}
