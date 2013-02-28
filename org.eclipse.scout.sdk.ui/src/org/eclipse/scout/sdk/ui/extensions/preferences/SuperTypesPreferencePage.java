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
package org.eclipse.scout.sdk.ui.extensions.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.ui.extensions.preferences.IScoutProjectScrolledContent.IModelLoadProgressObserver;
import org.eclipse.scout.sdk.workspace.IScoutBundle;

/**
 * <h3>{@link SuperTypesPreferencePage}</h3> ...
 * 
 * @author mvi
 * @since 3.8.0 22.11.2012
 */
public class SuperTypesPreferencePage extends AbstractScoutProjectPreferencePage<SuperTypePreferenceScrolledContent, DefaultSuperClassModel> {

  public SuperTypesPreferencePage() {
    super(Texts.get("ScoutSDKSuperTypePreferences"), SuperTypePreferenceScrolledContent.class,
        IScoutBundle.TYPE_CLIENT, IScoutBundle.TYPE_SERVER, IScoutBundle.TYPE_SHARED);
  }

  @Override
  protected void loadAllModels(IModelLoadProgressObserver<DefaultSuperClassModel> observer) {
    for (Entry<IScoutBundle, SuperTypePreferenceScrolledContent> e : getProjectModelMap().entrySet()) {
      List<DefaultSuperClassModel> list = new ArrayList<DefaultSuperClassModel>();

      Set<Entry<String, String>> configuredClasses = RuntimeClasses.getAllDefaults(e.getKey()).entrySet();

      for (Entry<String, String> entry : configuredClasses) {
        list.add(new DefaultSuperClassModel(entry.getKey(), entry.getValue(), e.getKey()));
      }
      Collections.sort(list);

      e.getValue().loadModel(list, observer);
    }
  }

  @Override
  protected int getTotalWork() {
    if (getProjectModelMap().size() > 0) {
      // collect one bundle for each type
      HashMap<String, IScoutBundle> a = new HashMap<String, IScoutBundle>();
      for (IScoutBundle b : getProjectModelMap().keySet()) {
        a.put(b.getType(), b);
      }

      int work = 0;
      for (IScoutBundle b : a.values()) {
        work += RuntimeClasses.getAllDefaults(b).size();
      }
      return work;
    }
    return 0;
  }
}
