/*******************************************************************************
 * Copyright (c) 2016 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package formdata.shared.extension;

import javax.annotation.Generated;

import org.eclipse.scout.rt.platform.extension.Extends;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractFormFieldData;
import org.eclipse.scout.rt.shared.data.form.properties.AbstractPropertyData;

import formdata.shared.services.process.ListBoxFormData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Extends(ListBoxFormData.class)
@Generated(value = "formdata.client.extensions.FormPropertyExtension", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class PropertyExtensionData extends AbstractFormFieldData {

  private static final long serialVersionUID = 1L;

  /**
   * access method for property LongValue.
   */
  public Long getLongValue() {
    return getLongValueProperty().getValue();
  }

  /**
   * access method for property LongValue.
   */
  public void setLongValue(Long longValue) {
    getLongValueProperty().setValue(longValue);
  }

  public LongValueProperty getLongValueProperty() {
    return getPropertyByClass(LongValueProperty.class);
  }

  public static class LongValueProperty extends AbstractPropertyData<Long> {

    private static final long serialVersionUID = 1L;
  }
}
