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
package org.eclipse.scout.sdk.ui.internal.extensions.codecompletion.sql;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.osgi.framework.Bundle;

/**
 * <h3>SqlBindFromFormDataCompletionComputer</h3>
 * 
 * @author Andreas Hoegger
 * @since 1.0.8 09.02.2010
 */
public class SqlBindFromFormDataCompletionComputer implements IJavaCompletionProposalComputer {

  private final SqlBindCompletionProposalProcessor m_processor = new SqlBindCompletionProposalProcessor();

  @Override
  public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
    if (!(context instanceof JavaContentAssistInvocationContext) || Platform.getBundle(ScoutSdkUi.PLUGIN_ID).getState() != Bundle.ACTIVE) {
      return Collections.emptyList();
    }
    JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;

    return m_processor.computeCompletionProposals(javaContext);
  }

  @Override
  public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
    return Collections.emptyList();
  }

  @Override
  public String getErrorMessage() {
    return null;
  }

  @Override
  public void sessionEnded() {
  }

  @Override
  public void sessionStarted() {
  }
}
