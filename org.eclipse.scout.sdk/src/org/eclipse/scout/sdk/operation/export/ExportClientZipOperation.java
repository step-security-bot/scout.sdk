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
package org.eclipse.scout.sdk.operation.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.internal.core.exports.FeatureExportInfo;
import org.eclipse.pde.internal.core.exports.ProductExportOperation;
import org.eclipse.scout.commons.IOUtility;
import org.eclipse.scout.sdk.internal.ScoutSdk;
import org.eclipse.scout.sdk.operation.IOperation;
import org.eclipse.scout.sdk.util.log.ScoutStatus;
import org.eclipse.scout.sdk.util.pde.ProductFileModelHelper;
import org.eclipse.scout.sdk.util.resources.ResourceUtility;
import org.eclipse.scout.sdk.util.typecache.IWorkingCopyManager;

/**
 * <h3>{@link ExportClientZipOperation}</h3>
 *
 * @author Matthias Villiger
 * @since 3.8.0 01.03.2012
 */
@SuppressWarnings("restriction")
public class ExportClientZipOperation implements IOperation {

  private final IFile m_clientProduct;
  private IFolder m_htmlFolder;
  private String m_targetDir;

  private File m_tempBuildDir;
  private File m_clientZipFile;
  private String m_zipName;

  public ExportClientZipOperation(IFile clientProduct) {
    m_clientProduct = clientProduct;
  }

  @Override
  public String getOperationName() {
    return "Export '" + getClientProduct().getName() + "' to zip file...";
  }

  @Override
  public void validate() {
    if (getClientProduct() == null) {
      throw new IllegalArgumentException("Client product can not be null!");
    }
    if (getHtmlFolder() == null || !getHtmlFolder().exists()) {
      throw new IllegalArgumentException("Html folder does not exist!");
    }
  }

  @Override
  public void run(IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    try {
      m_tempBuildDir = IOUtility.createTempDirectory("clientZipExportBuildDir");
      IStatus result = buildClientProduct(monitor);
      if (result.isOK()) {
        if (monitor.isCanceled()) {
          return;
        }
        try {
          IOUtility.deleteDirectory(m_tempBuildDir);
        }
        catch (Exception e) {
          // nop
        }
      }
      else {
        throw new CoreException(result);
      }
    }
    catch (CoreException e) {
      throw e;
    }
    catch (Exception e) {
      throw new CoreException(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, "Error during product export.", e));
    }
  }

  private IStatus buildClientProduct(IProgressMonitor monitor) throws CoreException, IOException {
    ProductFileModelHelper pfmh = new ProductFileModelHelper(getClientProduct());
    m_zipName = getZipName(getHtmlFolder(), pfmh) + ".zip";
    List<BundleDescription> pluginModels = pfmh.ProductFile.getPluginModels();

    FeatureExportInfo featureInfo = new FeatureExportInfo();
    featureInfo.toDirectory = true;
    featureInfo.exportSource = false;
    featureInfo.exportSourceBundle = false;
    featureInfo.allowBinaryCycles = true;
    featureInfo.exportMetadata = false;
    featureInfo.destinationDirectory = m_tempBuildDir.getAbsolutePath() + "/client/buildDir/" + getZipName();
    featureInfo.items = pluginModels.toArray(new BundleDescription[pluginModels.size()]);

    ProductExportOperation productExportOp = new ProductExportOperation(featureInfo, "Build product '" + getZipName() + "'...", pfmh.ProductFile.getProduct(), ".");
    productExportOp.schedule();
    try {
      productExportOp.join();
    }
    catch (InterruptedException e) {
      throw new CoreException(new ScoutStatus("Interrupted while waiting for product export to finish.", e));
    }

    IStatus result = productExportOp.getResult();
    if (!result.isOK()) {
      return result;
    }
    if (monitor.isCanceled()) {
      return Status.CANCEL_STATUS;
    }

    File baseDir = new File(m_tempBuildDir.getAbsolutePath(), "client/buildDir/" + getZipName());
    ExportServerWarOperation.overwriteConfigIniIfNecessary(pfmh, new Path(baseDir.getAbsolutePath()).append("configuration"));

    // create zip file
    m_clientZipFile = new File(getTargetDirectory(), getZipName());
    if (m_clientZipFile.exists()) {
      m_clientZipFile.delete();
    }
    else {
      File folder = m_clientZipFile.getParentFile();
      if (!folder.exists() && !folder.mkdirs()) {
        throw new IOException("Unable to create file directory '" + m_clientZipFile.getParentFile().getAbsolutePath() + "'.");
      }
    }

    ZipOutputStream zipOut = null;
    try {
      zipOut = new ZipOutputStream(new FileOutputStream(m_clientZipFile));
      ResourceUtility.addFolderToZip(baseDir, zipOut);
      zipOut.flush();
    }
    finally {
      if (zipOut != null) {
        zipOut.close();
      }
    }

    return Status.OK_STATUS;
  }

  private String getZipName(IFolder destinationFolder, ProductFileModelHelper clientModel) throws CoreException {
    BufferedReader reader = null;
    try {
      IResource indexResource = destinationFolder.findMember("index.html");
      if (indexResource.exists() && indexResource.getType() == IResource.FILE) {
        reader = new BufferedReader(new InputStreamReader(((IFile) indexResource).getContents()));
        Pattern p = Pattern.compile("\\<a\\shref\\=(\\\")?[^a-zA-Z0-9]*([a-zA-Z0-9]*)\\.zip(\\\")?\\>");
        String l = reader.readLine();
        while (l != null) {
          Matcher m = p.matcher(l);
          if (m.find()) {
            return m.group(2);
          }
          l = reader.readLine();
        }
      }
    }
    catch (Exception e) {
      ScoutSdk.logWarning("could not parse application name out of the index.html in '" + destinationFolder.getFullPath() + "'", e);
    }
    finally {
      if (reader != null) {
        try {
          reader.close();
        }
        catch (IOException e) {
          // void
        }
      }
    }
    String productName = clientModel.ProductFile.getProduct().getName();
    if (productName == null) {
      productName = "clientApplication";
    }
    else {
      StringBuilder productNameBuilder = new StringBuilder();
      String[] split = productName.split("\\s");
      for (String s : split) {
        if (s.length() > 0) {
          productNameBuilder.append(Character.toUpperCase(s.charAt(0)));
          if (s.length() > 1) {
            productNameBuilder.append(s.substring(1));
          }
        }
      }
      productName = productNameBuilder.toString();
    }

    return productName;
  }

  /**
   * @return the clientProduct
   */
  public IFile getClientProduct() {
    return m_clientProduct;
  }

  /**
   * @return the htmlFolder
   */
  public IFolder getHtmlFolder() {
    return m_htmlFolder;
  }

  /**
   * @param htmlFolder
   *          the htmlFolder to set
   */
  public void setHtmlFolder(IFolder htmlFolder) {
    m_htmlFolder = htmlFolder;
  }

  public void setTargetDirectory(String targetDir) {
    m_targetDir = targetDir;
  }

  public String getTargetDirectory() {
    return m_targetDir;
  }

  public File getResultingZipFile() {
    return m_clientZipFile;
  }

  public String getZipName() {
    return m_zipName;
  }
}
