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
package formdata.shared.scope.extended;

import javax.annotation.Generated;

import org.eclipse.scout.rt.platform.Replace;

import formdata.shared.scope.field.AbstractScopeTestGroupBoxData.Process;
import formdata.shared.scope.orig.ScopeTestFormData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Replace
@Generated(value = "formdata.client.scope.extended.ExtendedScopeTestForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class ExtendedScopeTestFormData extends ScopeTestFormData {

  private static final long serialVersionUID = 1L;

  public AnliegenBox getAnliegenBox() {
    return getFieldByClass(AnliegenBox.class);
  }

  public ExtendedProcess getExtendedProcess() {
    return getFieldByClass(ExtendedProcess.class);
  }

  @Replace
  public static class AnliegenBox extends ProcessesBox {

    private static final long serialVersionUID = 1L;
  }

  @Replace
  public static class ExtendedProcess extends Process {

    private static final long serialVersionUID = 1L;
  }
}
