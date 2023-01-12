/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package formdata.shared.extension;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.annotation.Generated;

import org.eclipse.scout.rt.platform.extension.Extends;

import formdata.shared.services.pages.ExtendedEmptyTablePageData.ExtendedEmptyTableRowData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Extends(ExtendedEmptyTableRowData.class)
@Generated(value = "formdata.client.extensions.MultiColumnExtension", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class MultiColumnExtensionData implements Serializable {

  private static final long serialVersionUID = 1L;
  public static final String thirdLong = "thirdLong";
  public static final String fourthDouble = "fourthDouble";
  private Long m_thirdLong;
  private BigDecimal m_fourthDouble;

  public Long getThirdLong() {
    return m_thirdLong;
  }

  public void setThirdLong(Long newThirdLong) {
    m_thirdLong = newThirdLong;
  }

  public BigDecimal getFourthDouble() {
    return m_fourthDouble;
  }

  public void setFourthDouble(BigDecimal newFourthDouble) {
    m_fourthDouble = newFourthDouble;
  }
}
