/*
 * Copyright (c) 2016 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package formdata.shared.services.pages;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Generated(value = "formdata.client.ui.desktop.outline.pages.ExtendedTablePage", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class ExtendedTablePageData extends BaseTablePageData {

  private static final long serialVersionUID = 1L;

  @Override
  public ExtendedTableRowData addRow() {
    return (ExtendedTableRowData) super.addRow();
  }

  @Override
  public ExtendedTableRowData addRow(int rowState) {
    return (ExtendedTableRowData) super.addRow(rowState);
  }

  @Override
  public ExtendedTableRowData createRow() {
    return new ExtendedTableRowData();
  }

  @Override
  public Class<? extends AbstractTableRowData> getRowType() {
    return ExtendedTableRowData.class;
  }

  @Override
  public ExtendedTableRowData[] getRows() {
    return (ExtendedTableRowData[]) super.getRows();
  }

  @Override
  public ExtendedTableRowData rowAt(int index) {
    return (ExtendedTableRowData) super.rowAt(index);
  }

  public void setRows(ExtendedTableRowData[] rows) {
    super.setRows(rows);
  }

  public static class ExtendedTableRowData extends BaseTableRowData {

    private static final long serialVersionUID = 1L;
    public static final String intermediate = "intermediate";
    public static final String ignoredColumnEx = "ignoredColumnEx";
    private BigDecimal m_intermediate;
    private Date m_ignoredColumnEx;

    public BigDecimal getIntermediate() {
      return m_intermediate;
    }

    public void setIntermediate(BigDecimal newIntermediate) {
      m_intermediate = newIntermediate;
    }

    public Date getIgnoredColumnEx() {
      return m_ignoredColumnEx;
    }

    public void setIgnoredColumnEx(Date newIgnoredColumnEx) {
      m_ignoredColumnEx = newIgnoredColumnEx;
    }
  }
}
