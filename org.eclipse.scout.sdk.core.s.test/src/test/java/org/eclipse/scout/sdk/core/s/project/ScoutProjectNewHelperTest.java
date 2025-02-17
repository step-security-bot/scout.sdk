/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.s.project;

import static org.eclipse.scout.sdk.core.s.project.ScoutProjectNewHelper.getSupportedArchetypeVersions;
import static org.eclipse.scout.sdk.core.s.project.ScoutProjectNewHelper.getSupportedJavaVersions;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.eclipse.scout.sdk.core.java.apidef.Api;
import org.eclipse.scout.sdk.core.java.apidef.ApiVersion;
import org.eclipse.scout.sdk.core.s.java.apidef.IScoutApi;
import org.eclipse.scout.sdk.core.s.util.maven.IMavenConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ScoutProjectNewHelperTest {

  @Test
  public void testGetSupportedJavaVersions() {
    var latest = Api.create(IScoutApi.class, ApiVersion.LATEST).supportedJavaVersions();
    assertArrayEquals(latest, getSupportedJavaVersions(null));
    assertArrayEquals(latest, getSupportedJavaVersions(IMavenConstants.LATEST));
    assertArrayEquals(latest, getSupportedJavaVersions("latest"));
    assertArrayEquals(latest, getSupportedJavaVersions(""));
    assertArrayEquals(latest, getSupportedJavaVersions("--invalid--version--"));
    assertArrayEquals(new int[]{8, 11}, getSupportedJavaVersions("10"));
    assertArrayEquals(new int[]{8, 11}, getSupportedJavaVersions("11.4.5"));
    assertArrayEquals(new int[]{11}, getSupportedJavaVersions("22.0.2"));
    assertArrayEquals(new int[]{11}, getSupportedJavaVersions("22.0.10"));
    assertArrayEquals(new int[]{11, 17}, getSupportedJavaVersions("22.0.11"));
    assertArrayEquals(new int[]{11, 17}, getSupportedJavaVersions("22"));
  }

  @Test
  @Tag("IntegrationTest")
  public void testGetSupportedArchetypeVersions() throws IOException {
    var javaRelease = getSupportedArchetypeVersions(true, false);
    var javaPreview = getSupportedArchetypeVersions(true, true);
    var javaScriptRelease = getSupportedArchetypeVersions(false, false);
    var javaScriptPreview = getSupportedArchetypeVersions(false, true);
    assertFalse(javaRelease.isEmpty());
    assertFalse(javaScriptPreview.isEmpty());
    assertTrue(javaRelease.size() <= javaPreview.size());
    assertTrue(javaScriptRelease.size() <= javaScriptPreview.size());
    assertTrue(javaRelease.stream().anyMatch(v -> v.startsWith("10.")));
    assertTrue(javaRelease.stream().anyMatch(v -> v.startsWith("11.")));
    assertTrue(javaRelease.stream().anyMatch(v -> v.startsWith("22.")));
  }
}
