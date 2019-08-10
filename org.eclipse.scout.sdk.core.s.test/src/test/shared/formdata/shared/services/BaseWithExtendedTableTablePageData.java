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
package formdata.shared.services;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;
import org.eclipse.scout.rt.shared.data.page.AbstractTablePageData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Generated(value = "formdata.client.ui.desktop.outline.pages.BaseWithExtendedTableTablePage", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class BaseWithExtendedTableTablePageData extends AbstractTablePageData {

  private static final long serialVersionUID = 1L;

  @Override
  public BaseWithExtendedTableTableRowData addRow() {
    return (BaseWithExtendedTableTableRowData) super.addRow();
  }

  @Override
  public BaseWithExtendedTableTableRowData addRow(int rowState) {
    return (BaseWithExtendedTableTableRowData) super.addRow(rowState);
  }

  @Override
  public BaseWithExtendedTableTableRowData createRow() {
    return new BaseWithExtendedTableTableRowData();
  }

  @Override
  public Class<? extends AbstractTableRowData> getRowType() {
    return BaseWithExtendedTableTableRowData.class;
  }

  @Override
  public BaseWithExtendedTableTableRowData[] getRows() {
    return (BaseWithExtendedTableTableRowData[]) super.getRows();
  }

  @Override
  public BaseWithExtendedTableTableRowData rowAt(int index) {
    return (BaseWithExtendedTableTableRowData) super.rowAt(index);
  }

  public void setRows(BaseWithExtendedTableTableRowData[] rows) {
    super.setRows(rows);
  }

  public static class BaseWithExtendedTableTableRowData extends AbstractTableRowData {

    private static final long serialVersionUID = 1L;
    public static final String colInAbstractTable = "colInAbstractTable";
    public static final String colInTable = "colInTable";
    private String m_colInAbstractTable;
    private String m_colInTable;

    public String getColInAbstractTable() {
      return m_colInAbstractTable;
    }

    public void setColInAbstractTable(String newColInAbstractTable) {
      m_colInAbstractTable = newColInAbstractTable;
    }

    public String getColInTable() {
      return m_colInTable;
    }

    public void setColInTable(String newColInTable) {
      m_colInTable = newColInTable;
    }
  }
}
