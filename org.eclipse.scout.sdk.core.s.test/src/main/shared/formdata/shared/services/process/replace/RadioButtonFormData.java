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
package formdata.shared.services.process.replace;

import javax.annotation.Generated;

import org.eclipse.scout.commons.annotations.Replace;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

import formdata.shared.services.process.replace.AbstractRadioButtonGroupWithFieldsData.InputString;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 * 
 * @generated
 */
@Generated(value = "formdata.client.ui.template.formfield.replace.RadioButtonForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class RadioButtonFormData extends AbstractFormData {

  private static final long serialVersionUID = 1L;

  public RadioButtonFormData() {
  }

  public InputExString getInputExString() {
    return getFieldByClass(InputExString.class);
  }

  public UsageOneUsualString getUsageOneUsualString() {
    return getFieldByClass(UsageOneUsualString.class);
  }

  public UsedRadioButtonGroup getUsedRadioButtonGroup() {
    return getFieldByClass(UsedRadioButtonGroup.class);
  }

  public UsualRadioButtonGroup getUsualRadioButtonGroup() {
    return getFieldByClass(UsualRadioButtonGroup.class);
  }

  public UsualString getUsualString() {
    return getFieldByClass(UsualString.class);
  }

  @Replace
  public static class InputExString extends InputString {

    private static final long serialVersionUID = 1L;

    public InputExString() {
    }
  }

  public static class UsageOneUsualString extends AbstractValueFieldData<String> {

    private static final long serialVersionUID = 1L;

    public UsageOneUsualString() {
    }
  }

  public static class UsedRadioButtonGroup extends AbstractRadioButtonGroupWithFieldsData {

    private static final long serialVersionUID = 1L;

    public UsedRadioButtonGroup() {
    }
  }

  public static class UsualRadioButtonGroup extends AbstractValueFieldData<String> {

    private static final long serialVersionUID = 1L;

    public UsualRadioButtonGroup() {
    }
  }

  public static class UsualString extends AbstractValueFieldData<String> {

    private static final long serialVersionUID = 1L;

    public UsualString() {
    }
  }
}
