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
package org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.server.service.common.smtp;

import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.RuntimeClasses;
import org.eclipse.scout.sdk.ScoutSdk;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.action.AbstractScoutHandler;
import org.eclipse.scout.sdk.ui.action.create.SmtpServiceNewAction;
import org.eclipse.scout.sdk.ui.action.validation.FormDataSqlBindingValidateAction;
import org.eclipse.scout.sdk.ui.action.validation.ITypeResolver;
import org.eclipse.scout.sdk.ui.view.outline.pages.AbstractPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IScoutPageConstants;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.type.TypeComparators;
import org.eclipse.scout.sdk.workspace.type.TypeFilters;
import org.eclipse.scout.sdk.workspace.typecache.ICachedTypeHierarchy;

/**
 * <h3>SmtpServiceTablePage</h3> ...
 */
public class SmtpServiceTablePage extends AbstractPage {

  final IType iSMTPService = ScoutSdk.getType(RuntimeClasses.ISMTPService);
  ICachedTypeHierarchy m_serviceHierarchy;

  public SmtpServiceTablePage(AbstractPage parent) {
    setParent(parent);
    setName(Texts.get("SMTPServices"));
    setImageDescriptor(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.Services));
  }

  @Override
  public void unloadPage() {
    if (m_serviceHierarchy != null) {
      m_serviceHierarchy.removeHierarchyListener(getPageDirtyListener());
      m_serviceHierarchy = null;
    }
    super.unloadPage();
  }

  @Override
  public void refresh(boolean clearCache) {
    if (clearCache && m_serviceHierarchy != null) {
      m_serviceHierarchy.invalidate();
    }
    super.refresh(clearCache);
  }

  @Override
  public String getPageId() {
    return IScoutPageConstants.SMTP_SERVICE_TABLE_PAGE;
  }

  @Override
  public boolean isFolder() {
    return true;
  }

  /**
   * server bundle
   */
  @Override
  public IScoutBundle getScoutResource() {
    return (IScoutBundle) super.getScoutResource();
  }

  @Override
  public void loadChildrenImpl() {
    for (IType service : resolveServices()) {
      IType serviceInterface = null;
      IType[] interfaces = m_serviceHierarchy.getSuperInterfaces(service, TypeFilters.getElementNameFilter("I" + service.getElementName()));
      if (interfaces.length > 0) {
        serviceInterface = interfaces[0];
      }
      new SmtpServiceNodePage(this, service, serviceInterface);
    }
  }

  protected IType[] resolveServices() {
    if (m_serviceHierarchy == null) {
      m_serviceHierarchy = ScoutSdk.getPrimaryTypeHierarchy(iSMTPService);
      m_serviceHierarchy.addHierarchyListener(getPageDirtyListener());
    }
    IType[] services = m_serviceHierarchy.getAllSubtypes(iSMTPService, TypeFilters.getClassesInProject(getScoutResource().getJavaProject()), TypeComparators.getTypeNameComparator());
    return services;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends AbstractScoutHandler>[] getSupportedMenuActions() {
    return new Class[]{FormDataSqlBindingValidateAction.class, SmtpServiceNewAction.class};
  }

  @Override
  public void prepareMenuAction(AbstractScoutHandler menu) {
    if (menu instanceof FormDataSqlBindingValidateAction) {
      ((FormDataSqlBindingValidateAction) menu).setTyperesolver(new ITypeResolver() {
        @Override
        public IType[] getTypes() {
          return resolveServices();
        }
      });
    }
    else if (menu instanceof SmtpServiceNewAction) {
      ((SmtpServiceNewAction) menu).setScoutBundle(getScoutResource());
    }
  }
}
