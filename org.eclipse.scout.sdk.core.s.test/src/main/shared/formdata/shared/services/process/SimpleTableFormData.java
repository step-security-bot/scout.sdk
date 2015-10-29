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
package formdata.shared.services.process;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Generated(value = "formdata.client.ui.forms.SimpleTableForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SimpleTableFormData extends AbstractFormData {

  private static final long serialVersionUID = 1L;

  public SimpleTableFormData() {
  }

  public TestTable getTestTable() {
    return getFieldByClass(TestTable.class);
  }

  public static class TestTable extends AbstractTableFieldBeanData {

    private static final long serialVersionUID = 1L;

    public TestTable() {
    }

    @Override
    public TestTableRowData addRow() {
      return (TestTableRowData) super.addRow();
    }

    @Override
    public TestTableRowData addRow(int rowState) {
      return (TestTableRowData) super.addRow(rowState);
    }

    @Override
    public TestTableRowData createRow() {
      return new TestTableRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return TestTableRowData.class;
    }

    @Override
    public TestTableRowData[] getRows() {
      return (TestTableRowData[]) super.getRows();
    }

    @Override
    public TestTableRowData rowAt(int index) {
      return (TestTableRowData) super.rowAt(index);
    }

    public void setRows(TestTableRowData[] rows) {
      super.setRows(rows);
    }

    public static class TestTableRowData extends AbstractTableRowData {

      private static final long serialVersionUID = 1L;
      public static final String name = "name";
      private String m_name;

      public TestTableRowData() {
      }

      public String getName() {
        return m_name;
      }

      public void setName(String name) {
        m_name = name;
      }
    }
  }
}
