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
package org.eclipse.scout.sdk.ui.action.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.ui.action.AbstractScoutHandler;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;
import org.eclipse.scout.sdk.util.pde.PluginModelHelper;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;

/**
 * <h3>{@link LibrariesBundleUnlinkAction}</h3> ...
 * 
 * @author aho
 * @since 3.8.0 12.03.2012
 */
public class LibrariesBundleUnlinkAction extends AbstractScoutHandler {

  private IScoutBundle m_bundle;
  private HashMap<IScoutBundle, List<IPluginModelBase>> m_libraries;

  public LibrariesBundleUnlinkAction() {
    super(Texts.get("UnlinkLibraryBundlePopup"), ScoutSdkUi.getImageDescriptor(ScoutSdkUi.LibrariesRemove), "Delete", true, Category.DELETE);
    m_libraries = new HashMap<IScoutBundle, List<IPluginModelBase>>();
  }

  @Override
  public Object execute(final Shell shell, IPage[] selection, ExecutionEvent event) throws ExecutionException {
    BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
      @Override
      public void run() {
        removeLibraries();
      }

    });
    return null;
  }

  protected void removeLibraries() {
    for (Entry<IScoutBundle, List<IPluginModelBase>> workUnit : m_libraries.entrySet()) {
      try {
        PluginModelHelper helper = new PluginModelHelper(workUnit.getKey().getProject());
        for (IPluginModelBase model : workUnit.getValue()) {
          helper.Manifest.removeDependency(model.getPluginBase().getId());
        }
        helper.save();
      }
      catch (CoreException ex) {
        ScoutSdkUi.logError("Could not remove library dependency of '" + workUnit.getKey().getBundleName() + "'.", ex);
      }
    }
  }

  public void addLibraryToRemove(IScoutBundle ownerBundle, IPluginModelBase libraryModel) {
    List<IPluginModelBase> libs = m_libraries.get(ownerBundle);
    if (libs == null) {
      libs = new ArrayList<IPluginModelBase>(3);
      m_libraries.put(ownerBundle, libs);
    }
    libs.add(libraryModel);
  }

}
