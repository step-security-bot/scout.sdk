/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package formdata.shared.services.pages;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.annotation.Generated;

import org.eclipse.scout.commons.annotations.Extends;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 * 
 * @generated
 */
@Extends(BaseTablePageData.BaseTableRowData.class)
@Generated(value = "org.eclipse.scout.sdk.workspace.dto.pagedata.PageDataDtoUpdateOperation", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class PageWithTableExtensionData implements Serializable {

  private static final long serialVersionUID = 1L;
  public static final String bigDecimalTest = "bigDecimalTest";
  private BigDecimal m_bigDecimalTest;

  public PageWithTableExtensionData() {
  }

  public BigDecimal getBigDecimalTest() {
    return m_bigDecimalTest;
  }

  public void setBigDecimalTest(BigDecimal bigDecimalTest) {
    m_bigDecimalTest = bigDecimalTest;
  }
}
