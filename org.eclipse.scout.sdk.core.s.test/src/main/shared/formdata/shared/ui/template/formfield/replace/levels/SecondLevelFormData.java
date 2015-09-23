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
package formdata.shared.ui.template.formfield.replace.levels;

import javax.annotation.Generated;

import org.eclipse.scout.commons.annotations.Replace;

import formdata.shared.ui.template.formfield.replace.levels.AbstractMainBoxData.FirstLevel;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 * 
 * @generated
 */
@Generated(value = "FormDataUpdateOperation", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SecondLevelFormData extends FirstLevelFormData {

  private static final long serialVersionUID = 1L;

  public SecondLevelFormData() {
  }

  public SecondInnerBox getSecondInnerBox() {
    return getFieldByClass(SecondInnerBox.class);
  }

  @Replace
  public static class SecondInnerBox extends FirstInnerBox {

    private static final long serialVersionUID = 1L;

    public SecondInnerBox() {
    }

    public SecondLevel getSecondLevel() {
      return getFieldByClass(SecondLevel.class);
    }

    @Replace
    public static class SecondLevel extends FirstLevel {

      private static final long serialVersionUID = 1L;

      public SecondLevel() {
      }
    }
  }
}
