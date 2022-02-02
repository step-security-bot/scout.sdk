/*
 * Copyright (c) 2010-2022 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.core.s.util.maven;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.scout.sdk.core.ISourceFolders;
import org.eclipse.scout.sdk.core.model.api.IJavaEnvironment;
import org.eclipse.scout.sdk.core.model.api.internal.JavaEnvironmentImplementor;
import org.eclipse.scout.sdk.core.model.ecj.FileSystemWithOverride;
import org.eclipse.scout.sdk.core.model.ecj.JavaEnvironmentFactories.RunningJavaEnvironmentFactory;
import org.eclipse.scout.sdk.core.model.ecj.JavaEnvironmentWithEcj;
import org.eclipse.scout.sdk.core.s.apidef.ScoutApi;
import org.eclipse.scout.sdk.core.s.project.ScoutProjectNewHelper;
import org.eclipse.scout.sdk.core.s.testing.ScoutFixtureHelper.ScoutSharedJavaEnvironmentFactory;
import org.eclipse.scout.sdk.core.testing.context.ExtendWithJavaEnvironmentFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@ExtendWithJavaEnvironmentFactory(ScoutSharedJavaEnvironmentFactory.class)
public class MavenModuleVersionTest {

  @Test
  public void testGetFromJar(IJavaEnvironment env) {
    assertFalse(MavenModuleVersion.usedIn(null, null).isPresent());
    assertNotNull(MavenModuleVersion.usedIn(ScoutApi.SCOUT_RT_PLATFORM_NAME, env).orElseThrow());
  }

  @Test
  public void testAllOnCentral() throws IOException {
    assertTrue(MavenModuleVersion.allOnCentral(ScoutProjectNewHelper.SCOUT_ARCHETYPES_GROUP_ID, ScoutProjectNewHelper.SCOUT_ARCHETYPES_HELLOWORLD_ARTIFACT_ID).findAny().isPresent());
    assertFalse(MavenModuleVersion.allOnCentral(ScoutProjectNewHelper.SCOUT_ARCHETYPES_GROUP_ID, "not-existing").findAny().isPresent());
  }

  @Test
  public void testGetFromSourceFolder() {
    var modulePath = Paths.get("").toAbsolutePath();
    var version = new RunningJavaEnvironmentFactory().get()
        .excludeIfContains("scout")
        .withSourceFolder(modulePath.resolve(ISourceFolders.MAIN_JAVA_SOURCE_FOLDER).toString())
        .withSourcesIncluded(false)
        .call(e -> MavenModuleVersion.usedIn(modulePath.getFileName().toString(), e));
    assertNotNull(version.orElseThrow());
  }

  @Test
  public void testNameEnvironmentAndCompilerNotCreated() {
    var spy = Mockito.spy(new JavaEnvForSpy());
    IJavaEnvironment env = new JavaEnvironmentImplementor(spy);
    assertFalse(MavenModuleVersion.usedIn(ScoutApi.SCOUT_RT_PLATFORM_NAME, env).isPresent());
    verify(spy, never()).getNameEnvironment();
  }

  protected static class JavaEnvForSpy extends JavaEnvironmentWithEcj {

    protected JavaEnvForSpy() {
      super(null /* running jre */, null /* only JRE */, null /* use default */);
    }

    @Override
    public FileSystemWithOverride getNameEnvironment() {
      // make method public
      return super.getNameEnvironment();
    }
  }
}
