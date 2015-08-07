package formdata.client.ui.forms;

import org.eclipse.scout.commons.annotations.FormData;
import org.eclipse.scout.commons.annotations.FormData.SdkCommand;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.listbox.AbstractListBox;

import formdata.client.ui.forms.ListBoxForm.MainBox.ListBoxField;
import formdata.shared.services.process.ListBoxFormData;

@FormData(value = ListBoxFormData.class, sdkCommand = SdkCommand.CREATE)
public class ListBoxForm extends AbstractForm {

  public ListBoxForm() throws ProcessingException {
    super();
  }

  public ListBoxField getListBoxField() {
    return getFieldByClass(ListBoxField.class);
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  @Order(10.0)
  public class MainBox extends AbstractGroupBox {

    @Order(10.0)
    public class ListBoxField extends AbstractListBox<Long> {
    }
  }
}
