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
package org.eclipse.scout.sdk.operation.util;

import java.util.regex.Pattern;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.sdk.util.SdkProperties;
import org.eclipse.scout.sdk.util.signature.SimpleImportValidator;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.IWorkingCopyManager;
import org.eclipse.scout.sdk.workspace.IScoutBundle;

/**
 * <h3> {@link ScoutTypeNewOperation}</h3> To create a new {@link IType}. Can be used to create a class or an interface.
 * To
 * create an
 * interface add the {@link Flags#AccInterface} to the typeModifiers.
 * <p>
 * <b>Note:</b> the parameter will not be validated before when run is called.
 */
public class ScoutTypeNewOperation extends AbstractScoutTypeNewOperation {

  private final static Pattern JAVA_LANG_REGEX = Pattern.compile("^java\\.lang\\.[^.]*$");

  private final String m_implementationPackageName;
  private final IScoutBundle m_scoutBundle;

  private IType m_createdType;

  /**
   * @param name
   *          The name of the new type
   * @param implementationPackageName
   *          the package name where the new type should be placed in
   * @param scoutBundle
   *          the bundle where the type has to be created in.
   */
  public ScoutTypeNewOperation(String name, String implementationPackageName, IScoutBundle scoutBundle) {
    super(name);
    m_scoutBundle = scoutBundle;
    m_implementationPackageName = implementationPackageName;
  }

  @Override
  public String getOperationName() {
    return "New Type";
  }

  public String getImplementationPackageName() {
    return m_implementationPackageName;
  }

  @Override
  public void validate() throws IllegalArgumentException {
    super.validate();
    if (StringUtility.isNullOrEmpty(getImplementationPackageName())) {
      throw new IllegalArgumentException("no package defined!");
    }
    if (getScoutBundle() == null) {
      throw new IllegalArgumentException("no bundle defined!");
    }
  }

  public static IPackageFragment getOrCreatePackageFragment(IJavaProject p, String packageName, IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    IPackageFragment pck = null;
    IPackageFragmentRoot root = p.findPackageFragmentRoot(new Path(IPath.SEPARATOR + p.getProject().getName() + IPath.SEPARATOR + SdkProperties.DEFAULT_SOURCE_FOLDER_NAME));
    pck = root.getPackageFragment(packageName);
    if ((pck == null || !pck.exists())) {
      PackageNewOperation proc = new PackageNewOperation(p, SdkProperties.DEFAULT_SOURCE_FOLDER_NAME, packageName);
      proc.run(monitor, workingCopyManager);
      pck = proc.getCreatedPackageFragment();
    }
    return pck;
  }

  @Override
  public void run(IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    IPackageFragment pck = getOrCreatePackageFragment(getScoutBundle().getJavaProject(), getImplementationPackageName(), monitor, workingCopyManager);
    // needed to ensure jdt events getting fired at this point. Otherwise some events getting lost.
    ResourcesPlugin.getWorkspace().checkpoint(false);
    ICompilationUnit icu = pck.getCompilationUnit(getTypeName() + ".java");
    if (!TypeUtility.exists(icu)) {
      icu = pck.createCompilationUnit(getTypeName() + ".java", "", true, monitor);
    }
    else {
      // remove old types
      JavaElementDeleteOperation op = new JavaElementDeleteOperation();
      for (IJavaElement e : icu.getChildren()) {
        op.addMember(e);
      }
      op.run(monitor, workingCopyManager);
    }
    // needed to ensure jdt events getting fired at this point. Otherwise some events getting lost.
    ResourcesPlugin.getWorkspace().checkpoint(false);

    workingCopyManager.register(icu, monitor);
    icu.createPackageDeclaration(pck.getElementName(), monitor);
    SimpleImportValidator validator = new SimpleImportValidator(getImplementationPackageName());
    String content = createSource(validator);

    // imports
    for (String imp : validator.getImportsToCreate()) {
      if (!JAVA_LANG_REGEX.matcher(imp).matches()) {
        icu.createImport(imp, null, monitor);
      }
    }
    m_createdType = icu.createType(content, null, true, monitor);
    // TODO verify this reconcile
    workingCopyManager.reconcile(icu, monitor);
  }

  public IScoutBundle getScoutBundle() {
    return m_scoutBundle;
  }

  @Override
  public IType getCreatedType() {
    return m_createdType;
  }

}
