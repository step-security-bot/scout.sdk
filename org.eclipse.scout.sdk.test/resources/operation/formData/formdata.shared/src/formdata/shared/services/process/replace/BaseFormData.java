package formdata.shared.services.process.replace;

import org.eclipse.scout.rt.shared.data.form.ValidationRule;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;

public class BaseFormData extends AbstractFormData {
  private static final long serialVersionUID = 1L;

  public BaseFormData() {
  }

  public Lookup getLookup() {
    return getFieldByClass(Lookup.class);
  }

  public Name getName() {
    return getFieldByClass(Name.class);
  }

  public SdkCommandCreate getSdkCommandCreate() {
    return getFieldByClass(SdkCommandCreate.class);
  }

  public SdkCommandNone getSdkCommandNone() {
    return getFieldByClass(SdkCommandNone.class);
  }

  public SdkCommandUse getSdkCommandUse() {
    return getFieldByClass(SdkCommandUse.class);
  }

  public Smart getSmart() {
    return getFieldByClass(Smart.class);
  }

  public static class Lookup extends AbstractValueFieldData<Long> {
    private static final long serialVersionUID = 1L;

    public Lookup() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(java.util.Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.LOOKUP_CALL, TestingLookupCall.class);
      ruleMap.put(ValidationRule.ZERO_NULL_EQUALITY, true);
    }
  }

  public static class Name extends AbstractValueFieldData<String> {
    private static final long serialVersionUID = 1L;

    public Name() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(java.util.Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.MANDATORY, true);
      ruleMap.put(ValidationRule.MAX_LENGTH, 60);
    }
  }

  public static class SdkCommandCreate extends AbstractValueFieldData<String> {
    private static final long serialVersionUID = 1L;

    public SdkCommandCreate() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(java.util.Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.MAX_LENGTH, 4000);
    }
  }

  public static class SdkCommandNone extends AbstractValueFieldData<String> {
    private static final long serialVersionUID = 1L;

    public SdkCommandNone() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(java.util.Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.MAX_LENGTH, 4000);
    }
  }

  public static class SdkCommandUse extends UsingFormFieldData {
    private static final long serialVersionUID = 1L;

    public SdkCommandUse() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(java.util.Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.MAX_LENGTH, 4000);
    }
  }

  public static class Smart extends AbstractValueFieldData<Long> {
    private static final long serialVersionUID = 1L;

    public Smart() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(java.util.Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.CODE_TYPE, TestingCodeType.class);
      ruleMap.put(ValidationRule.ZERO_NULL_EQUALITY, true);
    }
  }
}
