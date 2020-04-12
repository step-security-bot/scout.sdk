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
package formdata.shared.services.process.replace;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Generated(value = "formdata.client.ui.template.formfield.replace.AbstractRadioButtonGroupWithFields", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public abstract class AbstractRadioButtonGroupWithFieldsData extends AbstractValueFieldData<Long> {

  private static final long serialVersionUID = 1L;

  public InputString getInputString() {
    return getFieldByClass(InputString.class);
  }

  public static class InputString extends AbstractValueFieldData<String> {

    private static final long serialVersionUID = 1L;
  }
}
