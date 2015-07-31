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
package formdata.shared.services;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 * 
 * @generated
 */
@Generated(value = "FormDataUpdateOperation", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class BaseWithExtendedTableFormData extends AbstractFormData {

  private static final long serialVersionUID = 1L;

  public BaseWithExtendedTableFormData() {
  }

  public TableInForm getTableInForm() {
    return getFieldByClass(TableInForm.class);
  }

  public static class TableInForm extends AbstractTableFieldBeanData {

    private static final long serialVersionUID = 1L;

    public TableInForm() {
    }

    @Override
    public TableInFormRowData addRow() {
      return (TableInFormRowData) super.addRow();
    }

    @Override
    public TableInFormRowData addRow(int rowState) {
      return (TableInFormRowData) super.addRow(rowState);
    }

    @Override
    public TableInFormRowData createRow() {
      return new TableInFormRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return TableInFormRowData.class;
    }

    @Override
    public TableInFormRowData[] getRows() {
      return (TableInFormRowData[]) super.getRows();
    }

    @Override
    public TableInFormRowData rowAt(int index) {
      return (TableInFormRowData) super.rowAt(index);
    }

    public void setRows(TableInFormRowData[] rows) {
      super.setRows(rows);
    }

    public static class TableInFormRowData extends AbstractTableRowData {

      private static final long serialVersionUID = 1L;
      public static final String colInAbstractTable = "colInAbstractTable";
      public static final String colInDesktopForm = "colInDesktopForm";
      private String m_colInAbstractTable;
      private String m_colInDesktopForm;

      public TableInFormRowData() {
      }

      public String getColInAbstractTable() {
        return m_colInAbstractTable;
      }

      public void setColInAbstractTable(String colInAbstractTable) {
        m_colInAbstractTable = colInAbstractTable;
      }

      public String getColInDesktopForm() {
        return m_colInDesktopForm;
      }

      public void setColInDesktopForm(String colInDesktopForm) {
        m_colInDesktopForm = colInDesktopForm;
      }
    }
  }
}
