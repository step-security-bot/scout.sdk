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
package formdata.shared.services.process;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated, no manual modifications recommended.
 * 
 * @generated
 */
public abstract class AbstractAddressTableFieldData extends AbstractTableFieldBeanData {

  private static final long serialVersionUID = 1L;

  public AbstractAddressTableFieldData() {
  }

  @Override
  public AbstractAddressTableRowData addRow() {
    return (AbstractAddressTableRowData) super.addRow();
  }

  @Override
  public AbstractAddressTableRowData addRow(int rowState) {
    return (AbstractAddressTableRowData) super.addRow(rowState);
  }

  @Override
  public abstract AbstractAddressTableRowData createRow();

  @Override
  public Class<? extends AbstractTableRowData> getRowType() {
    return AbstractAddressTableRowData.class;
  }

  @Override
  public AbstractAddressTableRowData[] getRows() {
    return (AbstractAddressTableRowData[]) super.getRows();
  }

  @Override
  public AbstractAddressTableRowData rowAt(int index) {
    return (AbstractAddressTableRowData) super.rowAt(index);
  }

  public void setRows(AbstractAddressTableRowData[] rows) {
    super.setRows(rows);
  }

  public abstract static class AbstractAddressTableRowData extends AbstractTableRowData {

    private static final long serialVersionUID = 1L;
    public static final String addressId = "addressId";
    public static final String street = "street";
    public static final String poBoxAddress = "poBoxAddress";
    private String m_addressId;
    private String m_street;
    private Boolean m_poBoxAddress;

    public AbstractAddressTableRowData() {
    }

    public String getAddressId() {
      return m_addressId;
    }

    public void setAddressId(String addressId) {
      m_addressId = addressId;
    }

    public String getStreet() {
      return m_street;
    }

    public void setStreet(String street) {
      m_street = street;
    }

    public Boolean getPoBoxAddress() {
      return m_poBoxAddress;
    }

    public void setPoBoxAddress(Boolean poBoxAddress) {
      m_poBoxAddress = poBoxAddress;
    }
  }
}
