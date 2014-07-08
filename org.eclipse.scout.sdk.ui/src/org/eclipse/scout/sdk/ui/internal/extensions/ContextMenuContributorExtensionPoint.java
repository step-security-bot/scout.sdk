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
package org.eclipse.scout.sdk.ui.internal.extensions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.scout.sdk.ui.action.IScoutHandler;
import org.eclipse.scout.sdk.ui.extensions.IContextMenuContributor;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.view.outline.pages.IPage;

public class ContextMenuContributorExtensionPoint {

  private static final Object LOCK = new Object();

  private static volatile List<IScoutHandler> contextMenuExtensions;
  private static volatile MenuContributionInfo[] contextMenuContributorExtensions;
  private static volatile Map<IScoutHandler.Category, ArrayList<IScoutHandler>> contextMenuByCat;

  private static class MenuContributionInfo {
    private IContextMenuContributor contributor;
    private Class<? extends IPage> pageClassFilter;

    public MenuContributionInfo(IContextMenuContributor c, Class<? extends IPage> p) {
      contributor = c;
      pageClassFilter = p;
    }
  }

  private interface IExtensionVisitor {
    boolean visit(IConfigurationElement element);
  }

  public static IContextMenuContributor[] getContextMenuContributors(IPage page) {
    ArrayList<IContextMenuContributor> ret = new ArrayList<IContextMenuContributor>();
    for (MenuContributionInfo i : getContributors()) {
      if (page == null || i.pageClassFilter == null || i.pageClassFilter.isAssignableFrom(page.getClass())) {
        ret.add(i.contributor);
      }
    }
    return ret.toArray(new IContextMenuContributor[ret.size()]);
  }

  private static MenuContributionInfo[] getContributors() {
    if (contextMenuContributorExtensions == null) {
      synchronized (LOCK) {
        if (contextMenuContributorExtensions == null) {
          final ArrayList<MenuContributionInfo> list = new ArrayList<MenuContributionInfo>();
          visitExtensions("contextMenuContributor", "contributor", new IExtensionVisitor() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean visit(IConfigurationElement element) {
              try {
                String pageClassName = element.getAttribute("page");
                IContextMenuContributor ext = (IContextMenuContributor) element.createExecutableExtension("class");
                Class<? extends IPage> clazz = null;
                if (pageClassName != null) {
                  clazz = (Class<? extends IPage>) Class.forName(pageClassName.trim());
                }

                MenuContributionInfo info = new MenuContributionInfo(ext, clazz);
                list.add(info);
              }
              catch (Exception t) {
                ScoutSdkUi.logError("create context menu contributor: " + element.getAttribute("class"), t);
              }
              return true;
            }
          });
          contextMenuContributorExtensions = list.toArray(new MenuContributionInfo[list.size()]);
        }
      }
    }
    return contextMenuContributorExtensions;
  }

  public static void visitExtensions(String extensionPointName, String elementName, IExtensionVisitor v) {
    IExtensionRegistry reg = Platform.getExtensionRegistry();
    IExtensionPoint xp = reg.getExtensionPoint(ScoutSdkUi.PLUGIN_ID, extensionPointName);
    IExtension[] extensions = xp.getExtensions();
    for (IExtension extension : extensions) {
      IConfigurationElement[] elements = extension.getConfigurationElements();
      for (IConfigurationElement element : elements) {
        if (elementName.equals(element.getName())) {
          if (!v.visit(element)) {
            return; // cancel when requested
          }
        }
      }
    }
  }

  public static IScoutHandler[] getAllRegisteredContextMenus() {
    if (contextMenuExtensions == null) {
      synchronized (LOCK) {
        if (contextMenuExtensions == null) {
          final ArrayList<IScoutHandler> list = new ArrayList<IScoutHandler>();
          visitExtensions("contextMenu", "contextMenu", new IExtensionVisitor() {
            @Override
            public boolean visit(IConfigurationElement element) {
              try {
                IScoutHandler ext = (IScoutHandler) element.createExecutableExtension("class");
                list.add(ext);
              }
              catch (Exception t) {
                ScoutSdkUi.logError("create context menu: " + element.getAttribute("class"), t);
              }
              return true;
            }
          });
          contextMenuExtensions = list;
        }
      }
    }
    return contextMenuExtensions.toArray(new IScoutHandler[contextMenuExtensions.size()]);
  }

  public static Map<IScoutHandler.Category, ArrayList<IScoutHandler>> getAllRegisteredContextMenusByCategory() {
    if (contextMenuByCat == null) {
      synchronized (LOCK) {
        if (contextMenuByCat == null) {
          TreeMap<IScoutHandler.Category, ArrayList<IScoutHandler>> sorted =
              new TreeMap<IScoutHandler.Category, ArrayList<IScoutHandler>>(new Comparator<IScoutHandler.Category>() {
                @Override
                public int compare(IScoutHandler.Category o1, IScoutHandler.Category o2) {
                  return Integer.valueOf(o1.getOrder()).compareTo(o2.getOrder());
                }
              });

          // group and sort all actions by category
          for (IScoutHandler a : getAllRegisteredContextMenus()) {
            ArrayList<IScoutHandler> listOfCurCat = sorted.get(a.getCategory());
            if (listOfCurCat == null) {
              listOfCurCat = new ArrayList<IScoutHandler>();
              sorted.put(a.getCategory(), listOfCurCat);
            }
            listOfCurCat.add(a);
          }

          contextMenuByCat = sorted;
        }
      }
    }

    return contextMenuByCat;
  }
}
