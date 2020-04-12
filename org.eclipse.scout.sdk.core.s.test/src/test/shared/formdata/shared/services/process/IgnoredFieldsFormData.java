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
package formdata.shared.services.process;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Generated(value = "formdata.client.ui.forms.IgnoredFieldsForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class IgnoredFieldsFormData extends AbstractFormData {

  private static final long serialVersionUID = 1L;

  public NotIgnored getNotIgnored() {
    return getFieldByClass(NotIgnored.class);
  }

  public static class NotIgnored extends AbstractValueFieldData<String> {

    private static final long serialVersionUID = 1L;
  }
}
