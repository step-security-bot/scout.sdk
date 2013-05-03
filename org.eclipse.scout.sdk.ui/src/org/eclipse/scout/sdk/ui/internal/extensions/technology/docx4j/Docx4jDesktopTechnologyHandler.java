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
package org.eclipse.scout.sdk.ui.internal.extensions.technology.docx4j;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.scout.commons.TriState;
import org.eclipse.scout.nls.sdk.model.INlsEntry;
import org.eclipse.scout.nls.sdk.model.util.Language;
import org.eclipse.scout.nls.sdk.model.workspace.NlsEntry;
import org.eclipse.scout.sdk.extensions.runtime.classes.RuntimeClasses;
import org.eclipse.scout.sdk.operation.MenuNewOperation;
import org.eclipse.scout.sdk.operation.method.MethodOverrideOperation;
import org.eclipse.scout.sdk.operation.util.JavaElementDeleteOperation;
import org.eclipse.scout.sdk.operation.util.JavaElementFormatOperation;
import org.eclipse.scout.sdk.ui.extensions.technology.AbstractScoutTechnologyHandler;
import org.eclipse.scout.sdk.ui.extensions.technology.IScoutTechnologyResource;
import org.eclipse.scout.sdk.ui.extensions.technology.ScoutTechnologyResource;
import org.eclipse.scout.sdk.util.internal.sigcache.SignatureCache;
import org.eclipse.scout.sdk.util.signature.IImportValidator;
import org.eclipse.scout.sdk.util.signature.SignatureUtility;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ICachedTypeHierarchy;
import org.eclipse.scout.sdk.util.typecache.IWorkingCopyManager;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.IScoutBundleFilter;
import org.eclipse.scout.sdk.workspace.ScoutBundleFilters;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeFilters;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeUtility;

/**
 * <h3>{@link Docx4jDesktopTechnologyHandler}</h3> ...
 * 
 * @author mvi
 * @since 3.9.0 03.05.2013
 */
public class Docx4jDesktopTechnologyHandler extends AbstractScoutTechnologyHandler {

  private final static String EXCEL_EXPORT_MENU_TYPE_NAME = "ExportToExcelMenu";
  private final static String EXCEL_EXPORT_NLS_KEY = "ExportToExcelMenu";

  @Override
  public void selectionChanged(IScoutTechnologyResource[] resources, boolean selected, IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    DesktopTechnologyResource r = (DesktopTechnologyResource) resources[0];

    if (selected) {
      IType menu = r.m_toolsMenuType.getType(EXCEL_EXPORT_MENU_TYPE_NAME);
      if (!TypeUtility.exists(menu)) {
        MenuNewOperation mno = new MenuNewOperation(r.m_toolsMenuType, false);
        INlsEntry nlsEntry = r.getBundle().getNlsProject().getEntry(EXCEL_EXPORT_NLS_KEY);
        if (nlsEntry == null) {
          // create
          NlsEntry newEntry = new NlsEntry(EXCEL_EXPORT_NLS_KEY, r.getBundle().getNlsProject());
          newEntry.addTranslation(Language.LANGUAGE_DEFAULT, "Export to &Excel");
          r.getBundle().getNlsProject().updateRow(newEntry, monitor);
        }
        mno.setNlsEntry(nlsEntry);
        mno.setTypeName(EXCEL_EXPORT_MENU_TYPE_NAME);
        mno.setSuperTypeSignature(RuntimeClasses.getSuperTypeSignature(RuntimeClasses.IMenu, r.getBundle()));
        mno.validate();
        mno.run(monitor, workingCopyManager);

        menu = mno.getCreatedMenu();
        workingCopyManager.reconcile(menu.getCompilationUnit(), monitor);
      }

      final String scoutXlsxSpreadsheetAdapterFqn = "org.eclipse.scout.rt.docx4j.client.ScoutXlsxSpreadsheetAdapter";
      MethodOverrideOperation execAction = new MethodOverrideOperation(menu, "execAction") {
        @Override
        protected String createMethodBody(IImportValidator validator) throws JavaModelException {
          String typeRef = SignatureUtility.getTypeReference(SignatureCache.createTypeSignature(scoutXlsxSpreadsheetAdapterFqn), validator);
          String ioFileRef = SignatureUtility.getTypeReference(SignatureCache.createTypeSignature("java.io.File"), validator);
          String shellRef = SignatureUtility.getTypeReference(SignatureCache.createTypeSignature("org.eclipse.scout.rt.shared.services.common.shell.IShellService"), validator);
          String svcRef = SignatureUtility.getTypeReference(SignatureCache.createTypeSignature("org.eclipse.scout.service.SERVICES"), validator);

          StringBuilder body = new StringBuilder();
          body.append("if (getOutline() != null && getOutline().getActivePage() != null) {\n");
          body.append("  ");
          body.append(typeRef);
          body.append(" s = new ");
          body.append(typeRef);
          body.append("();\n");
          body.append("  ");
          body.append(ioFileRef);
          body.append(" xlsx = s.exportPage(null, 0, 0, getOutline().getActivePage());\n");
          body.append("  ");
          body.append(svcRef);
          body.append(".getService(");
          body.append(shellRef);
          body.append(".class).shellOpen(xlsx.getAbsolutePath());\n");
          body.append("}");
          return body.toString();
        }
      };
      execAction.setFormatSource(false);
      execAction.validate();
      execAction.run(monitor, workingCopyManager);

      workingCopyManager.reconcile(execAction.getCreatedMethod().getCompilationUnit(), monitor);

      JavaElementFormatOperation formatOb = new JavaElementFormatOperation(menu, false);
      formatOb.validate();
      formatOb.run(monitor, workingCopyManager);
    }
    else {
      IType typeToDelete = r.m_toolsMenuType.getType(EXCEL_EXPORT_MENU_TYPE_NAME);
      if (TypeUtility.exists(typeToDelete)) {
        JavaElementDeleteOperation d = new JavaElementDeleteOperation();
        d.addMember(typeToDelete);
        d.validate();
        d.run(monitor, workingCopyManager);
      }
    }
  }

  @Override
  public TriState getSelection(IScoutBundle project) throws CoreException {
    IType desktopToolsMenu = getDesktopToolsMenu(project);
    if (TypeUtility.exists(desktopToolsMenu) && TypeUtility.exists(desktopToolsMenu.getType(EXCEL_EXPORT_MENU_TYPE_NAME))) {
      return TriState.TRUE;
    }
    return TriState.FALSE;
  }

  @Override
  public boolean isActive(IScoutBundle project) {
    return getDesktopToolsMenu(project) != null;
  }

  @Override
  protected void contributeResources(IScoutBundle project, List<IScoutTechnologyResource> list) throws CoreException {
    IType desktopToolsMenu = getDesktopToolsMenu(project);
    if (TypeUtility.exists(desktopToolsMenu) && desktopToolsMenu.getResource() instanceof IFile) {
      DesktopTechnologyResource r = new DesktopTechnologyResource(ScoutTypeUtility.getScoutBundle(desktopToolsMenu), (IFile) desktopToolsMenu.getResource(), desktopToolsMenu);
      list.add(r);
    }
  }

  private IScoutBundle[] getClientBundlesBelow(IScoutBundle startBundle) {
    IScoutBundleFilter filter = ScoutBundleFilters.getMultiFilterAnd(ScoutBundleFilters.getBundlesOfTypeFilter(IScoutBundle.TYPE_CLIENT),
        ScoutBundleFilters.getWorkspaceBundlesFilter());
    return startBundle.getChildBundles(filter, true);
  }

  private IType getDesktopToolsMenu(IScoutBundle startBundle) {
    IScoutBundle clients[] = getClientBundlesBelow(startBundle);
    IType iDesktop = TypeUtility.getType(RuntimeClasses.IDesktop);
    ICachedTypeHierarchy desktopHierarchy = TypeUtility.getPrimaryTypeHierarchy(iDesktop);
    IType[] desktops = desktopHierarchy.getAllSubtypes(iDesktop, ScoutTypeFilters.getTypesInScoutBundles(clients));
    if (desktops != null && desktops.length == 1) {
      return desktops[0].getType("ToolsMenu");
    }
    return null;
  }

  private static class DesktopTechnologyResource extends ScoutTechnologyResource {
    private final IType m_toolsMenuType;

    private DesktopTechnologyResource(IScoutBundle bundle, IFile resource, IType toolsMenuType) {
      super(bundle, resource);
      m_toolsMenuType = toolsMenuType;
    }
  }
}
