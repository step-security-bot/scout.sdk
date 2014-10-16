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
package org.eclipse.scout.sdk.ws.jaxws.swt.view.pages;

import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.ui.action.IScoutHandler;
import org.eclipse.scout.sdk.ui.view.outline.pages.AbstractPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;
import org.eclipse.scout.sdk.util.type.TypeComparators;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ICachedTypeHierarchy;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchyChangedListener;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeFilters;
import org.eclipse.scout.sdk.ws.jaxws.JaxWsIcons;
import org.eclipse.scout.sdk.ws.jaxws.JaxWsRuntimeClasses;
import org.eclipse.scout.sdk.ws.jaxws.JaxWsSdk;
import org.eclipse.scout.sdk.ws.jaxws.Texts;
import org.eclipse.scout.sdk.ws.jaxws.resource.IResourceListener;
import org.eclipse.scout.sdk.ws.jaxws.resource.ResourceFactory;
import org.eclipse.scout.sdk.ws.jaxws.resource.XmlResource;
import org.eclipse.scout.sdk.ws.jaxws.swt.action.ConsumerNewWizardAction;
import org.eclipse.scout.sdk.ws.jaxws.util.JaxWsSdkUtility;

public class WebServiceConsumerTablePage extends AbstractPage {

  private IScoutBundle m_bundle; // necessary to be hold as in method unloadPage, a reference to the bundle is required

  private ICachedTypeHierarchy m_hierarchy;
  private ITypeHierarchyChangedListener m_hierarchyChangedListener;
  private IResourceListener m_resourceListener;

  public WebServiceConsumerTablePage(IPage parent) {
    setParent(parent);
    setName(Texts.get("Services"));
    setImageDescriptor(JaxWsSdk.getImageDescriptor(JaxWsIcons.WebserviceConsumerFolder));

    m_bundle = getScoutBundle();

    m_hierarchyChangedListener = new P_TypeHierarchyChangedListener();
    m_hierarchy = TypeUtility.getPrimaryTypeHierarchy(TypeUtility.getType(JaxWsRuntimeClasses.AbstractWebServiceClient));
    m_hierarchy.addHierarchyListener(m_hierarchyChangedListener);

    // listener on build-jaxws.xml is necessary to reflect created consumers. That is because type listener is not notified about created types (bug).
    m_resourceListener = new P_BuildJaxWsResourceListener();
    getBuildJaxWsResource().addResourceListener(IResourceListener.EVENT_BUILDJAXWS_ENTRY_ADDED, m_resourceListener);
    getBuildJaxWsResource().addResourceListener(IResourceListener.ELEMENT_FILE, m_resourceListener);
  }

  @Override
  public String getPageId() {
    return IJaxWsPageConstants.WEBSERVICE_CONSUMER_TABLE_PAGE;
  }

  @Override
  public void unloadPage() {
    if (m_hierarchy != null && m_hierarchyChangedListener != null) {
      m_hierarchy.removeHierarchyListener(m_hierarchyChangedListener);
    }
    getBuildJaxWsResource().removeResourceListener(m_resourceListener);
    super.unloadPage();
  }

  @Override
  public boolean isFolder() {
    return true;
  }

  @Override
  public void refresh(boolean clearCache) {
    if (clearCache) {
      m_hierarchy.invalidate();
    }
    super.refresh(clearCache);
  }

  @Override
  public Set<Class<? extends IScoutHandler>> getSupportedMenuActions() {
    return newSet(ConsumerNewWizardAction.class);
  }

  @Override
  protected void loadChildrenImpl() {
    Set<IType> wsConsumerTypes = m_hierarchy.getAllSubtypes(TypeUtility.getType(JaxWsRuntimeClasses.AbstractWebServiceClient), ScoutTypeFilters.getClassesInScoutBundles(getScoutBundle()), TypeComparators.getTypeNameComparator());
    for (IType consumerType : wsConsumerTypes) {
      new WebServiceConsumerNodePage(this, consumerType);
    }
  }

  public XmlResource getBuildJaxWsResource() {
    return ResourceFactory.getBuildJaxWsResource(m_bundle);
  }

  private class P_TypeHierarchyChangedListener implements ITypeHierarchyChangedListener {
    @Override
    public void hierarchyInvalidated() {
      markStructureDirty();
    }
  }

  private class P_BuildJaxWsResourceListener implements IResourceListener {

    @Override
    public void changed(String element, int event) {
      JaxWsSdkUtility.markStructureDirtyAndFixSelection(WebServiceConsumerTablePage.this);
    }
  }
}
