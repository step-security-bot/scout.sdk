package formdata.client.ui.template.formfield.replace.levels;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.platform.Replace;
import org.eclipse.scout.rt.platform.exception.ProcessingException;

import formdata.shared.ui.template.formfield.replace.levels.ThirdLevelFormData;

@FormData(value = ThirdLevelFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class ThirdLevelForm extends SecondLevelForm {

  public ThirdLevelForm() throws ProcessingException {
    super();
  }

  @Replace
  public class ThirdInnerBox extends SecondInnerBox {
    public ThirdInnerBox(FirstLevelForm.MainBox m) {
      super(m);
    }

    @Replace
    public class ThirdLevel extends SecondLevelForm.SecondInnerBox.SecondLevel {
      public ThirdLevel(FirstLevelForm.MainBox.FirstInnerBox m) {
        super(m);
      }
    }
  }
}
