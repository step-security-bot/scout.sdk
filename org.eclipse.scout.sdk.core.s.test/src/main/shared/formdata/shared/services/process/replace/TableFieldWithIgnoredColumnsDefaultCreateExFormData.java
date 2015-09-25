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
@Generated(value = "formdata.client.ui.forms.replace.TableFieldWithIgnoredColumnsDefaultCreateExForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class TableFieldWithIgnoredColumnsDefaultCreateExFormData extends TableFieldWithIgnoredColumnsBaseFormData {

  private static final long serialVersionUID = 1L;

  public TableFieldWithIgnoredColumnsDefaultCreateExFormData() {
  }

  public TableDefaultCreateEx getTableDefaultCreateEx() {
    return getFieldByClass(TableDefaultCreateEx.class);
  }

  @Replace
  public static class TableDefaultCreateEx extends TableBase {

    private static final long serialVersionUID = 1L;

    public TableDefaultCreateEx() {
    }

    @Override
    public TableDefaultCreateExRowData addRow() {
      return (TableDefaultCreateExRowData) super.addRow();
    }

    @Override
    public TableDefaultCreateExRowData addRow(int rowState) {
      return (TableDefaultCreateExRowData) super.addRow(rowState);
    }

    @Override
    public TableDefaultCreateExRowData createRow() {
      return new TableDefaultCreateExRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return TableDefaultCreateExRowData.class;
    }

    @Override
    public TableDefaultCreateExRowData[] getRows() {
      return (TableDefaultCreateExRowData[]) super.getRows();
    }

    @Override
    public TableDefaultCreateExRowData rowAt(int index) {
      return (TableDefaultCreateExRowData) super.rowAt(index);
    }

    public void setRows(TableDefaultCreateExRowData[] rows) {
      super.setRows(rows);
    }

    public static class TableDefaultCreateExRowData extends formdata.shared.services.process.replace.TableFieldWithIgnoredColumnsBaseFormData.TableBase.TableBaseRowData {

      private static final long serialVersionUID = 1L;
      public static final String ignoreDefaultCreate = "ignoreDefaultCreate";
      private String m_ignoreDefaultCreate;

      public TableDefaultCreateExRowData() {
      }

      public String getIgnoreDefaultCreate() {
        return m_ignoreDefaultCreate;
      }

      public void setIgnoreDefaultCreate(String ignoreDefaultCreate) {
        m_ignoreDefaultCreate = ignoreDefaultCreate;
      }
    }
  }
}
