package org.eclipse.scout.sdk.rap.operations.project;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.scout.sdk.operation.project.AbstractScoutProjectNewOperation;
import org.eclipse.scout.sdk.operation.project.CreateServerPluginOperation;
import org.eclipse.scout.sdk.util.typecache.IWorkingCopyManager;

public class FillUiRapPluginOperation extends AbstractScoutProjectNewOperation {

  private IJavaProject m_serverProject;

  @Override
  public String getOperationName() {
    return "Fill UI RAP Plugin and Install Target Platform";
  }

  @Override
  public void init() {
    String serverPluginName = getProperties().getProperty(CreateServerPluginOperation.PROP_BUNDLE_SERVER_NAME, String.class);
    m_serverProject = getCreatedBundle(serverPluginName);
  }

  @Override
  public boolean isRelevant() {
    return isNodeChecked(CreateUiRapPluginOperation.BUNDLE_ID);
  }

  @Override
  public void run(IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException, IllegalArgumentException {
    if (m_serverProject != null) {
      ResourcesPlugin.getWorkspace().checkpoint(false);
      CreateAjaxServletOperation createAjaxServletOperation = new CreateAjaxServletOperation(m_serverProject);
      createAjaxServletOperation.run(monitor, workingCopyManager);
    }
  }
}
