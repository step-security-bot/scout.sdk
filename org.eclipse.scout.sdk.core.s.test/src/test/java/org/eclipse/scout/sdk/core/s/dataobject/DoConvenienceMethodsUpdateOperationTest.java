/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.s.dataobject;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertAnnotation;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertHasFlags;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertHasSuperClass;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertHasSuperInterfaces;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertMethodExist;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertMethodReturnType;
import static org.eclipse.scout.sdk.core.java.testing.SdkJavaAssertions.assertNoCompileErrors;
import static org.eclipse.scout.sdk.core.testing.SdkAssertions.assertEqualsRefFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.scout.sdk.core.java.model.api.Flags;
import org.eclipse.scout.sdk.core.java.model.api.IType;
import org.eclipse.scout.sdk.core.java.testing.context.ExtendWithJavaEnvironmentFactory;
import org.eclipse.scout.sdk.core.s.environment.NullProgress;
import org.eclipse.scout.sdk.core.s.java.apidef.IScout22DoApi;
import org.eclipse.scout.sdk.core.s.java.apidef.IScoutApi;
import org.eclipse.scout.sdk.core.s.testing.ScoutFixtureHelper.ScoutSharedJavaEnvironmentFactory;
import org.eclipse.scout.sdk.core.s.testing.context.ClassIdAutoCreationExtension;
import org.eclipse.scout.sdk.core.s.testing.context.ExtendWithTestingEnvironment;
import org.eclipse.scout.sdk.core.s.testing.context.TestingEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import dataobject.ChildDo;
import dataobject.ChildDoWithTypeArg;
import dataobject.DoWithTypeParams;
import dataobject.SampleDo;

@ExtendWith(ClassIdAutoCreationExtension.class)
@ExtendWithTestingEnvironment(primary = @ExtendWithJavaEnvironmentFactory(ScoutSharedJavaEnvironmentFactory.class))
public class DoConvenienceMethodsUpdateOperationTest {

  private static final String REF_FILE_FOLDER = "org/eclipse/scout/sdk/core/s/dataobject/";

  @Test
  public void testDo(TestingEnvironment env) {
    var javaEnvironment = env.primaryEnvironment();
    var childDo = javaEnvironment.requireType(ChildDo.class.getName());
    var sampleDo = javaEnvironment.requireType(SampleDo.class.getName());

    new DoConvenienceMethodsUpdateOperation()
        .withLineSeparator("\n")
        .withDataObjects(asList(childDo, sampleDo))
        .accept(env, new NullProgress());
    javaEnvironment.reload();

    testApiOfChildDo(assertNoCompileErrors(childDo));

    var generatedSampleDo = assertNoCompileErrors(sampleDo);
    var scoutApi = javaEnvironment.requireApi(IScoutApi.class);
    if (scoutApi.maxLevel().major() >= 22) {
      testApiOfSampleDo_Scout22(generatedSampleDo);

      // check that the primitive boolean getter uses the nvl method
      var nvlMethodName = scoutApi.requireApi(IScout22DoApi.class).DoEntity().nvlMethodName();
      var isEnabledBodySource = generatedSampleDo.methods().withName("isEnabled").first().orElseThrow().sourceOfBody().orElseThrow().asCharSequence().toString();
      assertTrue(isEnabledBodySource.indexOf(nvlMethodName + "(getEnabled())") > 0);
    }
    else {
      testApiOfSampleDo_Scout10(generatedSampleDo);
    }

    // validate that the "withEnabled" method has a JavaDoc pointing to the original JavaDoc of the attribute
    assertEquals("/**\n* See {@link #enabled()}.\n*/", childDo.methods().withName("withEnabled").first().orElseThrow()
        .javaDoc().orElseThrow().asCharSequence().toString());
  }

  @Test
  public void testWithTypeParametersAndOverride(TestingEnvironment env) {
    var javaEnvironment = env.primaryEnvironment();
    var childDoWithTypeArg = javaEnvironment.requireType(ChildDoWithTypeArg.class.getName());

    new DoConvenienceMethodsUpdateOperation()
        .withLineSeparator("\n")
        .withDataObjects(singletonList(childDoWithTypeArg))
        .accept(env, new NullProgress());
    javaEnvironment.reload();

    // checks the following:
    // 1. The method 'public Integer getTestAttribute()' is moved down into the generated section and gets the Generated annotation
    // 2. The single-line comment before the convenience method generated marker comment is preserved
    // 3. The method 'public ChildDoWithTypeArg withId(Long id)' gets an override annotation
    // 4. The oldAnnotatedMethodThatShouldBeRemoved is removed
    assertEqualsRefFile(REF_FILE_FOLDER + "WithTypeParametersAndOverride.txt", childDoWithTypeArg.requireCompilationUnit().source().orElseThrow().asCharSequence());
  }

  @Test
  public void testDoWithTypeParameters(TestingEnvironment env) {
    var javaEnvironment = env.primaryEnvironment();
    var doWithTypeParams = javaEnvironment.requireType(DoWithTypeParams.class.getName());

    new DoConvenienceMethodsUpdateOperation()
        .withLineSeparator("\n")
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

    assertEquals(6, childDo.methods().stream().count(), "method count of 'dataobject.ChildDo'");
    var abstractNodeTestingInherit = assertMethodExist(childDo, "abstractNodeTestingInherit");
    assertMethodReturnType(abstractNodeTestingInherit, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.String>");
    assertEquals(1, abstractNodeTestingInherit.annotations().stream().count(), "annotation count");
    assertAnnotation(abstractNodeTestingInherit, "java.lang.Override");
    var withAbstractNodeTestingInherit = assertMethodExist(childDo, "withAbstractNodeTestingInherit", new String[]{"java.lang.String"});
    assertMethodReturnType(withAbstractNodeTestingInherit, "dataobject.ChildDo");
    assertEquals(1, withAbstractNodeTestingInherit.annotations().stream().count(), "annotation count");
    assertAnnotation(withAbstractNodeTestingInherit, "javax.annotation.Generated");
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
  private static void testApiOfSampleDo_Scout10(IType sampleDo) {
    assertHasFlags(sampleDo, Flags.AccPublic);
    assertHasSuperClass(sampleDo, "org.eclipse.scout.rt.dataobject.DoEntity");
    assertHasSuperInterfaces(sampleDo, new String[]{"dataobject.DataObjectTestInterface"});
    assertEquals(0, sampleDo.annotations().stream().count(), "annotation count");

    // fields of SampleDo
    assertEquals(0, sampleDo.fields().stream().count(), "field count of 'dataobject.SampleDo'");

    assertEquals(11, sampleDo.methods().stream().count(), "method count of 'dataobject.SampleDo'");
    var enabled = assertMethodExist(sampleDo, "enabled");
    assertMethodReturnType(enabled, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.Boolean>");
    assertEquals(4, enabled.annotations().stream().count(), "annotation count");
    assertAnnotation(enabled, "java.lang.Override");
    assertAnnotation(enabled, "java.lang.Deprecated");
    assertAnnotation(enabled, "org.eclipse.scout.rt.dataobject.ValueFormat");
    assertAnnotation(enabled, "org.eclipse.scout.rt.dataobject.AttributeName");
    var stringAttribute = assertMethodExist(sampleDo, "stringAttribute");
    assertMethodReturnType(stringAttribute, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.String>");
    assertEquals(0, stringAttribute.annotations().stream().count(), "annotation count");
    var versions = assertMethodExist(sampleDo, "versions");
    assertMethodReturnType(versions, "org.eclipse.scout.rt.dataobject.DoList<java.lang.Long>");
    assertEquals(0, versions.annotations().stream().count(), "annotation count");
    var ignored = assertMethodExist(sampleDo, "ignored");
    assertMethodReturnType(ignored, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.Long>");
    assertEquals(1, ignored.annotations().stream().count(), "annotation count");
    assertAnnotation(ignored, "org.eclipse.scout.rt.dataobject.IgnoreConvenienceMethodGeneration");
    var withEnabled = assertMethodExist(sampleDo, "withEnabled", new String[]{"java.lang.Boolean"});
    assertMethodReturnType(withEnabled, "dataobject.SampleDo");
    assertEquals(2, withEnabled.annotations().stream().count(), "annotation count");
    assertAnnotation(withEnabled, "java.lang.Deprecated");
    assertAnnotation(withEnabled, "javax.annotation.Generated");
    var isEnabled = assertMethodExist(sampleDo, "isEnabled");
    assertMethodReturnType(isEnabled, "java.lang.Boolean");
    assertEquals(2, isEnabled.annotations().stream().count(), "annotation count");
    assertAnnotation(isEnabled, "java.lang.Deprecated");
    assertAnnotation(isEnabled, "javax.annotation.Generated");
    var withStringAttribute = assertMethodExist(sampleDo, "withStringAttribute", new String[]{"java.lang.String"});
    assertMethodReturnType(withStringAttribute, "dataobject.SampleDo");
    assertEquals(1, withStringAttribute.annotations().stream().count(), "annotation count");
    assertAnnotation(withStringAttribute, "javax.annotation.Generated");
    var getStringAttribute = assertMethodExist(sampleDo, "getStringAttribute");
    assertMethodReturnType(getStringAttribute, "java.lang.String");
    assertEquals(2, getStringAttribute.annotations().stream().count(), "annotation count");
    assertAnnotation(getStringAttribute, "java.lang.Override");
    assertAnnotation(getStringAttribute, "javax.annotation.Generated");
    var withVersions = assertMethodExist(sampleDo, "withVersions", new String[]{"java.util.Collection<? extends java.lang.Long>"});
    assertMethodReturnType(withVersions, "dataobject.SampleDo");
    assertEquals(1, withVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions, "javax.annotation.Generated");
    var withVersions1 = assertMethodExist(sampleDo, "withVersions", new String[]{"java.lang.Long[]"});
    assertMethodReturnType(withVersions1, "dataobject.SampleDo");
    assertEquals(1, withVersions1.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions1, "javax.annotation.Generated");
    var getVersions = assertMethodExist(sampleDo, "getVersions");
    assertMethodReturnType(getVersions, "java.util.List<java.lang.Long>");
    assertEquals(2, getVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(getVersions, "java.lang.Override");
    assertAnnotation(getVersions, "javax.annotation.Generated");

    assertEquals(0, sampleDo.innerTypes().stream().count(), "inner types count of 'SampleDo'");
  }

  /**
   * @Generated with org.eclipse.scout.sdk.core.testing.ApiTestGenerator
   */
  private static void testApiOfSampleDo_Scout22(IType sampleDo) {
    assertHasFlags(sampleDo, Flags.AccPublic);
    assertHasSuperClass(sampleDo, "org.eclipse.scout.rt.dataobject.DoEntity");
    assertHasSuperInterfaces(sampleDo, new String[]{"dataobject.DataObjectTestInterface"});
    assertEquals(0, sampleDo.annotations().stream().count(), "annotation count");

    // fields of SampleDo
    assertEquals(0, sampleDo.fields().stream().count(), "field count of 'dataobject.SampleDo'");

    assertEquals(12, sampleDo.methods().stream().count(), "method count of 'dataobject.SampleDo'");
    var enabled = assertMethodExist(sampleDo, "enabled");
    assertMethodReturnType(enabled, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.Boolean>");
    assertEquals(4, enabled.annotations().stream().count(), "annotation count");
    assertAnnotation(enabled, "java.lang.Override");
    assertAnnotation(enabled, "java.lang.Deprecated");
    assertAnnotation(enabled, "org.eclipse.scout.rt.dataobject.ValueFormat");
    assertAnnotation(enabled, "org.eclipse.scout.rt.dataobject.AttributeName");
    var stringAttribute = assertMethodExist(sampleDo, "stringAttribute");
    assertMethodReturnType(stringAttribute, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.String>");
    assertEquals(0, stringAttribute.annotations().stream().count(), "annotation count");
    var versions = assertMethodExist(sampleDo, "versions");
    assertMethodReturnType(versions, "org.eclipse.scout.rt.dataobject.DoList<java.lang.Long>");
    assertEquals(0, versions.annotations().stream().count(), "annotation count");
    var ignored = assertMethodExist(sampleDo, "ignored");
    assertMethodReturnType(ignored, "org.eclipse.scout.rt.dataobject.DoValue<java.lang.Long>");
    assertEquals(1, ignored.annotations().stream().count(), "annotation count");
    assertAnnotation(ignored, "org.eclipse.scout.rt.dataobject.IgnoreConvenienceMethodGeneration");
    var withEnabled = assertMethodExist(sampleDo, "withEnabled", new String[]{"java.lang.Boolean"});
    assertMethodReturnType(withEnabled, "dataobject.SampleDo");
    assertEquals(2, withEnabled.annotations().stream().count(), "annotation count");
    assertAnnotation(withEnabled, "java.lang.Deprecated");
    assertAnnotation(withEnabled, "javax.annotation.Generated");
    var getEnabled = assertMethodExist(sampleDo, "getEnabled");
    assertMethodReturnType(getEnabled, "java.lang.Boolean");
    assertEquals(2, getEnabled.annotations().stream().count(), "annotation count");
    assertAnnotation(getEnabled, "java.lang.Deprecated");
    assertAnnotation(getEnabled, "javax.annotation.Generated");
    var isEnabled = assertMethodExist(sampleDo, "isEnabled");
    assertMethodReturnType(isEnabled, "boolean");
    assertEquals(2, isEnabled.annotations().stream().count(), "annotation count");
    assertAnnotation(isEnabled, "java.lang.Deprecated");
    assertAnnotation(isEnabled, "javax.annotation.Generated");
    var withStringAttribute = assertMethodExist(sampleDo, "withStringAttribute", new String[]{"java.lang.String"});
    assertMethodReturnType(withStringAttribute, "dataobject.SampleDo");
    assertEquals(1, withStringAttribute.annotations().stream().count(), "annotation count");
    assertAnnotation(withStringAttribute, "javax.annotation.Generated");
    var getStringAttribute = assertMethodExist(sampleDo, "getStringAttribute");
    assertMethodReturnType(getStringAttribute, "java.lang.String");
    assertEquals(2, getStringAttribute.annotations().stream().count(), "annotation count");
    assertAnnotation(getStringAttribute, "java.lang.Override");
    assertAnnotation(getStringAttribute, "javax.annotation.Generated");
    var withVersions = assertMethodExist(sampleDo, "withVersions", new String[]{"java.util.Collection<? extends java.lang.Long>"});
    assertMethodReturnType(withVersions, "dataobject.SampleDo");
    assertEquals(1, withVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions, "javax.annotation.Generated");
    var withVersions1 = assertMethodExist(sampleDo, "withVersions", new String[]{"java.lang.Long[]"});
    assertMethodReturnType(withVersions1, "dataobject.SampleDo");
    assertEquals(1, withVersions1.annotations().stream().count(), "annotation count");
    assertAnnotation(withVersions1, "javax.annotation.Generated");
    var getVersions = assertMethodExist(sampleDo, "getVersions");
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
    var versions = assertMethodExist(doWithTypeParams, "versions");
    assertMethodReturnType(versions, "org.eclipse.scout.rt.dataobject.DoList<java.lang.Long>");
    assertEquals(0, versions.annotations().stream().count(), "annotation count");
    var getT = assertMethodExist(doWithTypeParams, "getT");
    assertMethodReturnType(getT, "T");
    assertEquals(0, getT.annotations().stream().count(), "annotation count");
    var getS = assertMethodExist(doWithTypeParams, "getS");
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
    var getVersions = assertMethodExist(doWithTypeParams, "getVersions");
    assertMethodReturnType(getVersions, "java.util.List<java.lang.Long>");
    assertEquals(1, getVersions.annotations().stream().count(), "annotation count");
    assertAnnotation(getVersions, "javax.annotation.Generated");

    assertEquals(0, doWithTypeParams.innerTypes().stream().count(), "inner types count of 'DoWithTypeParams'");
  }
}
