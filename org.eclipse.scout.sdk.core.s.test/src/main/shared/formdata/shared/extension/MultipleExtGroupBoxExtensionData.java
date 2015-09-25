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
package formdata.shared.extension;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Generated;

import org.eclipse.scout.commons.annotations.Extends;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractFormFieldData;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

import formdata.shared.services.process.ListBoxFormData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 * 
 * @generated
 */
@Extends(ListBoxFormData.class)
@Generated(value = "formdata.client.extensions.MultipleExtGroupBoxExtension", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class MultipleExtGroupBoxExtensionData extends AbstractFormFieldData {

  private static final long serialVersionUID = 1L;

  public MultipleExtGroupBoxExtensionData() {
  }

  public SecondDouble getSecondDouble() {
    return getFieldByClass(SecondDouble.class);
  }

  public ThirdDate getThirdDate() {
    return getFieldByClass(ThirdDate.class);
  }

  public static class SecondDouble extends AbstractValueFieldData<BigDecimal> {

    private static final long serialVersionUID = 1L;

    public SecondDouble() {
    }
  }

  public static class ThirdDate extends AbstractValueFieldData<Date> {

    private static final long serialVersionUID = 1L;

    public ThirdDate() {
    }
  }
}
