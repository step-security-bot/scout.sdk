/*
 * Copyright (c) 2010-2021 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.core.s.dataobject;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.eclipse.scout.sdk.core.testing.SdkAssertions.assertAnnotation;
import static org.eclipse.scout.sdk.core.testing.SdkAssertions.assertHasFlags;
import static org.eclipse.scout.sdk.core.testing.SdkAssertions.assertHasSuperClass;
import static org.eclipse.scout.sdk.core.testing.SdkAssertions.assertHasSuperInterfaces;
import static org.eclipse.scout.sdk.core.testing.SdkAssertions.assertMethodExist;
import static org.eclipse.scout.sdk.core.testing.SdkAssertions.assertMethodReturnType;
import static org.eclipse.scout.sdk.core.testing.SdkAssertions.assertNoCompileErrors;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.scout.sdk.core.model.api.Flags;
import org.eclipse.scout.sdk.core.model.api.IType;
import org.eclipse.scout.sdk.core.s.environment.NullProgress;
import org.eclipse.scout.sdk.core.s.testing.ScoutFixtureHelper.ScoutSharedJavaEnvironmentFactory;
import org.eclipse.scout.sdk.core.s.testing.context.ClassIdAutoCreationExtension;
import org.eclipse.scout.sdk.core.s.testing.context.ExtendWithTestingEnvironment;
import org.eclipse.scout.sdk.core.s.testing.context.TestingEnvironment;
import org.eclipse.scout.sdk.core.s.testing.context.TestingEnvironmentExtension;
import org.eclipse.scout.sdk.core.testing.context.ExtendWithJavaEnvironmentFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import dataobject.ChildDo;
import dataobject.DoWithTypeParams;
import dataobject.SampleDo;

@ExtendWith(TestingEnvironmentExtension.class)
@ExtendWith(ClassIdAutoCreationExtension.class)
@ExtendWithTestingEnvironment(primary = @ExtendWithJavaEnvironmentFactory(ScoutSharedJavaEnvironmentFactory.class))
public class DoConvenienceMethodsUpdateOperationTest {
  @Test
  public void testDo(TestingEnvironment env) {
    var javaEnvironment = env.primaryEnvironment();
    var childDo = javaEnvironment.requireType(ChildDo.class.getName());
    var sampleDo = javaEnvironment.requireType(SampleDo.class.getName());

    new DoConvenienceMethodsUpdateOperation()
        .withDataObjects(asList(childDo, sampleDo))
        .accept(env, new NullProgress());
    javaEnvironment.reload();

    testApiOfChildDo(assertNoCompileErrors(childDo));
    testApiOfSampleDo(assertNoCompileErrors(sampleDo));
  }

  @Test
  public void testDoWithTypeParameters(TestingEnvironment env) {
    var javaEnvironment = env.primaryEnvironment();
    var doWithTypeParams = javaEnvironment.requireType(DoWithTypeParams.class.getName());

    new DoConvenienceMethodsUpdateOperation()
        .withDataObjects(singletonList(doWithTypeParams))
        .accept(env, new NullProgress());
    javaEnvironment.reload();

    testApiOfDoWithTypeParams(assertNoCompileErrors(doWithTypeParams));
  }

  /**
   * @Generated with org.eclipse.scout.sdk.core.testing.ApiTestGenerator
   */
  private static void testApiOfChildDo(IType childDo) {
    assertHasFlags(childDo, Flags.AccPublic);
    assertHasSuperClass(childDo, "dataobject.BaseDo");
    assertEquals(0, childDo.annotations().stream().count(), "annotation count");

    // fields of ChildDo
    assertEquals(0, childDo.fields().stream().count(), "field count of 'dataobject.ChildDo'");

    assertEquals(7, childDo.methods().stream().count(), "method count of 'dataobject.ChildDo'");
    var notANodeBecauseAbstract = assertMethodExist(childDo, "notANodeBecauseAbstract", new String[]{});
    assertMethodReturnType(notANodeBecauseAbstract, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.String>");
    assertEquals(1, notANodeBecauseAbstract.annotations().stream().count(), "annotation count");
    assertAnnotation(notANodeBecauseAbstract, "java.lang.Override");
    var withNotANodeBecauseAbstract = assertMethodExist(childDo, "withNotANodeBecauseAbstract", new String[]{"java.lang.String"});
    assertMethodReturnType(withNotANodeBecauseAbstract, "dataobject.ChildDo");
    assertEquals(1, withNotANodeBecauseAbstract.annotations().stream().count(), "annotation count");
    assertAnnotation(withNotANodeBecauseAbstract, "javax.annotation.Generated");
    var getNotANodeBecauseAbstract = assertMethodExist(childDo, "getNotANodeBecauseAbstract", new String[]{});
    assertMethodReturnType(getNotANodeBecauseAbstract, "java.lang.String");
    assertEquals(1, getNotANodeBecauseAbstract.annotations().stream().count(), "annotation count");
    assertAnnotation(getNotANodeBecauseAbstract, "javax.annotation.Generated");
    var withId = assertMethodExist(childDo, "withId", new String[]{"java.lang.CharSequence"});
    assertMethodReturnType(withId, "dataobject.ChildDo");
    assertEquals(1, withId.annotations().stream().count(), "annotation count");
    assertAnnotation(withId, "javax.annotation.Generated");
    var withVersions = assertMethodExist(childDo, "withVersions", new String[]{"java.util.Collection<? extends java.lang.Long>"});
    assertMethodReturnType(withVersions, "dataobject.ChildDo");
    assertEquals(1, withVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions, "javax.annotation.Generated");
    var withVersions1 = assertMethodExist(childDo, "withVersions", new String[]{"java.lang.Long[]"});
    assertMethodReturnType(withVersions1, "dataobject.ChildDo");
    assertEquals(1, withVersions1.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions1, "javax.annotation.Generated");
    var withEnabled = assertMethodExist(childDo, "withEnabled", new String[]{"java.lang.Boolean"});
    assertMethodReturnType(withEnabled, "dataobject.ChildDo");
    assertEquals(1, withEnabled.annotations().stream().count(), "annotation count");
    assertAnnotation(withEnabled, "javax.annotation.Generated");

    assertEquals(0, childDo.innerTypes().stream().count(), "inner types count of 'ChildDo'");
  }

  /**
   * @Generated with org.eclipse.scout.sdk.core.testing.ApiTestGenerator
   */
  private static void testApiOfSampleDo(IType sampleDo) {
    assertHasFlags(sampleDo, Flags.AccPublic);
    assertHasSuperClass(sampleDo, "org.eclipse.scout.rt.dataobject.DoEntity");
    assertHasSuperInterfaces(sampleDo, new String[]{"dataobject.DataObjectTestInterface"});
    assertEquals(0, sampleDo.annotations().stream().count(), "annotation count");

    // fields of SampleDo
    assertEquals(0, sampleDo.fields().stream().count(), "field count of 'dataobject.SampleDo'");

    assertEquals(8, sampleDo.methods().stream().count(), "method count of 'dataobject.SampleDo'");
    var enabled = assertMethodExist(sampleDo, "enabled", new String[]{});
    assertMethodReturnType(enabled, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.Boolean>");
    assertEquals(0, enabled.annotations().stream().count(), "annotation count");
    var versions = assertMethodExist(sampleDo, "versions", new String[]{});
    assertMethodReturnType(versions, "org.eclipse.scout.rt.dataobject.DoList<java.lang.Long>");
    assertEquals(0, versions.annotations().stream().count(), "annotation count");
    var ignored = assertMethodExist(sampleDo, "ignored", new String[]{});
    assertMethodReturnType(ignored, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.Long>");
    assertEquals(1, ignored.annotations().stream().count(), "annotation count");
    assertAnnotation(ignored, "org.eclipse.scout.rt.dataobject.IgnoreConvenienceMethodGeneration");
    var withEnabled = assertMethodExist(sampleDo, "withEnabled", new String[]{"java.lang.Boolean"});
    assertMethodReturnType(withEnabled, "dataobject.SampleDo");
    assertEquals(1, withEnabled.annotations().stream().count(), "annotation count");
    assertAnnotation(withEnabled, "javax.annotation.Generated");
    var isEnabled = assertMethodExist(sampleDo, "isEnabled", new String[]{});
    assertMethodReturnType(isEnabled, "java.lang.Boolean");
    assertEquals(2, isEnabled.annotations().stream().count(), "annotation count");
    assertAnnotation(isEnabled, "java.lang.Override");
    assertAnnotation(isEnabled, "javax.annotation.Generated");
    var withVersions = assertMethodExist(sampleDo, "withVersions", new String[]{"java.util.Collection<? extends java.lang.Long>"});
    assertMethodReturnType(withVersions, "dataobject.SampleDo");
    assertEquals(1, withVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions, "javax.annotation.Generated");
    var withVersions1 = assertMethodExist(sampleDo, "withVersions", new String[]{"java.lang.Long[]"});
    assertMethodReturnType(withVersions1, "dataobject.SampleDo");
    assertEquals(1, withVersions1.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions1, "javax.annotation.Generated");
    var getVersions = assertMethodExist(sampleDo, "getVersions", new String[]{});
    assertMethodReturnType(getVersions, "java.util.List<java.lang.Long>");
    assertEquals(2, getVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(getVersions, "java.lang.Override");
    assertAnnotation(getVersions, "javax.annotation.Generated");

    assertEquals(0, sampleDo.innerTypes().stream().count(), "inner types count of 'SampleDo'");
  }

  /**
   * @Generated with org.eclipse.scout.sdk.core.testing.ApiTestGenerator
   */
  private static void testApiOfDoWithTypeParams(IType doWithTypeParams) {
    assertHasFlags(doWithTypeParams, Flags.AccPublic);
    assertHasSuperClass(doWithTypeParams, "org.eclipse.scout.rt.dataobject.DoEntity");
    assertEquals(0, doWithTypeParams.annotations().stream().count(), "annotation count");

    // fields of DoWithTypeParams
    assertEquals(0, doWithTypeParams.fields().stream().count(), "field count of 'dataobject.DoWithTypeParams'");

    assertEquals(6, doWithTypeParams.methods().stream().count(), "method count of 'dataobject.DoWithTypeParams'");
    var versions = assertMethodExist(doWithTypeParams, "versions", new String[]{});
    assertMethodReturnType(versions, "org.eclipse.scout.rt.dataobject.DoList<java.lang.Long>");
    assertEquals(0, versions.annotations().stream().count(), "annotation count");
    var getT = assertMethodExist(doWithTypeParams, "getT", new String[]{});
    assertMethodReturnType(getT, "T");
    assertEquals(0, getT.annotations().stream().count(), "annotation count");
    var getS = assertMethodExist(doWithTypeParams, "getS", new String[]{});
    assertMethodReturnType(getS, "S");
    assertEquals(0, getS.annotations().stream().count(), "annotation count");
    var withVersions = assertMethodExist(doWithTypeParams, "withVersions", new String[]{"java.util.Collection<? extends java.lang.Long>"});
    assertMethodReturnType(withVersions, "dataobject.DoWithTypeParams<T,S>");
    assertEquals(1, withVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions, "javax.annotation.Generated");
    var withVersions1 = assertMethodExist(doWithTypeParams, "withVersions", new String[]{"java.lang.Long[]"});
    assertMethodReturnType(withVersions1, "dataobject.DoWithTypeParams<T,S>");
    assertEquals(1, withVersions1.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions1, "javax.annotation.Generated");
    var getVersions = assertMethodExist(doWithTypeParams, "getVersions", new String[]{});
    assertMethodReturnType(getVersions, "java.util.List<java.lang.Long>");
    assertEquals(1, getVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(getVersions, "javax.annotation.Generated");

    assertEquals(0, doWithTypeParams.innerTypes().stream().count(), "inner types count of 'DoWithTypeParams'");
  }
}
