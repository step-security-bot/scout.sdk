package formdata.shared.services.process;

import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;

public class ListBoxFormData extends AbstractFormData {
  private static final long serialVersionUID = 1L;

  public ListBoxFormData() {
  }

  public ListBox getListBox() {
    return getFieldByClass(ListBox.class);
  }

  public static class ListBox extends AbstractValueFieldData<Long[]> {
    private static final long serialVersionUID = 1L;

    public ListBox() {
    }
  }
}
