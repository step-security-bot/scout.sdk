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
 */
@Generated(value = "formdata.client.ui.forms.replace.TableFieldWithIgnoredColumnsDefaultExForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class TableFieldWithIgnoredColumnsDefaultExFormData extends TableFieldWithIgnoredColumnsBaseFormData {

  private static final long serialVersionUID = 1L;

  public TableFieldWithIgnoredColumnsDefaultExFormData() {
  }

  public TableDefaultEx getTableDefaultEx() {
    return getFieldByClass(TableDefaultEx.class);
  }

  @Replace
  public static class TableDefaultEx extends TableBase {

    private static final long serialVersionUID = 1L;

    public TableDefaultEx() {
    }

    @Override
    public TableDefaultExRowData addRow() {
      return (TableDefaultExRowData) super.addRow();
    }

    @Override
    public TableDefaultExRowData addRow(int rowState) {
      return (TableDefaultExRowData) super.addRow(rowState);
    }

    @Override
    public TableDefaultExRowData createRow() {
      return new TableDefaultExRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return TableDefaultExRowData.class;
    }

    @Override
    public TableDefaultExRowData[] getRows() {
      return (TableDefaultExRowData[]) super.getRows();
    }

    @Override
    public TableDefaultExRowData rowAt(int index) {
      return (TableDefaultExRowData) super.rowAt(index);
    }

    public void setRows(TableDefaultExRowData[] rows) {
      super.setRows(rows);
    }

    public static class TableDefaultExRowData extends formdata.shared.services.process.replace.TableFieldWithIgnoredColumnsBaseFormData.TableBase.TableBaseRowData {

      private static final long serialVersionUID = 1L;

      public TableDefaultExRowData() {
      }
    }
  }
}
