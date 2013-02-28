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
package org.eclipse.scout.sdk.operation.outline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.internal.ScoutSdk;
import org.eclipse.scout.sdk.operation.util.JavaElementDeleteOperation;
import org.eclipse.scout.sdk.operation.util.SourceFormatOperation;
import org.eclipse.scout.sdk.util.ScoutUtility;
import org.eclipse.scout.sdk.util.SdkProperties;
import org.eclipse.scout.sdk.util.resources.ResourceUtility;
import org.eclipse.scout.sdk.util.type.ITypeFilter;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.IPrimaryTypeTypeHierarchy;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchy;
import org.eclipse.scout.sdk.util.typecache.IWorkingCopyManager;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeComparators;
import org.eclipse.text.edits.ReplaceEdit;

/**
 * <h3>{@link OutlineDeleteOperation}</h3> ...
 * 
 * @author mvi
 * @since 3.8.0 09.03.2012
 */
public class OutlineDeleteOperation extends JavaElementDeleteOperation {

  private final IType[] m_desktops;
  private final IType m_outline;

  public OutlineDeleteOperation(IType outlineType) {
    IType iDesktop = TypeUtility.getType(RuntimeClasses.IDesktop);
    IPrimaryTypeTypeHierarchy pth = TypeUtility.getPrimaryTypeHierarchy(iDesktop);
    ITypeFilter filter = TypeFilters.getMultiTypeFilter(TypeFilters.getTypesOnClasspath(outlineType.getJavaProject()), TypeFilters.getInWorkspaceFilter());
    m_desktops = pth.getAllSubtypes(iDesktop, filter);
    m_outline = outlineType;
    setMembers(new IJavaElement[]{m_outline});
  }

  @Override
  public void run(IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    super.run(monitor, workingCopyManager);

    for (IType desktop : m_desktops) {
      workingCopyManager.register(desktop.getCompilationUnit(), monitor);
      removeOutlineFromDesktop(desktop, monitor, workingCopyManager);

      /*JavaElementFormatOperation desktopFormatOp = new JavaElementFormatOperation(desktop, true);
      desktopFormatOp.validate();
      desktopFormatOp.run(monitor, workingCopyManager);
      */
      SourceFormatOperation op = new SourceFormatOperation(desktop);
      op.validate();
      op.run(monitor, workingCopyManager);
    }
  }

  private void removeOutlineFromDesktop(IType desktop, IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    removeOutlineViewButton(desktop, monitor, workingCopyManager);
    removeFromConfiguredOutlines(desktop, monitor, workingCopyManager);
  }

  private void removeOutlineViewButton(IType desktop, IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    IType iViewButton = TypeUtility.getType(RuntimeClasses.IViewButton);
    ITypeHierarchy hierarchy = TypeUtility.getLocalTypeHierarchy(desktop);
    IType[] allViewButtons = TypeUtility.getInnerTypes(desktop, TypeFilters.getSubtypeFilter(iViewButton, hierarchy), ScoutTypeComparators.getOrderAnnotationComparator());

    for (IType viewButton : allViewButtons) {
      if (TypeUtility.exists(viewButton) && viewButton.getElementName().equals(m_outline.getElementName() + SdkProperties.SUFFIX_VIEW_BUTTON)) {
        JavaElementDeleteOperation op = new JavaElementDeleteOperation();
        op.addMember(viewButton);
        op.validate();
        op.run(monitor, workingCopyManager);
      }
    }
  }

  private void removeFromConfiguredOutlines(IType desktop, IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    IMethod method = TypeUtility.getMethod(desktop, "getConfiguredOutlines");
    if (TypeUtility.exists(method)) {
      String source = method.getSource();
      String replacement = null;
      Matcher fm = Pattern.compile("([a-zA-Z0-9\\_\\-]*\\.add\\(\\s*" + m_outline.getFullyQualifiedName() + ".class\\s*\\)\\s*;)", Pattern.MULTILINE).matcher(source);
      if (fm.find()) {
        replacement = fm.group(1);
      }
      else {
        Matcher sm = Pattern.compile("([a-zA-Z0-9\\_\\-]*\\.add\\(\\s*" + m_outline.getElementName() + ".class\\s*\\)\\s*;)", Pattern.MULTILINE).matcher(source);
        if (sm.find()) {
          replacement = sm.group(1);
        }
        else {
          Matcher am = Pattern.compile("\\s*return\\s*new\\s*Class\\[\\]\\{[^\\}]*(" + m_outline.getFullyQualifiedName() + ".class\\s*[,]?)[^\\}]*\\}\\s*\\;", Pattern.MULTILINE).matcher(source);
          if (am.find()) {
            replacement = am.group(1);
          }
          else {
            Matcher asm = Pattern.compile("\\s*return\\s*new\\s*Class\\[\\]\\{[^\\}]*(" + m_outline.getElementName() + ".class\\s*[,]?)[^\\}]*\\}\\s*\\;", Pattern.MULTILINE).matcher(source);
            if (asm.find()) {
              replacement = asm.group(1);
            }
          }
        }
      }

      if (replacement != null) {
        try {
          ICompilationUnit icu = method.getDeclaringType().getCompilationUnit();
          Document doc = new Document(icu.getSource());
          String nl = ResourceUtility.getLineSeparator(doc);
          if (source.contains(replacement + nl)) {
            source = source.replace(replacement + nl, "");
          }
          else {
            source = source.replace(replacement, "");
          }
          ISourceRange range = method.getSourceRange();
          ReplaceEdit redit = new ReplaceEdit(range.getOffset(), range.getLength(), source);
          redit.apply(doc);
          icu.getBuffer().setContents(ScoutUtility.cleanLineSeparator(doc.get(), doc));
          workingCopyManager.reconcile(icu, monitor);
        }
        catch (BadLocationException e) {
          throw new CoreException(new Status(Status.ERROR, ScoutSdk.PLUGIN_ID, "could not update method: " + method.getElementName(), e));
        }
      }
    }
  }
}
