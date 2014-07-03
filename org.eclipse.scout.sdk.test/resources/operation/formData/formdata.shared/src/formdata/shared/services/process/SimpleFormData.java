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

import java.util.Map;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.ValidationRule;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;
import org.eclipse.scout.rt.shared.data.form.fields.composer.AbstractComposerData;
import org.eclipse.scout.rt.shared.data.form.properties.AbstractPropertyData;

import formdata.shared.IFormDataInterface02;
import formdata.shared.IFormDataInterface03;
import formdata.shared.TestRunnable;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 * 
 * @generated
 */
@Generated(value = "org.eclipse.scout.sdk.workspace.dto.formdata.FormDataDtoUpdateOperation", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SimpleFormData extends AbstractFormData implements IFormDataInterface02, IFormDataInterface03 {

  private static final long serialVersionUID = 1L;

  public SimpleFormData() {
  }

  public Date getDate() {
    return getFieldByClass(Date.class);
  }

  public Double getDouble() {
    return getFieldByClass(Double.class);
  }

  public MultiTypeArgsBox getMultiTypeArgsBox() {
    return getFieldByClass(MultiTypeArgsBox.class);
  }

  @Override
  public SampleComposer getSampleComposer() {
    return getFieldByClass(SampleComposer.class);
  }

  public SampleDate getSampleDate() {
    return getFieldByClass(SampleDate.class);
  }

  public SampleSmart getSampleSmart() {
    return getFieldByClass(SampleSmart.class);
  }

  public SampleString getSampleString() {
    return getFieldByClass(SampleString.class);
  }

  /**
   * access method for property SimpleNr.
   */
  public Long getSimpleNr() {
    return getSimpleNrProperty().getValue();
  }

  /**
   * access method for property SimpleNr.
   */
  public void setSimpleNr(Long simpleNr) {
    getSimpleNrProperty().setValue(simpleNr);
  }

  public SimpleNrProperty getSimpleNrProperty() {
    return getPropertyByClass(SimpleNrProperty.class);
  }

  public static class Date extends AbstractValueFieldData<Integer> {

    private static final long serialVersionUID = 1L;

    public Date() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.MAX_VALUE, Integer.MAX_VALUE);
      ruleMap.put(ValidationRule.MIN_VALUE, Integer.MIN_VALUE);
    }
  }

  public static class Double extends AbstractValueFieldData<java.lang.Double> {

    private static final long serialVersionUID = 1L;

    public Double() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.MAX_VALUE, -java.lang.Double.MAX_VALUE);
      ruleMap.put(ValidationRule.MIN_VALUE, 0.0);
    }
  }

  public static class MultiTypeArgsBox extends AbstractValueFieldData<TestRunnable> {

    private static final long serialVersionUID = 1L;

    public MultiTypeArgsBox() {
    }
  }

  public static class SampleComposer extends AbstractComposerData {

    private static final long serialVersionUID = 1L;

    public SampleComposer() {
    }
  }

  public static class SampleDate extends AbstractValueFieldData<java.util.Date> {

    private static final long serialVersionUID = 1L;

    public SampleDate() {
    }
  }

  public static class SampleSmart extends AbstractValueFieldData<Long> {

    private static final long serialVersionUID = 1L;

    public SampleSmart() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.ZERO_NULL_EQUALITY, true);
    }
  }

  public static class SampleString extends AbstractValueFieldData<String> {

    private static final long serialVersionUID = 1L;

    public SampleString() {
    }

    /**
     * list of derived validation rules.
     */
    @Override
    protected void initValidationRules(Map<String, Object> ruleMap) {
      super.initValidationRules(ruleMap);
      ruleMap.put(ValidationRule.MAX_LENGTH, 4000);
    }
  }

  public static class SimpleNrProperty extends AbstractPropertyData<Long> {

    private static final long serialVersionUID = 1L;

    public SimpleNrProperty() {
    }
  }
}
