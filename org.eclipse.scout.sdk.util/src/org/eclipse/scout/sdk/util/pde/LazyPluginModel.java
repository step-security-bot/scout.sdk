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
package org.eclipse.scout.sdk.util.pde;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.pde.core.build.IBuildModel;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDEState;
import org.eclipse.pde.internal.core.PluginModelManager;
import org.eclipse.pde.internal.core.build.WorkspaceBuildModel;
import org.eclipse.pde.internal.core.bundle.BundlePluginModel;
import org.eclipse.pde.internal.core.bundle.BundlePluginModelBase;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundleModel;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelBase;
import org.eclipse.pde.internal.core.plugin.WorkspaceExtensionsModel;
import org.eclipse.scout.sdk.util.resources.ResourceUtility;
import org.osgi.framework.Version;

/**
 * Base class for PDE model access of workspace plugins.<br>
 * This implementation uses lazy initialization of the PDE models.<br>
 * This class is thread safe.
 *
 * @author Matthias Villiger
 * @since 3.8.0
 */
@SuppressWarnings("restriction")
public final class LazyPluginModel {
  private final IProject m_project;
  private final IFile m_manifestFile;
  private final IFile m_pluginXmlFile;
  private final IFile m_buildPropertiesFile;

  // lazily instantiated models
  private volatile BundleDescription m_desc;
  private volatile IPluginModelBase m_bundlePluginModel;
  private volatile IPluginBase m_pluginBase;
  private volatile IBundle m_bundle;
  private volatile WorkspaceBundleModel m_bundleModel;
  private volatile WorkspaceBuildModel m_buildModel;
  private volatile WorkspaceExtensionsModel m_extensionsModel;

  public LazyPluginModel(IPluginModelBase modelBase) {
    if (modelBase == null) {
      throw new IllegalArgumentException("null project not allowed.");
    }
    m_project = null;
    m_manifestFile = null;
    m_pluginXmlFile = null;
    m_buildPropertiesFile = null;
    m_bundlePluginModel = modelBase;
    m_desc = modelBase.getBundleDescription();
    m_pluginBase = modelBase.getPluginBase(true);
  }

  public LazyPluginModel(IProject project) {
    if (project == null) {
      throw new IllegalArgumentException("null project not allowed.");
    }
    m_project = project;
    m_manifestFile = getProject().getFile(ICoreConstants.BUNDLE_FILENAME_DESCRIPTOR);
    IFile pluginManifest = getProject().getFile(ICoreConstants.FRAGMENT_FILENAME_DESCRIPTOR);
    if (!ResourceUtility.exists(pluginManifest)) {
      pluginManifest = getProject().getFile(ICoreConstants.PLUGIN_FILENAME_DESCRIPTOR);
    }
    m_pluginXmlFile = pluginManifest;
    m_buildPropertiesFile = getProject().getFile(ICoreConstants.BUILD_FILENAME_DESCRIPTOR);

    if (!isInteresting()) {
      throw new IllegalArgumentException("the passed project '" + project.getName() + "' is not a valid plugin.");
    }
  }

  private boolean isInteresting() {
    return getProject() != null && getProject().isOpen()
        && getManifestFile() != null && getManifestFile().exists()
        && getPluginXmlFile() != null && /* plugin.xml must not exist yet */
        getBuildPropertiesFile() != null /* build properties must not exist (e.g. in an imported binary project, see bug 415083) */;
  }

  public IPluginModelBase getBundlePluginModel() {
    loadBundlePluginExtensionModels();
    return m_bundlePluginModel;
  }

  public WorkspaceExtensionsModel getExtensionsModel() {
    loadBundlePluginExtensionModels();
    return m_extensionsModel;
  }

  private synchronized void loadBundlePluginExtensionModels() {
    boolean bpmCreated = false;
    boolean wemCreated = false;
    BundlePluginModel bpm = null;
    WorkspaceExtensionsModel wem = m_extensionsModel;

    if (m_bundlePluginModel == null) {
      bpm = new BundlePluginModel();

      bpm.setBundleDescription(getBundleDescription());
      bpm.setBundleModel(getBundleModel());
      bpm.setBuildModel(getBuildModel());
      bpm.setEnabled(true);
      bpm.setDirty(false);

      bpmCreated = true;
    }

    IFile pluginXmlFile = getPluginXmlFile();
    if (m_extensionsModel == null && pluginXmlFile != null) {
      wem = new WorkspaceExtensionsModel(pluginXmlFile);

      wem.load(getBundleDescription(), getPdeState());
      wem.setDirty(false); // the model is marked dirty after a fresh load

      wemCreated = true;
    }

    boolean useNewWem = wemCreated && wem != null && (bpmCreated || m_bundlePluginModel instanceof IBundlePluginModelBase);

    if (bpmCreated && bpm != null) {
      if (useNewWem) {
        bpm.setExtensionsModel(wem);
      }
      else {
        bpm.setExtensionsModel(m_extensionsModel);
      }
      m_bundlePluginModel = bpm;
    }

    if (useNewWem && wem != null) {
      wem.setBundleModel((IBundlePluginModelBase) m_bundlePluginModel);
      m_extensionsModel = wem;
    }
  }

  public synchronized IPluginBase getPluginBase() {
    if (m_pluginBase == null) {
      m_pluginBase = getBundlePluginModel().getPluginBase(true);
    }
    return m_pluginBase;
  }

  public synchronized IBuildModel getBuildModel() {
    if (m_buildModel == null && getBuildPropertiesFile() != null) {
      WorkspaceBuildModel tmp = new WorkspaceBuildModel(getBuildPropertiesFile());
      tmp.load();
      tmp.setDirty(false); // the model is marked dirty after a fresh load

      m_buildModel = tmp;
    }
    return m_buildModel;
  }

  public synchronized IBundle getBundle() {
    if (m_bundle == null) {
      m_bundle = getBundleModel().getBundle();
    }
    return m_bundle;
  }

  public synchronized WorkspaceBundleModel getBundleModel() {
    if (m_bundleModel == null) {
      WorkspaceBundleModel tmp = new WorkspaceBundleModel(getManifestFile());
      tmp.load();
      tmp.setDirty(false); // the model is marked dirty after a fresh load

      m_bundleModel = tmp;
    }
    return m_bundleModel;
  }

  /**
   * @see PluginModelManager#getState()
   */
  public static PDEState getPdeState() {
    return PDECore.getDefault().getModelManager().getState();
  }

  /**
   * @see State#getBundles()
   */
  public static BundleDescription[] getBundles() {
    return getPdeState().getState().getBundles();
  }

  /**
   * @see State#getBundles(String)
   */
  public static BundleDescription[] getBundles(String symbolicName) {
    return getPdeState().getState().getBundles(symbolicName);
  }

  /**
   * @see State#getBundle(String, Version)
   */
  public static BundleDescription getBundle(String symbolicName, Version version) {
    return getPdeState().getState().getBundle(symbolicName, version);
  }

  public synchronized BundleDescription getBundleDescription() {
    if (m_desc == null) {
      IPluginModelBase pluginBase = PDECore.getDefault().getModelManager().findModel(getProject());
      if (pluginBase != null) {
        m_desc = pluginBase.getBundleDescription();
      }

      if (m_desc == null) {
        throw new IllegalArgumentException("project '" + getProject().getName() + "' could not be found in the workspace.");
      }
    }
    return m_desc;
  }

  public synchronized void save() {
    boolean baseSaved = false;
    if (m_bundlePluginModel instanceof BundlePluginModelBase) {
      // if available: saves bundle model and extensions model
      BundlePluginModelBase base = (BundlePluginModelBase) m_bundlePluginModel;
      if (base.isDirty()) {
        base.save();
        baseSaved = true;
      }
    }

    if (!baseSaved) {
      if (m_bundleModel != null && m_bundleModel.isDirty()) {
        m_bundleModel.save();
      }
      if (m_extensionsModel != null && m_extensionsModel.isDirty()) {
        m_extensionsModel.save();
      }
    }

    if (m_buildModel != null && m_buildModel.isDirty()) {
      m_buildModel.save();
    }
  }

  public IProject getProject() {
    return m_project;
  }

  public IFile getManifestFile() {
    return m_manifestFile;
  }

  public IFile getPluginXmlFile() {
    return m_pluginXmlFile;
  }

  public IFile getBuildPropertiesFile() {
    return m_buildPropertiesFile;
  }
}
