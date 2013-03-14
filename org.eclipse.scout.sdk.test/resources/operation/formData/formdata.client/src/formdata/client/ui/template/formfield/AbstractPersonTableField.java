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
package formdata.client.ui.template.formfield;

import org.eclipse.scout.commons.annotations.FormData;
import org.eclipse.scout.commons.annotations.FormData.SdkCommand;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;

import formdata.shared.services.process.AbstractPersonTableFieldData;

@FormData(value = AbstractPersonTableFieldData.class, sdkCommand = SdkCommand.CREATE)
public abstract class AbstractPersonTableField extends AbstractBeanTableField<AbstractPersonTableField.Table> {

  public class Table extends AbstractTable {

    public PersonIdColumn getAddressIdColumn() {
      return getColumnSet().getColumnByClass(PersonIdColumn.class);
    }

    public NameColumn getStreetColumn() {
      return getColumnSet().getColumnByClass(NameColumn.class);
    }

    public FemaleColumn getPoBoxAddressColumn() {
      return getColumnSet().getColumnByClass(FemaleColumn.class);
    }

    @Order(10)
    public class PersonIdColumn extends AbstractStringColumn {
    }

    @Order(20)
    public class NameColumn extends AbstractStringColumn {
    }

    @Order(30)
    public class FemaleColumn extends AbstractBooleanColumn {
    }
  }
}
