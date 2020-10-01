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
package org.eclipse.scout.sdk.s2e.ui.internal.template.ast;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ITrackedNodePosition;
import org.eclipse.scout.sdk.core.s.apidef.IScoutInterfaceApi.IValueField;
import org.eclipse.scout.sdk.core.util.Ensure;
import org.eclipse.scout.sdk.core.util.JavaTypes;
import org.eclipse.scout.sdk.s2e.util.ast.AstUtils;

/**
 * <h3>{@link AstRadioButtonBuilder}</h3>
 *
 * @since 5.2.0
 */
@SuppressWarnings("unchecked")
public class AstRadioButtonBuilder extends AstTypeBuilder<AstRadioButtonBuilder> {

  @SuppressWarnings("PublicStaticCollectionField")
  public static final Set<String> PROPOSAL_RADIO_DATA_TYPES = new ConcurrentSkipListSet<>();

  static {
    PROPOSAL_RADIO_DATA_TYPES.add(JavaTypes.Long);
    PROPOSAL_RADIO_DATA_TYPES.add(JavaTypes.Boolean);
    PROPOSAL_RADIO_DATA_TYPES.add(BigDecimal.class.getName());
  }

  protected AstRadioButtonBuilder(AstNodeFactory owner) {
    super(owner);
  }

  @Override
  public AstRadioButtonBuilder insert() {

    // calc value type from surrounding group
    String genericFromRadioButtonGroupFqn = parseValueTypeTypeFromGroup();
    Type genericFromRadioButtonGroupType = getFactory().newTypeReference(genericFromRadioButtonGroupFqn);

    // set type arg
    ParameterizedType parameterizedType = getFactory().getAst().newParameterizedType(getSuperType());
    parameterizedType.typeArguments().add(genericFromRadioButtonGroupType);
    withSuperType(parameterizedType);

    super.insert();

    addGetConfiguredRadioValue(genericFromRadioButtonGroupFqn);

    ILinkedPositionHolder links = getFactory().getLinkedPositionHolder();
    if (links != null && isCreateLinks()) {
      ITrackedNodePosition dataTypeTracker = getFactory().getRewrite().track(genericFromRadioButtonGroupType);
      links.addLinkedPosition(dataTypeTracker, true, AstNodeFactory.RADIO_VALUE_TYPE_GROUP);

      links.addLinkedPositionProposalsHierarchy(AstNodeFactory.SUPER_TYPE_GROUP, getFactory().getScoutApi().IRadioButton().fqn());

      String[] proposalTypes = PROPOSAL_RADIO_DATA_TYPES.toArray(new String[0]);
      for (String fqn : proposalTypes) {
        ITypeBinding typeBinding = getFactory().resolveTypeBinding(fqn);
        if (typeBinding != null) {
          links.addLinkedPositionProposal(AstNodeFactory.RADIO_VALUE_TYPE_GROUP, typeBinding);
        }
      }
    }

    return this;
  }

  protected String parseValueTypeTypeFromGroup() {
    IType typeBinding = Ensure.notNull(AstUtils.getTypeBinding(getDeclaringType()));
    //noinspection resource
    org.eclipse.scout.sdk.core.model.api.IType scoutType = getFactory().getScoutElementProvider().toScoutType(typeBinding);
    IValueField iValueField = getFactory().getScoutApi().IValueField();
    return scoutType.resolveTypeParamValue(iValueField.valueTypeParamIndex(), iValueField.fqn())
        .flatMap(Stream::findFirst)
        .map(org.eclipse.scout.sdk.core.model.api.IType::name)
        .orElse(Object.class.getName());
  }

  protected void addGetConfiguredRadioValue(String valueTypeFqn) {
    AST ast = getFactory().getAst();
    Expression initValue = getFactory().newDefaultValueExpression(valueTypeFqn, false);
    ReturnStatement returnStatement = ast.newReturnStatement();
    returnStatement.setExpression(initValue);

    Block body = ast.newBlock();
    body.statements().add(returnStatement);

    Type simpleDataType = getFactory().newTypeReference(valueTypeFqn);
    getFactory().newMethod(getFactory().getScoutApi().AbstractRadioButton().getConfiguredRadioValueMethodName())
        .withModifiers(ModifierKeyword.PROTECTED_KEYWORD)
        .withOverride(true)
        .withReturnType(simpleDataType)
        .withBody(body)
        .in(get())
        .insert();

    // linked positions
    ILinkedPositionHolder links = getFactory().getLinkedPositionHolder();
    if (links != null && isCreateLinks()) {
      links.addLinkedPosition(getFactory().getRewrite().track(simpleDataType), false, AstNodeFactory.RADIO_VALUE_TYPE_GROUP);
      links.addLinkedPosition(getFactory().getRewrite().track(initValue), true, AstNodeFactory.RADIO_VALUE_GROUP);
    }
  }
}
