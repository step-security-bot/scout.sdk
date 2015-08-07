package formdata.client.ui.forms;

import org.eclipse.scout.commons.annotations.FormData;
import org.eclipse.scout.commons.annotations.FormData.SdkCommand;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.integerfield.AbstractIntegerField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;

import formdata.client.ui.forms.FormWithGroupBoxesForm.MainBox.FlatStringField;
import formdata.client.ui.forms.FormWithGroupBoxesForm.MainBox.InnerBox;
import formdata.client.ui.forms.FormWithGroupBoxesForm.MainBox.InnerBox.IgnoredIntegerField;
import formdata.client.ui.forms.FormWithGroupBoxesForm.MainBox.InnerBox.InnerIntegerField;
import formdata.shared.services.process.FormWithGroupBoxesFormData;

@FormData(value = FormWithGroupBoxesFormData.class, sdkCommand = SdkCommand.CREATE)
public class FormWithGroupBoxesForm extends AbstractForm {

  public FormWithGroupBoxesForm() throws ProcessingException {
    super();

  }

  public FlatStringField getFlatStringField() {
    return getFieldByClass(FlatStringField.class);
  }

  public IgnoredIntegerField getIgnoredIntegerField() {
    return getFieldByClass(IgnoredIntegerField.class);
  }

  public InnerBox getInnerBox() {
    return getFieldByClass(InnerBox.class);
  }

  public InnerIntegerField getInnerIntegerField() {
    return getFieldByClass(InnerIntegerField.class);
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  @Order(10.0)
  public class MainBox extends AbstractGroupBox {

    @Order(10.0)
    public class FlatStringField extends AbstractStringField {
    }

    @Order(20.0)
    public class InnerBox extends AbstractGroupBox {

      @Order(10.0)
      public class InnerIntegerField extends AbstractIntegerField {
      }

      @Order(20.0)
      @FormData(sdkCommand = SdkCommand.IGNORE)
      public class IgnoredIntegerField extends AbstractIntegerField {
      }
    }
  }
}
