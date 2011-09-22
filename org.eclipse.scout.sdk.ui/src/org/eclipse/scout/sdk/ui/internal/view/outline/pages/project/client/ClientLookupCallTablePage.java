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
import org.eclipse.scout.sdk.RuntimeClasses;
import org.eclipse.scout.sdk.ScoutSdk;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.operation.util.wellform.WellformLookupCallsOperation;
import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.action.AbstractScoutHandler;
import org.eclipse.scout.sdk.ui.action.WellformAction;
import org.eclipse.scout.sdk.ui.action.create.LocalLookupCallNewAction;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.shared.LookupCallNodePage;
import org.eclipse.scout.sdk.ui.view.outline.pages.AbstractPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IScoutPageConstants;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.type.TypeComparators;
import org.eclipse.scout.sdk.workspace.type.TypeFilters;
import org.eclipse.scout.sdk.workspace.typecache.ICachedTypeHierarchy;

public class ClientLookupCallTablePage extends AbstractPage {
  final IType localLookupCall = ScoutSdk.getType(RuntimeClasses.LocalLookupCall);
  private ICachedTypeHierarchy m_lookupCallHierarchy;

  public ClientLookupCallTablePage(AbstractPage parent) {
    setParent(parent);
    setName(Texts.get("LocalLookupCallTablePage"));
    setImageDescriptor(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.LookupCalls));
  }

  @Override
  public void unloadPage() {
    if (m_lookupCallHierarchy != null) {
      m_lookupCallHierarchy.removeHierarchyListener(getPageDirtyListener());
      m_lookupCallHierarchy = null;
    }
    super.unloadPage();
  }

  @Override
  public void refresh(boolean clearCache) {
    if (clearCache && m_lookupCallHierarchy != null) {
      m_lookupCallHierarchy.invalidate();
    }
    super.refresh(clearCache);
  }

  @Override
  public String getPageId() {
    return IScoutPageConstants.CLIENT_LOOKUP_CALL_TABLE_PAGE;
  }

  /**
   * client bundle
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
  public void loadChildrenImpl() {
    if (m_lookupCallHierarchy == null) {
      m_lookupCallHierarchy = ScoutSdk.getPrimaryTypeHierarchy(localLookupCall);
      m_lookupCallHierarchy.addHierarchyListener(getPageDirtyListener());
    }
    IType[] lookupCalls = m_lookupCallHierarchy.getAllClasses(TypeFilters.getClassesInProject(getScoutResource().getJavaProject()), TypeComparators.getTypeNameComparator());
    for (IType lookupcall : lookupCalls) {
      new LookupCallNodePage(this, lookupcall);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends AbstractScoutHandler>[] getSupportedMenuActions() {
    return new Class[]{WellformAction.class, LocalLookupCallNewAction.class};
  }

  @Override
  public void prepareMenuAction(AbstractScoutHandler menu) {
    if (menu instanceof WellformAction) {
      WellformAction action = (WellformAction) menu;
      action.setOperation(new WellformLookupCallsOperation(getScoutResource()));
      action.setLabel(Texts.get("WellformLookupCalls"));
    }
    else if (menu instanceof LocalLookupCallNewAction) {
      ((LocalLookupCallNewAction) menu).setScoutBundle(getScoutResource());
    }
  }
}
