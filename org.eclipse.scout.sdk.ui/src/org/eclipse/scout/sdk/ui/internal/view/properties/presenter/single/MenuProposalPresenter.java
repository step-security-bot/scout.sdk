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

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.sdk.ui.fields.proposal.ContentProposalProvider;
import org.eclipse.scout.sdk.ui.fields.proposal.ProposalTextField;
import org.eclipse.scout.sdk.ui.fields.proposal.javaelement.JavaElementLabelProvider;
import org.eclipse.scout.sdk.ui.view.properties.PropertyViewFormToolkit;
import org.eclipse.scout.sdk.ui.view.properties.presenter.single.AbstractTypeProposalPresenter;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeUtility;
import org.eclipse.swt.widgets.Composite;

/**
 * <h3>{@link MenuProposalPresenter}</h3> ...
 * 
 * @author mvi
 * @since 3.8.0 04.12.2012
 */
public class MenuProposalPresenter extends AbstractTypeProposalPresenter {

  private final static Pattern STAR_REPLACE_REGEX = Pattern.compile("\\*$");

  public MenuProposalPresenter(PropertyViewFormToolkit toolkit, Composite parent) {
    super(toolkit, parent);
  }

  @Override
  protected void createProposalFieldProviders(ProposalTextField proposalField) {
    JavaElementLabelProvider labelProvider = new JavaElementLabelProvider();
    getProposalField().setLabelProvider(labelProvider);
    getProposalField().setContentProvider(new P_ContentProvider(labelProvider));
  }

  private class P_ContentProvider extends ContentProposalProvider {

    private final ILabelProvider m_labelProvider;
    private IType[] m_proposals; // cached items

    private P_ContentProvider(ILabelProvider labelProvider) {
      m_labelProvider = labelProvider;
    }

    @Override
    public Object[] getProposals(String searchPattern, IProgressMonitor monitor) {
      ensureCache();
      if (!StringUtility.hasText(searchPattern)) {
        searchPattern = "*";
      }
      else {
        searchPattern = STAR_REPLACE_REGEX.matcher(searchPattern).replaceAll("") + "*";
      }
      char[] pattern = CharOperation.toLowerCase(searchPattern.toCharArray());
      ArrayList<IType> collector = new ArrayList<IType>();
      for (IType proposal : m_proposals) {
        if (CharOperation.match(pattern, m_labelProvider.getText(proposal).toCharArray(), false)) {
          collector.add(proposal);
        }
      }
      return collector.toArray(new Object[collector.size()]);
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      m_proposals = null; // clear cache
    }

    private synchronized void ensureCache() {
      if (m_proposals == null) {
        if (getMethod() != null) {
          m_proposals = ScoutTypeUtility.getMenus(getMethod().getType());
        }
        else {
          m_proposals = new IType[0];
        }
      }
    }
  }
}
