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
package org.eclipse.scout.sdk.ui.internal.view.outline.pages.project;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.scout.sdk.ScoutSdkCore;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.internal.workspace.ScoutWorkspace;
import org.eclipse.scout.sdk.ui.action.IScoutHandler;
import org.eclipse.scout.sdk.ui.action.create.ScoutProjectNewAction;
import org.eclipse.scout.sdk.ui.extensions.bundle.ScoutBundleUiExtension;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.internal.extensions.bundle.ScoutBundleExtensionPoint;
import org.eclipse.scout.sdk.ui.internal.view.outline.ScoutExplorerSettingsSupport;
import org.eclipse.scout.sdk.ui.view.outline.pages.AbstractPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;
import org.eclipse.scout.sdk.ui.view.outline.pages.IScoutPageConstants;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.IScoutWorkspaceListener;
import org.eclipse.scout.sdk.workspace.ScoutBundleComparators;
import org.eclipse.scout.sdk.workspace.ScoutBundleFilters;
import org.eclipse.scout.sdk.workspace.ScoutWorkspaceEvent;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

public class ProjectsTablePage extends AbstractPage {

  private final IScoutWorkspaceListener m_workspaceListener = new IScoutWorkspaceListener() {
    @Override
    public void workspaceChanged(ScoutWorkspaceEvent event) {
      switch (event.getType()) {
        case ScoutWorkspaceEvent.TYPE_BUNDLE_ADDED:
        case ScoutWorkspaceEvent.TYPE_BUNDLE_CHANGED:
        case ScoutWorkspaceEvent.TYPE_BUNDLE_REMOVED:
          markStructureDirty();
          break;
      }
    }
  }; // end IScoutWorkspaceListener

  private final IPropertyChangeListener m_explorerConfigChangeListener = new IPropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (ScoutExplorerSettingsSupport.PREF_BUNDLE_DISPLAY_STYLE_KEY.equals(event.getProperty()) ||
          ScoutExplorerSettingsSupport.PREF_SHOW_FRAGMENTS_KEY.equals(event.getProperty()) ||
          ScoutExplorerSettingsSupport.PREF_SHOW_BINARY_BUNDLES_KEY.equals(event.getProperty()) ||
          ScoutExplorerSettingsSupport.PREF_HIDDEN_BUNDLES_TYPES.equals(event.getProperty())) {
        markStructureDirty();
      }
      else if (ScoutExplorerSettingsSupport.PREF_HIDDEN_WORKING_SETS.equals(event.getProperty()) ||
          ScoutExplorerSettingsSupport.PREF_WORKING_SETS_ORDER.equals(event.getProperty())) {
        if (ScoutExplorerSettingsSupport.BundlePresentation.WorkingSet.equals(ScoutExplorerSettingsSupport.get().getBundlePresentation())) {
          markStructureDirty();
        }
      }
    }
  }; // end IPropertyChangeListener

  private final IPropertyChangeListener m_workingSetConfigChangeListener = new IPropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (ScoutExplorerSettingsSupport.BundlePresentation.WorkingSet.equals(ScoutExplorerSettingsSupport.get().getBundlePresentation())) {
        markStructureDirty();
      }
    }
  }; // end IPropertyChangeListener

  public ProjectsTablePage(IPage parent) {
    setParent(parent);
    setName(Texts.get("RootNodeName"));
    ScoutSdkCore.getScoutWorkspace().addWorkspaceListener(m_workspaceListener);
    ScoutSdkUi.getDefault().getPreferenceStore().addPropertyChangeListener(m_explorerConfigChangeListener);
    PlatformUI.getWorkbench().getWorkingSetManager().addPropertyChangeListener(m_workingSetConfigChangeListener);
  }

  @Override
  public void unloadPage() {
    ScoutSdkCore.getScoutWorkspace().removeWorkspaceListener(m_workspaceListener);
    ScoutSdkUi.getDefault().getPreferenceStore().removePropertyChangeListener(m_explorerConfigChangeListener);
    PlatformUI.getWorkbench().getWorkingSetManager().removePropertyChangeListener(m_workingSetConfigChangeListener);
  }

  @Override
  public String getPageId() {
    return IScoutPageConstants.PROJECT_TABLE_PAGE;
  }

  @Override
  public boolean isInitiallyLoaded() {
    return true;
  }

  @Override
  public void refresh(boolean clearCache) {
    if (clearCache) {
      ScoutWorkspace.getInstance().rebuildGraph();
      // here the graph is rebuilt asynchronously. on completion the table page is refreshed because m_workspaceListener is fired.
    }
    else {
      super.refresh(clearCache);
    }
  }

  @Override
  public void loadChildrenImpl() {
    if (ScoutExplorerSettingsSupport.BundlePresentation.Flat.equals(ScoutExplorerSettingsSupport.get().getBundlePresentation())) {
      // flat display
      IScoutBundle[] allBundles = ScoutSdkCore.getScoutWorkspace().getBundleGraph().getBundles(ScoutExplorerSettingsBundleFilter.get(), ScoutBundleComparators.getSymbolicNameAscComparator());
      for (IScoutBundle b : allBundles) {
        createBundlePage(this, b);
      }
    }
    else if (ScoutExplorerSettingsSupport.BundlePresentation.Hierarchical.equals(ScoutExplorerSettingsSupport.get().getBundlePresentation())) {
      // hierarchical display
      for (IScoutBundle root : ScoutSdkCore.getScoutWorkspace().getBundleGraph().getBundles(
          ScoutBundleFilters.getFilteredRootBundlesFilter(ScoutExplorerSettingsBundleFilter.get()), ScoutBundleComparators.getSymbolicNameAscComparator())) {
        createBundlePage(this, root);
      }
    }
    else if (ScoutExplorerSettingsSupport.BundlePresentation.WorkingSet.equals(ScoutExplorerSettingsSupport.get().getBundlePresentation())) {
      // show working sets
      for (IWorkingSet ws : ScoutExplorerSettingsSupport.get().getScoutWorkingSets(false)) {
        new ScoutWorkingSetTablePage(this, ws);
      }
    }
    else {
      // grouped display
      ScoutBundleTreeModel uiModel = new ScoutBundleTreeModel();
      uiModel.build();
      for (ScoutBundleNodeGroup g : uiModel.getRoots()) {
        new BundleNodeGroupTablePage(this, g);
      }
    }
  }

  public static void createBundlePage(IPage parentPage, IScoutBundle b) {
    if (b != null) {
      ScoutBundleUiExtension childExt = ScoutBundleExtensionPoint.getExtension(b.getType());
      if (childExt != null) {
        ScoutBundleNode rootNode = new ScoutBundleNode(b, childExt);
        rootNode.createBundlePage(parentPage);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends IScoutHandler>[] getSupportedMenuActions() {
    return new Class[]{ScoutProjectNewAction.class};
  }

  @Override
  public boolean isFolder() {
    return true;
  }
}
