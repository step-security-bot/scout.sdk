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

import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.scout.sdk.core.s.apidef.IScoutApi;
import org.eclipse.scout.sdk.s2e.util.ast.AstUtils;

/**
 * <h3>{@link AstColumnBuilder}</h3>
 *
 * @since 5.2.0
 */
public class AstColumnBuilder extends AstTypeBuilder<AstColumnBuilder> {

  protected AstColumnBuilder(AstNodeFactory owner) {
    super(owner);
  }

  @Override
  public AstColumnBuilder insert() {

    super.insert();

    // getConfiguredWidth
    getFactory().newGetConfiguredWidth(100)
        .in(get())
        .insert();

    // column getter
    addColumnGetter();

    ILinkedPositionHolder links = getFactory().getLinkedPositionHolder();
    if (links != null && isCreateLinks()) {
      links.addLinkedPositionProposalsHierarchy(AstNodeFactory.SUPER_TYPE_GROUP, getFactory().getScoutApi().IColumn().fqn());
    }

    return this;
  }

  protected void addColumnGetter() {
    IScoutApi scoutApi = getFactory().getScoutApi();
    MethodInvocation getColumnSet = getFactory().getAst().newMethodInvocation();
    getColumnSet.setName(getFactory().getAst().newSimpleName(scoutApi.ITable().getColumnSetMethodName()));

    if (AstUtils.isInstanceOf(getFactory().getDeclaringTypeBinding(), scoutApi.IExtension().fqn())) {
      // column in table extension
      MethodInvocation getOwner = getFactory().getAst().newMethodInvocation();
      getOwner.setName(getFactory().getAst().newSimpleName(scoutApi.IExtension().getOwnerMethodName()));
      getColumnSet.setExpression(getOwner);
    }

    SimpleName columnSimpleName = getFactory().getAst().newSimpleName(getTypeName() + getReadOnlySuffix());
    Type columnGetterReturnType = AstUtils.getInnerTypeReturnType(columnSimpleName, getDeclaringType());

    getFactory().newInnerTypeGetter()
        .withMethodNameToFindInnerType(scoutApi.ColumnSet().getColumnByClassMethodName())
        .withMethodToFindInnerTypeExpression(getColumnSet)
        .withName(getTypeName())
        .withReadOnlySuffix(getReadOnlySuffix())
        .withReturnType(columnGetterReturnType)
        .in(getDeclaringType())
        .insert();
  }
}
