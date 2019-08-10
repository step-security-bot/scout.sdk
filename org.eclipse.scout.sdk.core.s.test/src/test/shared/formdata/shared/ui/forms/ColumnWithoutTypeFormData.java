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
package formdata.shared.ui.forms;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Generated(value = "formdata.client.ui.forms.ColumnWithoutTypeForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class ColumnWithoutTypeFormData extends AbstractFormData {

  private static final long serialVersionUID = 1L;

  public MyTable getMyTable() {
    return getFieldByClass(MyTable.class);
  }

  public static class MyTable extends AbstractTableFieldBeanData {

    private static final long serialVersionUID = 1L;

    @Override
    public MyTableRowData addRow() {
      return (MyTableRowData) super.addRow();
    }

    @Override
    public MyTableRowData addRow(int rowState) {
      return (MyTableRowData) super.addRow(rowState);
    }

    @Override
    public MyTableRowData createRow() {
      return new MyTableRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return MyTableRowData.class;
    }

    @Override
    public MyTableRowData[] getRows() {
      return (MyTableRowData[]) super.getRows();
    }

    @Override
    public MyTableRowData rowAt(int index) {
      return (MyTableRowData) super.rowAt(index);
    }

    public void setRows(MyTableRowData[] rows) {
      super.setRows(rows);
    }

    public static class MyTableRowData extends AbstractTableRowData {

      private static final long serialVersionUID = 1L;
      public static final String my = "my";
      private Object m_my;

      public Object getMy() {
        return m_my;
      }

      public void setMy(Object newMy) {
        m_my = newMy;
      }
    }
  }
}
