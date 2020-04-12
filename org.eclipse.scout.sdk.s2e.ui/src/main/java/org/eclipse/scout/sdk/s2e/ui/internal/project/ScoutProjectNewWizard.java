/*
 * Copyright (c) 2010-2020 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.s2e.ui.internal.project;

import static org.eclipse.scout.sdk.s2e.environment.EclipseEnvironment.runInEclipseEnvironment;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.internal.lifecyclemapping.discovery.ILifecycleMappingRequirement;
import org.eclipse.m2e.core.internal.lifecyclemapping.discovery.IMavenDiscoveryProposal;
import org.eclipse.m2e.core.internal.lifecyclemapping.discovery.LifecycleMappingDiscoveryRequest;
import org.eclipse.m2e.core.internal.lifecyclemapping.discovery.MojoExecutionMappingConfiguration.MojoExecutionMappingRequirement;
import org.eclipse.m2e.core.ui.internal.wizards.MappingDiscoveryJob;
import org.eclipse.scout.sdk.s2e.operation.project.ScoutProjectNewOperation;
import org.eclipse.scout.sdk.s2e.ui.ISdkIcons;
import org.eclipse.scout.sdk.s2e.ui.internal.S2ESdkUiActivator;
import org.eclipse.scout.sdk.s2e.ui.wizard.AbstractWizard;
import org.eclipse.scout.sdk.s2e.ui.wizard.WizardFinishTask;
import org.eclipse.scout.sdk.s2e.ui.wizard.WizardFinishTask.PageToOperationMappingInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * <h3>{@link ScoutProjectNewWizard}</h3> Wizard that creates a new Scout project
 *
 * @since 5.1.0
 */
public class ScoutProjectNewWizard extends AbstractWizard implements INewWizard {

  private ScoutProjectNewWizardPage m_page1;
  private WizardFinishTask<ScoutProjectNewOperation> m_finishTask;

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    setWindowTitle("New Scout Project");
    setHelpAvailable(true);
    setDefaultPageImageDescriptor(S2ESdkUiActivator.getImageDescriptor(ISdkIcons.ScoutProjectNewWizBanner));

    m_page1 = new ScoutProjectNewWizardPage();
    addPage(m_page1);

    m_finishTask = new WizardFinishTask<>(workbench.getDisplay());
    m_finishTask
        .withOperation(ScoutProjectNewOperation::new)
        .withMapper(this::mapPageToOperation)
        .withUiAction((op, d) -> {
          List<IProject> createdProjects = op.getCreatedProjects();
          if (createdProjects != null && !createdProjects.isEmpty()) {
            new P_MappingDiscoveryJob(createdProjects).schedule();
          }
        });
  }

  @Override
  public WizardFinishTask<ScoutProjectNewOperation> getFinishTask() {
    return m_finishTask;
  }

  @Override
  public boolean performFinish() {
    if (!allPagesCanFinish()) {
      return false;
    }

    runInEclipseEnvironment(getFinishTask(), ResourcesPlugin.getWorkspace().getRoot());

    return true;
  }

  /**
   * Fills the operation with the values from the UI. This method is called in a worker thread.
   *
   * @param input
   *          The mapping input.
   * @param op
   *          The operation to fill
   */
  protected void mapPageToOperation(PageToOperationMappingInput input, ScoutProjectNewOperation op) {
    op.setDisplayName(m_page1.getDisplayName());
    op.setGroupId(m_page1.getGroupId());
    op.setArtifactId(m_page1.getArtifactId());
    op.setUseJsClient(m_page1.isUseJsClient());
    if (m_page1.isUseWorkspaceLocation()) {
      op.setTargetDirectory(ScoutProjectNewWizardPage.getWorkspaceLocation());
    }
    else {
      op.setTargetDirectory(m_page1.getTargetDirectory());
    }

    // remember folder
    String path = null;
    if (m_page1.getTargetDirectory() != null) {
      path = m_page1.getTargetDirectory().toAbsolutePath().toString();
    }
    getDialogSettings().put(ScoutProjectNewWizardPage.SETTINGS_TARGET_DIR, path);
  }

  protected static final class P_MappingDiscoveryJob extends MappingDiscoveryJob {

    public P_MappingDiscoveryJob(Collection<IProject> projects) {
      super(projects);
    }

    @Override
    protected void discoverProposals(LifecycleMappingDiscoveryRequest discoveryRequest, IProgressMonitor monitor) throws CoreException {
      super.discoverProposals(discoveryRequest, monitor);

      // by default remove all wrong proposals so that only one proposal by execution-id remains -> default selection can choose and is correct by default.
      for (Entry<ILifecycleMappingRequirement, List<IMavenDiscoveryProposal>> entry : discoveryRequest.getAllProposals().entrySet()) {
        if (entry.getKey() instanceof MojoExecutionMappingRequirement) {
          MojoExecutionMappingRequirement req = (MojoExecutionMappingRequirement) entry.getKey();
          if ("default-compile".equals(req.getExecutionId()) || "default-testCompile".equals(req.getExecutionId())) {
            List<IMavenDiscoveryProposal> proposals = entry.getValue();
            if (proposals != null && proposals.size() > 1) {
              proposals.removeIf(proposal -> proposal == null || !proposal.toString().endsWith("Eclipse JDT Compiler"));
            }
          }
        }
      }
    }
  }
}
