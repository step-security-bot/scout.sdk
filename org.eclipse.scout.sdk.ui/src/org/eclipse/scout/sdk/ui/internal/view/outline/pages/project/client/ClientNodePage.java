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
package org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client;

import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.operation.form.formdata.ClientBundleUpdateFormDataOperation;
import org.eclipse.scout.sdk.operation.util.wellform.WellformClientBundleOperation;
import org.eclipse.scout.sdk.ui.action.FormDataUpdateAction;
import org.eclipse.scout.sdk.ui.action.IScoutHandler;
import org.eclipse.scout.sdk.ui.action.InstallClientSessionAction;
import org.eclipse.scout.sdk.ui.action.WellformAction;
import org.eclipse.scout.sdk.ui.action.create.ScoutBundleNewAction;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.library.LibrariesTablePage;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.AbstractBundleNodeTablePage;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.ScoutBundleNode;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client.form.FormTablePage;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client.form.SearchFormTablePage;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client.page.AllPagesTablePage;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client.wizard.WizardTablePage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IScoutPageConstants;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ICachedTypeHierarchy;

public class ClientNodePage extends AbstractBundleNodeTablePage {

  private ICachedTypeHierarchy m_clientSessionHierarchy;
  private ICachedTypeHierarchy m_desktopHierarchy;
  private ICachedTypeHierarchy m_desktopExtensionHierarchy;

  private final IType iClientSession = TypeUtility.getType(RuntimeClasses.IClientSession);
  private final IType iDesktop = TypeUtility.getType(RuntimeClasses.IDesktop);
  private final IType iDesktopExtension = TypeUtility.getType(RuntimeClasses.IDesktopExtension);

  public ClientNodePage(IPage parent, ScoutBundleNode node) {
    super(parent, node);
  }

  @Override
  public String getPageId() {
    return IScoutPageConstants.CLIENT_NODE_PAGE;
  }

  @Override
  public void unloadPage() {
    if (m_desktopHierarchy != null) {
      m_desktopHierarchy.removeHierarchyListener(getPageDirtyListener());
      m_desktopHierarchy = null;
    }
    if (m_desktopExtensionHierarchy != null) {
      m_desktopExtensionHierarchy.removeHierarchyListener(getPageDirtyListener());
      m_desktopExtensionHierarchy = null;
    }
    if (m_clientSessionHierarchy != null) {
      m_clientSessionHierarchy.removeHierarchyListener(getPageDirtyListener());
      m_clientSessionHierarchy = null;
    }
  }

  @Override
  public void loadChildrenImpl() {
    super.loadChildrenImpl();
    if (m_clientSessionHierarchy == null) {
      m_clientSessionHierarchy = TypeUtility.getPrimaryTypeHierarchy(iClientSession);
      m_clientSessionHierarchy.addHierarchyListener(getPageDirtyListener());
    }
    if (m_desktopHierarchy == null) {
      m_desktopHierarchy = TypeUtility.getPrimaryTypeHierarchy(iDesktop);
      m_desktopHierarchy.addHierarchyListener(getPageDirtyListener());
    }
    if (m_desktopExtensionHierarchy == null) {
      m_desktopExtensionHierarchy = TypeUtility.getPrimaryTypeHierarchy(iDesktopExtension);
      m_desktopExtensionHierarchy.addHierarchyListener(getPageDirtyListener());
    }
    // client session
    IType[] clientSessions = m_clientSessionHierarchy.getAllSubtypes(iClientSession, TypeFilters.getClassesInProject(getScoutResource().getJavaProject()));
    if (clientSessions.length > 1) {
      ScoutSdkUi.logWarning("more than one client session found.");
    }
    for (IType clientSession : clientSessions) {
      new ClientSessionNodePage(this, clientSession);
    }
    // desktop
    IType[] desktops = m_desktopHierarchy.getAllSubtypes(iDesktop, TypeFilters.getClassesInProject(getScoutResource().getJavaProject()));
    if (desktops.length > 1) {
      ScoutSdkUi.logWarning("more than one desktop found.");
    }
    for (IType desktop : desktops) {
      new DesktopNodePage(this, desktop);
    }
    // desktop extension
    IType[] desktopExtensions = m_desktopExtensionHierarchy.getAllSubtypes(iDesktopExtension, TypeFilters.getClassesInProject(getScoutResource().getJavaProject()));
    for (IType desktopExtension : desktopExtensions) {
      new DesktopExtensionNodePage(this, desktopExtension);
    }
    //others
    new FormTablePage(this);
    new SearchFormTablePage(this);
    new WizardTablePage(this);
    try {
      new ClientLookupCallTablePage(this);
    }
    catch (Exception e) {
      ScoutSdkUi.logWarning("could not load LocalLookupCallTablePage.", e);
    }
    new ClientServiceTablePage(this);
    new OutlineTablePage(this);
    new AllPagesTablePage(this);
    new TemplateTablePage(this);
    try {
      new LibrariesTablePage(this, getScoutResource());
    }
    catch (Exception e) {
      ScoutSdkUi.logWarning("Error occured while loading '" + LibrariesTablePage.class.getSimpleName() + "' node in bundle '" + getScoutResource().getSymbolicName() + "'.", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends IScoutHandler>[] getSupportedMenuActions() {
    return new Class[]{WellformAction.class, FormDataUpdateAction.class, InstallClientSessionAction.class, ScoutBundleNewAction.class};
  }

  @Override
  public void prepareMenuAction(IScoutHandler menu) {
    if (menu instanceof WellformAction) {
      ((WellformAction) menu).setOperation(new WellformClientBundleOperation(getScoutResource()));
    }
    else if (menu instanceof FormDataUpdateAction) {
      ((FormDataUpdateAction) menu).setOperation(new ClientBundleUpdateFormDataOperation(getScoutResource()));
    }
    else if (menu instanceof InstallClientSessionAction) {
      ((InstallClientSessionAction) menu).init(m_clientSessionHierarchy, getScoutResource());
    }
    else if (menu instanceof ScoutBundleNewAction) {
      ((ScoutBundleNewAction) menu).setScoutProject(getScoutResource());
    }
  }
}
