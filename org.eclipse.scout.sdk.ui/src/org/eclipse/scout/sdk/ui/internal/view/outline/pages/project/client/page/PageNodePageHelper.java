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
package org.eclipse.scout.sdk.ui.internal.view.outline.pages.project.client.page;

import java.util.ArrayList;

import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.RuntimeClasses;
import org.eclipse.scout.sdk.ui.view.outline.pages.AbstractPage;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchy;

/**
 * <h3>PageNodePageHelper</h3> ...
 */
public class PageNodePageHelper {
  private final static IType iPageWithNodes = TypeUtility.getType(RuntimeClasses.IPageWithNodes);
  private final static IType iPageWithTable = TypeUtility.getType(RuntimeClasses.IPageWithTable);

  private PageNodePageHelper() {
  }

  public static AbstractPage[] createRepresentationFor(AbstractPage parentPage, IType[] types, ITypeHierarchy pageTypeHierarchy) {
    ArrayList<AbstractPage> pages = new ArrayList<AbstractPage>();
    for (IType type : types) {
      if (TypeUtility.exists(type)) {
        if (pageTypeHierarchy.isSubtype(iPageWithNodes, type)) {
          // create page with node
          pages.add(new PageWithNodeNodePage(parentPage, type));
        }
        else if (pageTypeHierarchy.isSubtype(iPageWithTable, type)) {
          // create page with table
          pages.add(new PageWithTableNodePage(parentPage, type));
        }
        else {
          pages.add(new PageNodePage(parentPage, type));
        }
      }
    }
    return pages.toArray(new AbstractPage[pages.size()]);
  }
}
