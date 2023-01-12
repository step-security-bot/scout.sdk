/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package formdata.shared.ui.template.formfield.replace.levels;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.form.AbstractFormData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Generated(value = "formdata.client.ui.template.formfield.replace.levels.FirstLevelForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class FirstLevelFormData extends AbstractFormData {

  private static final long serialVersionUID = 1L;

  public FirstInnerBox getFirstInnerBox() {
    return getFieldByClass(FirstInnerBox.class);
  }

  public static class FirstInnerBox extends AbstractMainBoxData {

    private static final long serialVersionUID = 1L;
  }
}
