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
package formdata.shared.services.process.replace;

import org.eclipse.scout.commons.annotations.Replace;
import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated, no manual modifications recommended.
 * 
 * @generated
 */
public class TableFieldExFormData extends TableFieldBaseFormData {

  private static final long serialVersionUID = 1L;

  public TableFieldExFormData() {
  }

  public EmptyTableExtended getEmptyTableExtended() {
    return getFieldByClass(EmptyTableExtended.class);
  }

  public ExtendedAddress getExtendedAddress() {
    return getFieldByClass(ExtendedAddress.class);
  }

  public ExtendedPersonTable getExtendedPersonTable() {
    return getFieldByClass(ExtendedPersonTable.class);
  }

  public NoTableExtended getNoTableExtended() {
    return getFieldByClass(NoTableExtended.class);
  }

  public TableExtended getTableExtended() {
    return getFieldByClass(TableExtended.class);
  }

  @Replace
  public static class EmptyTableExtended extends EmptyTable {

    private static final long serialVersionUID = 1L;

    public EmptyTableExtended() {
    }

    @Override
    public EmptyTableExtendedRowData addRow() {
      return (EmptyTableExtendedRowData) super.addRow();
    }

    @Override
    public EmptyTableExtendedRowData addRow(int rowState) {
      return (EmptyTableExtendedRowData) super.addRow(rowState);
    }

    @Override
    public EmptyTableExtendedRowData createRow() {
      return new EmptyTableExtendedRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return EmptyTableExtendedRowData.class;
    }

    @Override
    public EmptyTableExtendedRowData[] getRows() {
      return (EmptyTableExtendedRowData[]) super.getRows();
    }

    @Override
    public EmptyTableExtendedRowData rowAt(int index) {
      return (EmptyTableExtendedRowData) super.rowAt(index);
    }

    public void setRows(EmptyTableExtendedRowData[] rows) {
      super.setRows(rows);
    }

    public static class EmptyTableExtendedRowData extends EmptyTableRowData {

      private static final long serialVersionUID = 1L;
      public static final String single = "single";
      private String m_single;

      public EmptyTableExtendedRowData() {
      }

      public String getSingle() {
        return m_single;
      }

      public void setSingle(String single) {
        m_single = single;
      }
    }
  }

  @Replace
  public static class ExtendedAddress extends AddressTable {

    private static final long serialVersionUID = 1L;

    public ExtendedAddress() {
    }

    @Override
    public ExtendedAddressRowData addRow() {
      return (ExtendedAddressRowData) super.addRow();
    }

    @Override
    public ExtendedAddressRowData addRow(int rowState) {
      return (ExtendedAddressRowData) super.addRow(rowState);
    }

    @Override
    public ExtendedAddressRowData createRow() {
      return new ExtendedAddressRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return ExtendedAddressRowData.class;
    }

    @Override
    public ExtendedAddressRowData[] getRows() {
      return (ExtendedAddressRowData[]) super.getRows();
    }

    @Override
    public ExtendedAddressRowData rowAt(int index) {
      return (ExtendedAddressRowData) super.rowAt(index);
    }

    public void setRows(ExtendedAddressRowData[] rows) {
      super.setRows(rows);
    }

    public static class ExtendedAddressRowData extends AddressTableRowData {

      private static final long serialVersionUID = 1L;
      public static final String state = "state";
      private String m_state;

      public ExtendedAddressRowData() {
      }

      public String getState() {
        return m_state;
      }

      public void setState(String state) {
        m_state = state;
      }
    }
  }

  @Replace
  public static class ExtendedPersonTable extends PersonTable {

    private static final long serialVersionUID = 1L;

    public ExtendedPersonTable() {
    }

    @Override
    public ExtendedPersonTableRowData addRow() {
      return (ExtendedPersonTableRowData) super.addRow();
    }

    @Override
    public ExtendedPersonTableRowData addRow(int rowState) {
      return (ExtendedPersonTableRowData) super.addRow(rowState);
    }

    @Override
    public ExtendedPersonTableRowData createRow() {
      return new ExtendedPersonTableRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return ExtendedPersonTableRowData.class;
    }

    @Override
    public ExtendedPersonTableRowData[] getRows() {
      return (ExtendedPersonTableRowData[]) super.getRows();
    }

    @Override
    public ExtendedPersonTableRowData rowAt(int index) {
      return (ExtendedPersonTableRowData) super.rowAt(index);
    }

    public void setRows(ExtendedPersonTableRowData[] rows) {
      super.setRows(rows);
    }

    public static class ExtendedPersonTableRowData extends PersonTableRowData {

      private static final long serialVersionUID = 1L;
      public static final String lastName = "lastName";
      private String m_lastName;

      public ExtendedPersonTableRowData() {
      }

      public String getLastName() {
        return m_lastName;
      }

      public void setLastName(String lastName) {
        m_lastName = lastName;
      }
    }
  }

  @Replace
  public static class NoTableExtended extends NoTable {

    private static final long serialVersionUID = 1L;

    public NoTableExtended() {
    }

    @Override
    public NoTableExtendedRowData addRow() {
      return (NoTableExtendedRowData) super.addRow();
    }

    @Override
    public NoTableExtendedRowData addRow(int rowState) {
      return (NoTableExtendedRowData) super.addRow(rowState);
    }

    @Override
    public NoTableExtendedRowData createRow() {
      return new NoTableExtendedRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return NoTableExtendedRowData.class;
    }

    @Override
    public NoTableExtendedRowData[] getRows() {
      return (NoTableExtendedRowData[]) super.getRows();
    }

    @Override
    public NoTableExtendedRowData rowAt(int index) {
      return (NoTableExtendedRowData) super.rowAt(index);
    }

    public void setRows(NoTableExtendedRowData[] rows) {
      super.setRows(rows);
    }

    public static class NoTableExtendedRowData extends AbstractTableRowData {

      private static final long serialVersionUID = 1L;
      public static final String new_ = "new";
      private String m_new;

      public NoTableExtendedRowData() {
      }

      public String getNew() {
        return m_new;
      }

      public void setNew(String newValue) {
        m_new = newValue;
      }
    }
  }

  @Replace
  public static class TableExtended extends Table {

    private static final long serialVersionUID = 1L;

    public TableExtended() {
    }

    @Override
    public TableExtendedRowData addRow() {
      return (TableExtendedRowData) super.addRow();
    }

    @Override
    public TableExtendedRowData addRow(int rowState) {
      return (TableExtendedRowData) super.addRow(rowState);
    }

    @Override
    public TableExtendedRowData createRow() {
      return new TableExtendedRowData();
    }

    @Override
    public Class<? extends AbstractTableRowData> getRowType() {
      return TableExtendedRowData.class;
    }

    @Override
    public TableExtendedRowData[] getRows() {
      return (TableExtendedRowData[]) super.getRows();
    }

    @Override
    public TableExtendedRowData rowAt(int index) {
      return (TableExtendedRowData) super.rowAt(index);
    }

    public void setRows(TableExtendedRowData[] rows) {
      super.setRows(rows);
    }

    public static class TableExtendedRowData extends TableRowData {

      private static final long serialVersionUID = 1L;
      public static final String boolean_ = "boolean";
      private Boolean m_boolean;

      public TableExtendedRowData() {
      }

      public Boolean getBoolean() {
        return m_boolean;
      }

      public void setBoolean(Boolean booleanValue) {
        m_boolean = booleanValue;
      }
    }
  }
}
