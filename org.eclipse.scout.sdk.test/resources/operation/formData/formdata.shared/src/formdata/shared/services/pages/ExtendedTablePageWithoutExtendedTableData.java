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

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated, no manual modifications recommended.
 * 
 * @generated
 */
public class ExtendedTablePageWithoutExtendedTableData extends BaseTablePageData {

  private static final long serialVersionUID = 1L;

  public ExtendedTablePageWithoutExtendedTableData() {
  }

  @Override
  public ExtendedTablePageWithoutExtendedTableRowData addRow() {
    return (ExtendedTablePageWithoutExtendedTableRowData) super.addRow();
  }

  @Override
  public ExtendedTablePageWithoutExtendedTableRowData addRow(int rowState) {
    return (ExtendedTablePageWithoutExtendedTableRowData) super.addRow(rowState);
  }

  @Override
  public ExtendedTablePageWithoutExtendedTableRowData createRow() {
    return new ExtendedTablePageWithoutExtendedTableRowData();
  }

  @Override
  public Class<? extends AbstractTableRowData> getRowType() {
    return ExtendedTablePageWithoutExtendedTableRowData.class;
  }

  @Override
  public ExtendedTablePageWithoutExtendedTableRowData[] getRows() {
    return (ExtendedTablePageWithoutExtendedTableRowData[]) super.getRows();
  }

  @Override
  public ExtendedTablePageWithoutExtendedTableRowData rowAt(int index) {
    return (ExtendedTablePageWithoutExtendedTableRowData) super.rowAt(index);
  }

  public void setRows(ExtendedTablePageWithoutExtendedTableRowData[] rows) {
    super.setRows(rows);
  }

  public static class ExtendedTablePageWithoutExtendedTableRowData extends BaseTableRowData {

    private static final long serialVersionUID = 1L;

    public ExtendedTablePageWithoutExtendedTableRowData() {
    }
  }
}
