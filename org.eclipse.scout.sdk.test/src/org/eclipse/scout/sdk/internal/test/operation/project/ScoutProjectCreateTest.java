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
package org.eclipse.scout.sdk.internal.test.operation.project;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.RuntimeClasses;
import org.eclipse.scout.sdk.ScoutSdkCore;
import org.eclipse.scout.sdk.helper.ScoutProjectHelper;
import org.eclipse.scout.sdk.jobs.OperationJob;
import org.eclipse.scout.sdk.operation.project.template.OutlineTemplateOperation;
import org.eclipse.scout.sdk.operation.project.template.SingleFormTemplateOperation;
import org.eclipse.scout.sdk.test.AbstractScoutSdkTest;
import org.eclipse.scout.sdk.util.ScoutSeverityManager;
import org.eclipse.scout.sdk.util.internal.typecache.JavaResourceChangedEmitter;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.IPrimaryTypeTypeHierarchy;
import org.eclipse.scout.sdk.workspace.IScoutProject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO work in progress
 */

public class ScoutProjectCreateTest extends AbstractScoutSdkTest {

  @BeforeClass
  public static void setup() {
    setAutoUpdateFormData(false);
    ScoutSdkCore.getScoutWorkspace();
  }

  @Test
  public void testCreateBundles() throws Exception {
    try {
      ScoutSdkCore.getScoutWorkspace();
      IScoutProject project = ScoutProjectHelper.setupNewProject("org.eclipse.testapp", true, true, true, true, false);
      int severity = ScoutSeverityManager.getInstance().getSeverityOf(ResourcesPlugin.getWorkspace().getRoot());
      Assert.assertTrue(severity < IMarker.SEVERITY_ERROR);
      Assert.assertNotNull(project.getClientBundle());
      Assert.assertNotNull(project.getSharedBundle());
      Assert.assertNotNull(project.getServerBundle());
      Assert.assertNotNull(project.getUiSwtBundle());
      Assert.assertNull(project.getUiSwingBundle());
    }
    finally {
      clearWorkspace();
      Assert.assertEquals(0, ScoutSdkCore.getScoutWorkspace().getRootProjects().length);
    }
  }

  @Test
  @Ignore
  public void testLoop() throws Exception {
    // create 5 times a project with the same name and remove it again
    for (int i = 0; i < 5; i++) {
      System.out.println("-------------- start " + i + "--------------");
      testTemplateDesktopForm("org.eclipse.testapp");
      System.out.println("-------------- end " + i + "--------------");
    }
  }

  private void testTemplateDesktopForm(String projectName) throws Exception {
    try {
      IScoutProject project = ScoutProjectHelper.setupNewProject(projectName, true, true, true);
      final IType iForm = TypeUtility.getType(RuntimeClasses.IForm);
      final IPrimaryTypeTypeHierarchy formHierarchy = TypeUtility.getPrimaryTypeHierarchy(iForm);
      IType[] subtypes = formHierarchy.getAllSubtypes(iForm, TypeFilters.getInWorkspaceFilter());
      Assert.assertEquals(0, subtypes.length);
      SingleFormTemplateOperation op = new SingleFormTemplateOperation(project);
      executeAndBuildWorkspace(op);
      int severity = ScoutSeverityManager.getInstance().getSeverityOf(ResourcesPlugin.getWorkspace().getRoot(), IMarker.SEVERITY_WARNING);
      if (severity >= IMarker.SEVERITY_ERROR) {
        System.out.println();
      }
      Assert.assertTrue(severity < IMarker.SEVERITY_ERROR);
      waitForIndexesReady();
      System.out.println("iForm exists " + iForm.exists() + "  " + iForm.getJavaProject().exists());
      subtypes = formHierarchy.getAllSubtypes(iForm, TypeFilters.getInWorkspaceFilter());
      if (subtypes.length != 1) {
        System.out.println("NOT FIRED RESOURCES -------");
        for (IResource r : JavaResourceChangedEmitter.getChangedResources()) {
          System.out.println(" - '" + r.getName() + "'");
        }
        System.out.println("EEEEEEEEEEEEEEEEEEEEEERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
        Thread.sleep(10000);
      }
      Assert.assertEquals(1, subtypes.length);
    }
    finally {
      assertNoWorkingCopies();
      clearWorkspace();
      Assert.assertEquals(0, ScoutSdkCore.getScoutWorkspace().getRootProjects().length);
    }

  }

  @Test
  public void testTemplateOutlineTreeTable() throws Exception {
    try {
      IScoutProject project = ScoutProjectHelper.setupNewProject("org.eclipse.testapp1", true, true, true);
      final IType iForm = TypeUtility.getType(RuntimeClasses.IForm);
      final IPrimaryTypeTypeHierarchy formHierarchy = TypeUtility.getPrimaryTypeHierarchy(iForm);
      IType[] subtypes = formHierarchy.getAllSubtypes(iForm, TypeFilters.getInWorkspaceFilter());
      Assert.assertEquals(0, subtypes.length);
      OutlineTemplateOperation op = new OutlineTemplateOperation(project);
      OperationJob job = new OperationJob(op);
      job.schedule();
      job.join();
      buildWorkspace();
      int severity = ScoutSeverityManager.getInstance().getSeverityOf(ResourcesPlugin.getWorkspace().getRoot());
      Assert.assertTrue(severity < IMarker.SEVERITY_ERROR);
    }
    finally {
      clearWorkspace();
      Assert.assertEquals(0, ScoutSdkCore.getScoutWorkspace().getRootProjects().length);
    }
  }
}
