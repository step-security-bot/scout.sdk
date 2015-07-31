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
import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 * 
 * @generated
 */
@Generated(value = "FormDataUpdateOperation", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class TableFieldWithIgnoredColumnsCreateExFormData extends TableFieldWithIgnoredColumnsBaseFormData {

  private static final long serialVersionUID = 1L;

  public TableFieldWithIgnoredColumnsCreateExFormData() {
  }

  public TableCreateEx getTableCreateEx() {
    return getFieldByClass(TableCreateEx.class);
  }

  @Replace
  public static class TableCreateEx extends TableBase {

    private static final long serialVersionUID = 1L;

    public TableCreateEx() {
    }

    @Override
    public TableCreateExRowData addRow() {
      return (TableCreateExRowData) super.addRow();
    }

    @Override
    public TableCreateExRowData addRow(int rowState) {
      return (TableCreateExRowData) super.addRow(rowState);
    }

    @Override
    public TableCreateExRowData createRow() {
      return new TableCreateExRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return TableCreateExRowData.class;
    }

    @Override
    public TableCreateExRowData[] getRows() {
      return (TableCreateExRowData[]) super.getRows();
    }

    @Override
    public TableCreateExRowData rowAt(int index) {
      return (TableCreateExRowData) super.rowAt(index);
    }

    public void setRows(TableCreateExRowData[] rows) {
      super.setRows(rows);
    }

    public static class TableCreateExRowData extends TableBaseRowData {

      private static final long serialVersionUID = 1L;
      public static final String ignoreCreate = "ignoreCreate";
      private String m_ignoreCreate;

      public TableCreateExRowData() {
      }

      public String getIgnoreCreate() {
        return m_ignoreCreate;
      }

      public void setIgnoreCreate(String ignoreCreate) {
        m_ignoreCreate = ignoreCreate;
      }
    }
  }
}
