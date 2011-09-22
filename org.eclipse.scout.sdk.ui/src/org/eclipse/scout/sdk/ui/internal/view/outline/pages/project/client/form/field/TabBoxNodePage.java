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
package org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client.form.field;

import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.RuntimeClasses;
import org.eclipse.scout.sdk.ScoutIdeProperties;
import org.eclipse.scout.sdk.ScoutSdk;
import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.action.AbstractScoutHandler;
import org.eclipse.scout.sdk.ui.action.FormDataUpdateAction;
import org.eclipse.scout.sdk.ui.action.ShowJavaReferencesAction;
import org.eclipse.scout.sdk.ui.action.create.CreateTemplateAction;
import org.eclipse.scout.sdk.ui.action.create.GroupBoxNewAction;
import org.eclipse.scout.sdk.ui.action.delete.FormFieldDeleteAction;
import org.eclipse.scout.sdk.ui.action.rename.FormFieldRenameAction;
import org.eclipse.scout.sdk.ui.internal.extensions.FormFieldExtensionPoint;
import org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client.KeyStrokeTablePage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IScoutPageConstants;
import org.eclipse.scout.sdk.ui.view.outline.pages.ITypePage;
import org.eclipse.scout.sdk.ui.view.outline.pages.InnerTypePageDirtyListener;
import org.eclipse.scout.sdk.ui.view.outline.pages.project.client.ui.form.field.AbstractFormFieldNodePage;
import org.eclipse.scout.sdk.workspace.type.TypeComparators;
import org.eclipse.scout.sdk.workspace.type.TypeFilters;
import org.eclipse.scout.sdk.workspace.type.TypeUtility;
import org.eclipse.scout.sdk.workspace.typecache.ITypeHierarchy;

public class TabBoxNodePage extends AbstractFormFieldNodePage {
  IType igroupBox = ScoutSdk.getType(RuntimeClasses.IGroupBox);
  IType iFormField = ScoutSdk.getType(RuntimeClasses.IFormField);

  private InnerTypePageDirtyListener m_innerTypeListener;

  public TabBoxNodePage() {
    setImageDescriptor(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.Tabbox));

  }

  @Override
  public String getPageId() {
    return IScoutPageConstants.TAB_BOX_NODE_PAGE;
  }

  @Override
  public void unloadPage() {
    if (m_innerTypeListener != null) {
      ScoutSdk.removeInnerTypeChangedListener(getType(), m_innerTypeListener);
      m_innerTypeListener = null;
    }
    super.unloadPage();
  }

  @Override
  protected void loadChildrenImpl() {
    if (m_innerTypeListener == null) {
      m_innerTypeListener = new InnerTypePageDirtyListener(this, iFormField);
      ScoutSdk.addInnerTypeChangedListener(getType(), m_innerTypeListener);
    }
    new KeyStrokeTablePage(this, getType());
    ITypeHierarchy hierarchy = ScoutSdk.getLocalTypeHierarchy(getType());
    IType[] allGroupboxes = TypeUtility.getInnerTypes(getType(), TypeFilters.getSubtypeFilter(igroupBox, hierarchy), TypeComparators.getOrderAnnotationComparator());
    for (IType groupBox : allGroupboxes) {
      ITypePage nodePage = (ITypePage) FormFieldExtensionPoint.createNodePage(groupBox, hierarchy);
      if (nodePage != null) {
        nodePage.setParent(this);
        nodePage.setType(groupBox);
        nodePage.setImageDescriptor(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.TabboxTab));
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends AbstractScoutHandler>[] getSupportedMenuActions() {
    return new Class[]{ShowJavaReferencesAction.class, FormDataUpdateAction.class,
        CreateTemplateAction.class, FormFieldRenameAction.class, FormFieldDeleteAction.class, GroupBoxNewAction.class};
  }

  @Override
  public void prepareMenuAction(AbstractScoutHandler menu) {
    super.prepareMenuAction(menu);
    if (menu instanceof FormFieldRenameAction) {
      FormFieldRenameAction a = (FormFieldRenameAction) menu;
      a.setReadOnlySuffix(ScoutIdeProperties.SUFFIX_BOX);
    }
    else if (menu instanceof FormFieldDeleteAction) {
      menu.setImage(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.TabboxRemove));
    }
    else if (menu instanceof GroupBoxNewAction) {
      ((GroupBoxNewAction) menu).setType(getType());
    }
  }
}
