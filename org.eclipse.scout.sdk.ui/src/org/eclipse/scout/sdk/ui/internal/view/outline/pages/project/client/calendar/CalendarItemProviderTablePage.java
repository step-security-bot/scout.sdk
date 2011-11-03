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
package org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client.calendar;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.Action;
import org.eclipse.scout.sdk.RuntimeClasses;
import org.eclipse.scout.sdk.ScoutSdk;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.action.WizardAction;
import org.eclipse.scout.sdk.ui.view.outline.pages.AbstractPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IScoutPageConstants;
import org.eclipse.scout.sdk.ui.view.outline.pages.InnerTypeOrderChangedPageDirtyListener;
import org.eclipse.scout.sdk.ui.view.outline.pages.InnerTypePageDirtyListener;
import org.eclipse.scout.sdk.ui.wizard.form.fields.calendarfield.itemprovider.CalendarItemProviderNewWizard;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.type.SdkTypeUtility;

/**
 * <h3>CalendarItemProducerTablePage</h3> ...
 */
public class CalendarItemProviderTablePage extends AbstractPage {

  final IType iCalendarItemProvider = ScoutSdk.getType(RuntimeClasses.ICalendarItemProvider);

  private InnerTypePageDirtyListener m_innerTypeListener;
  private InnerTypeOrderChangedPageDirtyListener m_orderChangedListener;

  private final IType m_calendarType;

  public CalendarItemProviderTablePage(IPage parent, IType calendarType) {
    super.setParent(parent);
    m_calendarType = calendarType;
    setName(Texts.get("CalendarItemProducerTablePage"));
    setImageDescriptor(ScoutSdkUi.getImageDescriptor(ScoutSdkUi.CalendarItemProviders));
  }

  @Override
  public void unloadPage() {
    if (m_innerTypeListener != null) {
      ScoutSdk.removeInnerTypeChangedListener(getCalendarType(), m_innerTypeListener);
      m_innerTypeListener = null;
    }
    if (m_orderChangedListener != null) {
      ScoutSdk.removeJavaResourceChangedListener(m_orderChangedListener);
      m_orderChangedListener = null;
    }
    super.unloadPage();
  }

  @Override
  public String getPageId() {
    return IScoutPageConstants.CALENDAR_ITEM_PROVIDER_TABLE_PAGE;
  }

  @Override
  public boolean isFolder() {
    return true;
  }

  /**
   * client bundle
   */
  @Override
  public IScoutBundle getScoutResource() {
    return (IScoutBundle) super.getScoutResource();
  }

  @Override
  public void loadChildrenImpl() {
    if (m_innerTypeListener == null) {
      m_innerTypeListener = new InnerTypePageDirtyListener(this, iCalendarItemProvider);
      ScoutSdk.addInnerTypeChangedListener(getCalendarType(), m_innerTypeListener);
    }
    if (m_orderChangedListener == null) {
      m_orderChangedListener = new InnerTypeOrderChangedPageDirtyListener(this, iCalendarItemProvider, getCalendarType());
      ScoutSdk.addJavaResourceChangedListener(m_orderChangedListener);
    }

    IType[] innerTypes = SdkTypeUtility.getCalendarItemProviders(getCalendarType());
    for (IType provider : innerTypes) {
      CalendarItemProviderNodePage childPage = new CalendarItemProviderNodePage();
      childPage.setParent(this);
      childPage.setType(provider);
    }
  }

  @Override
  public Action createNewAction() {
    return new WizardAction(Texts.get("Action_newTypeX", "Item Provider"), ScoutSdkUi.getImageDescriptor(ScoutSdkUi.CalendarItemProviderAdd),
        new CalendarItemProviderNewWizard(getCalendarType()));
  }

  public IType getCalendarType() {
    return m_calendarType;
  }
}
