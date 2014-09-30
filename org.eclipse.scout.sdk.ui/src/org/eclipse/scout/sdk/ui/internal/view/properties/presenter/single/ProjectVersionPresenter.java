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
package org.eclipse.scout.sdk.ui.internal.view.properties.presenter.single;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.view.properties.PropertyViewFormToolkit;
import org.eclipse.scout.sdk.ui.view.properties.presenter.AbstractPresenter;
import org.eclipse.scout.sdk.util.pde.PluginModelHelper;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.ScoutBundleFilters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.osgi.framework.Version;

/**
 *
 */
public class ProjectVersionPresenter extends AbstractPresenter {
  private Text m_versionField;
  private ImageHyperlink m_applyVersionLink;
  private final IScoutBundle m_project;

  public ProjectVersionPresenter(PropertyViewFormToolkit toolkit, Composite parent, IScoutBundle scoutProject) {
    super(toolkit, parent);
    m_project = scoutProject;
    createContent(getContainer());
  }

  private Map<IProject, PluginModelHelper> getModels() {
    Set<? extends IScoutBundle> scoutBundles = m_project.getChildBundles(ScoutBundleFilters.getWorkspaceBundlesFilter(), true);
    Map<IProject, PluginModelHelper> bundles = new HashMap<IProject, PluginModelHelper>(scoutBundles.size());
    for (IScoutBundle sb : scoutBundles) {
      bundles.put(sb.getProject(), new PluginModelHelper(sb.getProject()));
    }
    return bundles;
  }

  private void createContent(Composite container) {
    // setup fields
    m_versionField = getToolkit().createText(container, getCommonPluginVerionString(), SWT.BORDER);

    m_applyVersionLink = getToolkit().createImageHyperlink(container, SWT.PUSH);
    m_applyVersionLink.setImage(ScoutSdkUi.getImage(ScoutSdkUi.ToolRename));
    m_applyVersionLink.setToolTipText(Texts.get("ApplyVersion"));

    // add listeners
    m_versionField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.ESC) {
          m_versionField.setText(getCommonPluginVerionString());
        }
        else if (e.keyCode == SWT.CR) {
          updatePluginVersions();
        }
      }
    });
    m_applyVersionLink.addHyperlinkListener(new HyperlinkAdapter() {
      @Override
      public void linkActivated(HyperlinkEvent e) {
        updatePluginVersions();
      }
    });

    // define layout
    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 0;
    layout.horizontalSpacing = 3;
    layout.verticalSpacing = 3;
    layout.marginWidth = 0;
    container.setLayout(layout);

    m_versionField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_HORIZONTAL));
    m_applyVersionLink.setLayoutData(new GridData(GridData.FILL_VERTICAL));
  }

  private Version parseVersion() {
    String txt = m_versionField.getText();
    try {
      return Version.parseVersion(txt);
    }
    catch (IllegalArgumentException e) {
      return null;
    }
  }

  private void updatePluginVersions() {
    Version newVersion = parseVersion();
    if (newVersion != null) {
      String newVersionStr = newVersion.toString();
      Map<IProject, PluginModelHelper> models = getModels();
      LinkedList<String> errPlugins = new LinkedList<String>();
      for (Entry<IProject, PluginModelHelper> entry : models.entrySet()) {
        PluginModelHelper mf = entry.getValue();
        if (!newVersionStr.equals(mf.Manifest.getVersionAsString())) {
          try {
            mf.Manifest.setVersion(newVersionStr);
            mf.save();
          }
          catch (Exception e) {
            errPlugins.add(entry.getKey().getName());
            ScoutSdkUi.logError("Unable to set the new version for plugin " + entry.getKey().getName(), e);
          }
        }
      }

      if (errPlugins.size() < 1) {
        MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
        box.setMessage(Texts.get("VersionApplySuccess"));
        box.open();
      }
      else {
        MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
        StringBuilder sb = new StringBuilder(Texts.get("VersionApplyErr"));
        sb.append("\n");
        for (String pluginName : errPlugins) {
          sb.append(pluginName);
          sb.append("\n");
        }
        box.setMessage(sb.toString());
        box.open();
      }
    }
    else {
      MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
      box.setMessage(Texts.get("VersionApplyWrongFormat"));
      box.open();
    }
  }

  private String getCommonPluginVerionString() {
    String v = null;
    Map<IProject, PluginModelHelper> models = getModels();
    for (PluginModelHelper b : models.values()) {
      String bundleVersion = b.Manifest.getVersionAsString();
      if (v == null) {
        v = bundleVersion;
      }
      else {
        if (!v.equals(bundleVersion)) {
          return "";
        }
      }
    }

    if (v == null) {
      return "";
    }
    else {
      return v;
    }
  }
}
