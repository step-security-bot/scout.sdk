/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.s2e.nls.internal.simpleproject;

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.scout.sdk.core.util.SdkLog;
import org.eclipse.scout.sdk.s2e.nls.model.Language;
import org.eclipse.scout.sdk.s2e.nls.resource.AbstractTranslationResource;

public class PlatformTranslationFile extends AbstractTranslationResource {

  public PlatformTranslationFile(InputStream is, Language language) {
    super(language);
    try {
      parseResource(is);
    }
    catch (Exception e) {
      SdkLog.error("could not parse translation file: " + getLanguage().getDispalyName(), e);
    }
  }

  @Override
  public void reload(IProgressMonitor monitor) {
    // void here
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }
}
