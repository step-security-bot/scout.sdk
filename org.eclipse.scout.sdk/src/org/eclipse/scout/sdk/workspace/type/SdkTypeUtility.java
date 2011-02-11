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
package org.eclipse.scout.sdk.workspace.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.nls.sdk.model.workspace.project.INlsProject;
import org.eclipse.scout.sdk.RuntimeClasses;
import org.eclipse.scout.sdk.ScoutSdk;
import org.eclipse.scout.sdk.icon.IIconProvider;
import org.eclipse.scout.sdk.internal.typestructure.StructuredType;
import org.eclipse.scout.sdk.util.ScoutSignature;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.workspace.IScoutProject;
import org.eclipse.scout.sdk.workspace.type.IStructuredType.CATEGORIES;
import org.eclipse.scout.sdk.workspace.type.config.ConfigurationMethod;
import org.eclipse.scout.sdk.workspace.typecache.ICachedTypeHierarchy;
import org.eclipse.scout.sdk.workspace.typecache.ITypeHierarchy;

/**
 *
 */
public class SdkTypeUtility {
  static final IType iFormField = ScoutSdk.getType(RuntimeClasses.IFormField);
  static final IType iValueField = ScoutSdk.getType(RuntimeClasses.IValueField);
  static final IType iCompositeField = ScoutSdk.getType(RuntimeClasses.ICompositeField);
  private static SdkTypeUtility instance = new SdkTypeUtility();

  private SdkTypeUtility() {
  }

  public static IScoutBundle getScoutBundle(IJavaElement element) {
    return ScoutSdk.getScoutWorkspace().getScoutBundle(element.getJavaProject().getProject());
  }

  public static IScoutProject getScoutProject(IJavaElement element) {
    return ScoutSdk.getScoutWorkspace().getScoutBundle(element.getJavaProject().getProject()).getScoutProject();
  }

  public static INlsProject findNlsProject(IJavaElement element) {
    IScoutBundle scoutBundle = getScoutBundle(element);
    if (scoutBundle != null) {
      return scoutBundle.findBestMatchNlsProject();
    }
    return null;
  }

  public static IIconProvider findIconProvider(IJavaElement element) {
    IScoutBundle scoutBundle = getScoutBundle(element);
    if (scoutBundle != null) {
      return scoutBundle.findBestMatchIconProvider();
    }
    return null;
  }

  public static IType[] getPotentialMasterFields(IType field) {
    ITypeHierarchy hierarchy = ScoutSdk.getLocalTypeHierarchy(field.getCompilationUnit());
    IType mainbox = TypeUtility.getAncestor(field, TypeFilters.getRegexSimpleNameFilter("MainBox"));
    TreeSet<IType> collector = new TreeSet<IType>(TypeComparators.getTypeNameComparator());
    if (TypeUtility.exists(mainbox)) {
      instance.collectPotentialMasterFields(mainbox, collector, hierarchy);
    }
    collector.remove(field);
    return collector.toArray(new IType[collector.size()]);

  }

  private void collectPotentialMasterFields(IType type, Set<IType> collector, ITypeHierarchy formFieldHierarchy) {
    if (TypeUtility.exists(type)) {
      if (formFieldHierarchy.isSubtype(iValueField, type)) {
        collector.add(type);
      }
      for (IType subType : TypeUtility.getInnerTypes(type)) {
        collectPotentialMasterFields(subType, collector, formFieldHierarchy);
      }
    }
  }

  public static IType[] getInnerTypes(IType declaringType, IType superType, Comparator<IType> comparator) {
    if (TypeUtility.exists(declaringType)) {
      ITypeHierarchy typeHierarchy = ScoutSdk.getLocalTypeHierarchy(declaringType);
      return TypeUtility.getInnerTypes(declaringType, TypeFilters.getSubtypeFilter(superType, typeHierarchy), comparator);
    }
    return new IType[0];
  }

  public static IType[] getInnerTypes(IType declaringType, IType superType, ITypeHierarchy hierarchy, Comparator<IType> comparator) {
    if (TypeUtility.exists(declaringType)) {
      return TypeUtility.getInnerTypes(declaringType, TypeFilters.getSubtypeFilter(superType, hierarchy), comparator);
    }
    return new IType[0];
  }

  public static IType[] getAllTypes(ICompilationUnit icu, ITypeFilter filter) {
    Collection<IType> result = new ArrayList<IType>();
    try {
      for (IType t : icu.getTypes()) {
        instance.collectTypesTypes(t, result, filter);
      }
    }
    catch (JavaModelException e) {
      ScoutSdk.logError("could not get types of '" + icu.getElementName() + "'.", e);
    }
    return result.toArray(new IType[result.size()]);
  }

  private void collectTypesTypes(IType type, Collection<IType> result, ITypeFilter filter) {
    if (filter.accept(type)) {
      result.add(type);
    }
    try {
      for (IType t : type.getTypes()) {
        collectTypesTypes(t, result, filter);
      }
    }
    catch (JavaModelException e) {
      ScoutSdk.logError("could not get inner types of '" + type.getFullyQualifiedName() + "'.", e);
    }
  }

  public static IType[] getFormFields(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IFormField), TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getFormFields(IType declaringType, ITypeHierarchy hierarchy) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IFormField), hierarchy, TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getFormFieldsWithoutButtons(IType declaringType) {
    return getFormFieldsWithoutButtons(declaringType, ScoutSdk.getLocalTypeHierarchy(declaringType));
  }

  public static IType[] getFormFieldsWithoutButtons(IType declaringType, ITypeHierarchy hierarchy) {
    ITypeFilter notButtonFilter = TypeFilters.invertFilter(TypeFilters.getSubtypeFilter(ScoutSdk.getType(RuntimeClasses.IButton), hierarchy));
    ITypeFilter formFieldFilter = TypeFilters.getSubtypeFilter(ScoutSdk.getType(RuntimeClasses.IFormField), hierarchy);
    return TypeUtility.getInnerTypes(declaringType, TypeFilters.getMultiTypeFilter(formFieldFilter, notButtonFilter));
  }

  public static IType[] getTrees(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.ITree), TypeComparators.getTypeNameComparator());
  }

  public static IType[] getTables(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.ITable), TypeComparators.getTypeNameComparator());
  }

  public static IType[] getColumns(IType table) {
    return getInnerTypes(table, ScoutSdk.getType(RuntimeClasses.IColumn), TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getCodes(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.ICode), TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getKeyStrokes(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IKeyStroke), TypeComparators.getTypeNameComparator());
  }

  public static IType[] getToolbuttons(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IToolButton), TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getWizardSteps(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IWizardStep), TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getCalendar(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.ICalendar), TypeComparators.getTypeNameComparator());
  }

  public static IType[] getCalendarItemProviders(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.ICalendarItemProvider), TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getMenus(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IMenu), TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getButtons(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IButton), TypeComparators.getOrderAnnotationComparator());
  }

  public static IType[] getComposerEntities(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IComposerEntity), TypeComparators.getTypeNameComparator());
  }

  public static IType[] getComposerAttributes(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IComposerAttribute), TypeComparators.getTypeNameComparator());
  }

  public static IType[] getFormFieldData(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.AbstractFormFieldData), TypeComparators.getTypeNameComparator());
  }

  public static IType[] getFormHandlers(IType declaringType) {
    return getInnerTypes(declaringType, ScoutSdk.getType(RuntimeClasses.IFormHandler), TypeComparators.getTypeNameComparator());
  }

  public static IType[] getServiceImplementations(IType serviceInterface) {
    return getServiceImplementations(serviceInterface, null);
  }

  public static IType[] getServiceImplementations(IType serviceInterface, ITypeFilter filter) {
    ICachedTypeHierarchy serviceHierarchy = ScoutSdk.getPrimaryTypeHierarchy(ScoutSdk.getType(RuntimeClasses.IService));
    ITypeFilter serviceImplFilter = null;
    if (filter == null) {
      serviceImplFilter = TypeFilters.getMultiTypeFilter(
          TypeFilters.getExistingFilter(),
          TypeFilters.getClassFilter());
    }
    else {
      serviceImplFilter = TypeFilters.getMultiTypeFilter(
          TypeFilters.getExistingFilter(),
              TypeFilters.getClassFilter(),
              filter);
    }
    return serviceHierarchy.getAllSubtypes(serviceInterface, serviceImplFilter);
  }

  public static IType getServiceInterface(IType service) {
    ICachedTypeHierarchy serviceHierarchy = ScoutSdk.getPrimaryTypeHierarchy(ScoutSdk.getType(RuntimeClasses.IService));
    IType[] interfaces = serviceHierarchy.getSuperInterfaces(service, TypeFilters.getElementNameFilter("I" + service.getElementName()));
    if (interfaces.length > 0) {
      return interfaces[0];
    }
    return null;
  }

  public static IType[] getAbstractTypesOnClasspath(IType superType, IJavaProject project) {
    ICachedTypeHierarchy typeHierarchy = ScoutSdk.getPrimaryTypeHierarchy(superType);
    IType[] abstractTypes = typeHierarchy.getAllSubtypes(superType, TypeFilters.getAbstractOnClasspath(project), TypeComparators.getTypeNameComparator());
    return abstractTypes;
  }

  public static IType[] getClassesOnClasspath(IType superType, IJavaProject project) {
    ICachedTypeHierarchy typeHierarchy = ScoutSdk.getPrimaryTypeHierarchy(superType);
    ITypeFilter filter = TypeFilters.getMultiTypeFilter(
        TypeFilters.getTypesOnClasspath(project),
        TypeFilters.getClassFilter());
    IType[] classes = typeHierarchy.getAllSubtypes(superType, filter, TypeComparators.getTypeNameComparator());
    return classes;
  }

  public static IMethod getFormFieldGetterMethod(final IType formField) {
    ITypeHierarchy hierarchy = ScoutSdk.getLocalTypeHierarchy(formField.getCompilationUnit());
    return getFormFieldGetterMethod(formField, hierarchy);
  }

  public static IMethod getFormFieldGetterMethod(final IType formField, ITypeHierarchy hierarchy) {
    IType form = TypeUtility.getAncestor(formField, TypeFilters.getMultiTypeFilterOr(
        TypeFilters.getSubtypeFilter(ScoutSdk.getType(RuntimeClasses.IForm), hierarchy),
        TypeFilters.getToplevelTypeFilter()));

    if (TypeUtility.exists(form)) {

      final String formFieldSignature = Signature.createTypeSignature(formField.getFullyQualifiedName(), true);
      final String regex = "^get" + formField.getElementName();
      IMethod method = TypeUtility.getFirstMethod(form, new IMethodFilter() {
        @Override
        public boolean accept(IMethod candidate) {
          if (candidate.getElementName().matches(regex)) {
            try {
              String returnTypeSignature = Signature.getReturnType(candidate.getSignature());
              returnTypeSignature = ScoutSignature.getResolvedSignature(returnTypeSignature, candidate.getDeclaringType());
              return ScoutSignature.isEqualSignature(formFieldSignature, returnTypeSignature);
            }
            catch (JavaModelException e) {
              ScoutSdk.logError("could not parse signature of method '" + candidate.getElementName() + "' in type '" + candidate.getDeclaringType().getFullyQualifiedName() + "'.", e);
              return false;
            }
          }
          return false;
        }
      });
      return method;
    }
    return null;
  }

  public static IMethod getColumnGetterMethod(IType column) {
    IType table = column.getDeclaringType();
    final String formFieldSignature = Signature.createTypeSignature(column.getFullyQualifiedName(), true).replaceAll("\\$", ".");

    final String regex = "^get" + column.getElementName();
    IMethod method = TypeUtility.getFirstMethod(table, new IMethodFilter() {
      @Override
      public boolean accept(IMethod candidate) {
        if (candidate.getElementName().matches(regex)) {
          try {
            String returnTypeSignature = Signature.getReturnType(candidate.getSignature());
            returnTypeSignature = ScoutSignature.getResolvedSignature(returnTypeSignature, candidate.getDeclaringType());
            return formFieldSignature.equals(returnTypeSignature);
          }
          catch (JavaModelException e) {
            ScoutSdk.logError("could not parse signature of method '" + candidate.getElementName() + "' in type '" + candidate.getDeclaringType().getFullyQualifiedName() + "'.", e);
            return false;
          }
        }
        return false;
      }
    });
    return method;
  }

  public static IMethod getWizardStepGetterMethod(IType wizardStep) {
    IType wizard = wizardStep.getDeclaringType();
    final String formFieldSignature = Signature.createTypeSignature(wizardStep.getFullyQualifiedName(), true);
    final String regex = "^get" + wizardStep.getElementName();
    IMethod method = TypeUtility.getFirstMethod(wizard, new IMethodFilter() {
      @Override
      public boolean accept(IMethod candidate) {
        if (candidate.getElementName().matches(regex)) {
          try {
            String returnTypeSignature = Signature.getReturnType(candidate.getSignature());
            returnTypeSignature = ScoutSignature.getResolvedSignature(returnTypeSignature, candidate.getDeclaringType());
            return formFieldSignature.equals(returnTypeSignature);
          }
          catch (JavaModelException e) {
            ScoutSdk.logError("could not parse signature of method '" + candidate.getElementName() + "' in type '" + candidate.getDeclaringType().getFullyQualifiedName() + "'.", e);
            return false;
          }
        }
        return false;
      }
    });
    return method;
  }

  public static ConfigurationMethod getConfigurationMethod(IType declaringType, String methodName) {
    org.eclipse.jdt.core.ITypeHierarchy superTypeHierarchy = null;
    try {
      superTypeHierarchy = declaringType.newSupertypeHierarchy(null);
    }
    catch (JavaModelException e) {
      ScoutSdk.logWarning("could not build super type hierarchy for '" + declaringType.getFullyQualifiedName() + "'", e);
      return null;
    }
    return getConfigurationMethod(declaringType, methodName, superTypeHierarchy);

  }

  public static ConfigurationMethod getConfigurationMethod(IType declaringType, String methodName, org.eclipse.jdt.core.ITypeHierarchy superTypeHierarchy) {
    ArrayList<IType> affectedTypes = new ArrayList<IType>();
    IType[] superClasses = superTypeHierarchy.getAllSuperclasses(declaringType);
    for (IType t : superClasses) {
      if (TypeUtility.exists(t) && !t.getFullyQualifiedName().equals(Object.class.getName())) {
        affectedTypes.add(0, t);
      }
    }
    affectedTypes.add(declaringType);
    return getConfigurationMethod(declaringType, methodName, superTypeHierarchy, affectedTypes.toArray(new IType[affectedTypes.size()]));
  }

  public static ConfigurationMethod getConfigurationMethod(IType declaringType, String methodName, org.eclipse.jdt.core.ITypeHierarchy superTypeHierarchy, IType[] topDownAffectedTypes) {
    ConfigurationMethod newMethod = null;
    try {
      for (IType t : topDownAffectedTypes) {
        IMethod m = TypeUtility.getMethod(t, methodName);
        if (TypeUtility.exists(m)) {
          if (newMethod != null) {
            newMethod.pushMethod(m);
          }
          else {
            IAnnotation configOpAnnotation = TypeUtility.getAnnotation(m, RuntimeClasses.ConfigOperation);
            if (TypeUtility.exists(configOpAnnotation)) {
              newMethod = new ConfigurationMethod(declaringType, superTypeHierarchy, methodName, ConfigurationMethod.OPERATION_METHOD);
              newMethod.pushMethod(m);
            }
            IAnnotation configPropAnnotation = TypeUtility.getAnnotation(m, RuntimeClasses.ConfigProperty);
            if (TypeUtility.exists(configPropAnnotation)) {
              String configPropertyType = null;
              for (IMemberValuePair p : configPropAnnotation.getMemberValuePairs()) {
                if ("value".equals(p.getMemberName())) {
                  configPropertyType = (String) p.getValue();
                  break;
                }
              }
              if (!StringUtility.isNullOrEmpty(configPropertyType)) {
                newMethod = new ConfigurationMethod(declaringType, superTypeHierarchy, methodName, ConfigurationMethod.PROPERTY_METHOD);
                newMethod.setConfigAnnotationType(configPropertyType);
                newMethod.pushMethod(m);
              }
            }
          }
        }
      }
    }
    catch (JavaModelException e) {
      ScoutSdk.logError("could not build ConfigPropertyType for '" + methodName + "' in type '" + declaringType.getFullyQualifiedName() + "'.", e);
    }
    return newMethod;
  }

  public static IType getFistProcessButton(IType declaringType, ITypeHierarchy hierarchy) {
    Pattern returnTrueMatcher = Pattern.compile("return\\s*true", Pattern.MULTILINE);
    ITypeFilter buttonFilter = TypeFilters.getSubtypeFilter(ScoutSdk.getType(RuntimeClasses.IButton), hierarchy);
    for (IType field : getFormFields(declaringType, hierarchy)) {
      if (buttonFilter.accept(field)) {
        IMethod m = TypeUtility.getMethod(field, "getConfiguredProcessButton");
        if (!TypeUtility.exists(m)) {
          return field;
        }
        else {
          try {
            if (returnTrueMatcher.matcher(field.getSource()).find()) {
              return field;
            }
          }
          catch (JavaModelException e) {
            ScoutSdk.logError("could not get source of '" + m.getElementName() + "' on '" + m.getDeclaringType().getFullyQualifiedName() + "'.", e);
          }
        }

      }
    }
    return null;
  }

  public static IStructuredType createStructuredType(IType type) {
    try {
      org.eclipse.jdt.core.ITypeHierarchy supertypeHierarchy = type.newSupertypeHierarchy(null);
      if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.ICompositeField))) {
        return createStructuredCompositeField(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.ITableField))) {
        return createStructuredTableField(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.ITreeField))) {
        return createStructuredTreeField(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IComposerField))) {
        return createStructuredComposer(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IComposerAttribute))) {
        return createStructuredComposer(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IComposerEntity))) {
        return createStructuredComposer(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IFormField))) {
        return createStructuredFormField(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IForm))) {
        return createStructuredForm(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.ICalendar))) {
        return createStructuredCalendar(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.ICodeType))) {
        return createStructuredCodeType(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.ICode))) {
        return createStructuredCode(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IDesktop))) {
        return createStructuredDesktop(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IOutline))) {
        return createStructuredOutline(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IPageWithNodes))) {
        return createStructuredPageWithNodes(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IPageWithTable))) {
        return createStructuredPageWithTable(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.ITable))) {
        return createStructuredTable(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IWizard))) {
        return createStructuredWizard(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IWizardStep))) {
        return createStructuredWizardStep(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IMenu))) {
        return createStructuredMenu(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IColumn))) {
        return createStructuredColumn(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IActivityMap))) {
        return createStructuredActivityMap(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IFormHandler))) {
        return createStructuredFormHandler(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IKeyStroke))) {
        return createStructuredKeyStroke(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IButton))) {
        return createStructuredButton(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IViewButton))) {
        return createStructuredViewButton(type);
      }
      else if (supertypeHierarchy.contains(ScoutSdk.getType(RuntimeClasses.IToolButton))) {
        return createStructuredToolButton(type);
      }
      else {
        ScoutSdk.logInfo("potential performance leek, no structured type defined for type '" + type.getFullyQualifiedName() + "'.");
        return createUnknownStructuredType(type);
      }

    }
    catch (JavaModelException e) {
      ScoutSdk.logError("could not create structured type for '" + type.getFullyQualifiedName() + "'.", e);
      return null;
    }
  }

  /**
   * don not hang on this object.
   * 
   * @param type
   * @return
   */
  private static IStructuredType createUnknownStructuredType(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_FORM_DATA_BEAN,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_START_HANDLER,
          CATEGORIES.METHOD_INNER_TYPE_GETTER,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_FORM_FIELD,
          CATEGORIES.TYPE_COLUMN,
          CATEGORIES.TYPE_CODE,
          CATEGORIES.TYPE_FORM,
          CATEGORIES.TYPE_TABLE,
          CATEGORIES.TYPE_ACTIVITY_MAP,
          CATEGORIES.TYPE_TREE,
          CATEGORIES.TYPE_CALENDAR,
          CATEGORIES.TYPE_CALENDAR_ITEM_PROVIDER,
          CATEGORIES.TYPE_WIZARD,
          CATEGORIES.TYPE_WIZARD_STEP,
          CATEGORIES.TYPE_MENU,
          CATEGORIES.TYPE_VIEW_BUTTON,
          CATEGORIES.TYPE_TOOL_BUTTON,
          CATEGORIES.TYPE_KEYSTROKE,
          CATEGORIES.TYPE_COMPOSER_ATTRIBUTE,
          CATEGORIES.TYPE_COMPOSER_ENTRY,
          CATEGORIES.TYPE_FORM_HANDLER,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredButton(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredViewButton(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredToolButton(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_TOOL_BUTTON,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredKeyStroke(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
        CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredMenu(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_INNER_TYPE_GETTER,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_MENU,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredColumn(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredActivityMap(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_MENU,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredDesktop(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
        CATEGORIES.FIELD_LOGGER,
        CATEGORIES.FIELD_STATIC,
        CATEGORIES.FIELD_MEMBER,
        CATEGORIES.FIELD_UNKNOWN,
        CATEGORIES.METHOD_CONSTRUCTOR,
        CATEGORIES.METHOD_CONFIG_PROPERTY,
        CATEGORIES.METHOD_CONFIG_EXEC,
        CATEGORIES.METHOD_OVERRIDDEN,
        CATEGORIES.METHOD_LOCAL_BEAN,
        CATEGORIES.METHOD_UNCATEGORIZED,
        CATEGORIES.TYPE_MENU,
        CATEGORIES.TYPE_VIEW_BUTTON,
        CATEGORIES.TYPE_TOOL_BUTTON,
        CATEGORIES.TYPE_KEYSTROKE,
        CATEGORIES.TYPE_UNCATEGORIZED
        );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredFormHandler(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
         CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredForm(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_FORM_DATA_BEAN,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_START_HANDLER,
          CATEGORIES.METHOD_INNER_TYPE_GETTER,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_FORM_FIELD,
          CATEGORIES.TYPE_KEYSTROKE,
          CATEGORIES.TYPE_FORM_HANDLER,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredOutline(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredFormField(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredComposer(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_FORM_DATA_BEAN,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_COMPOSER_ATTRIBUTE,
          CATEGORIES.TYPE_COMPOSER_ENTRY,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredPageWithNodes(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_INNER_TYPE_GETTER,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_MENU,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredPageWithTable(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_TABLE,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredTableField(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_TABLE,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredTable(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
        CATEGORIES.FIELD_LOGGER,
        CATEGORIES.FIELD_STATIC,
        CATEGORIES.FIELD_MEMBER,
        CATEGORIES.FIELD_UNKNOWN,
        CATEGORIES.METHOD_CONSTRUCTOR,
        CATEGORIES.METHOD_CONFIG_PROPERTY,
        CATEGORIES.METHOD_CONFIG_EXEC,
        CATEGORIES.METHOD_OVERRIDDEN,
        CATEGORIES.METHOD_LOCAL_BEAN,
        CATEGORIES.METHOD_UNCATEGORIZED,
        CATEGORIES.TYPE_MENU,
        CATEGORIES.TYPE_COLUMN,
        CATEGORIES.TYPE_UNCATEGORIZED
        );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredCompositeField(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_FORM_FIELD,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredCodeType(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_CODE,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredCode(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_CODE,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredTreeField(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_TREE,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredWizard(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_INNER_TYPE_GETTER,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_WIZARD_STEP,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredWizardStep(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_INNER_TYPE_GETTER,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }

  public static IStructuredType createStructuredCalendar(IType type) {
    EnumSet<CATEGORIES> enabled = EnumSet.of(
          CATEGORIES.FIELD_LOGGER,
          CATEGORIES.FIELD_STATIC,
          CATEGORIES.FIELD_MEMBER,
          CATEGORIES.FIELD_UNKNOWN,
          CATEGORIES.METHOD_CONSTRUCTOR,
          CATEGORIES.METHOD_CONFIG_PROPERTY,
          CATEGORIES.METHOD_CONFIG_EXEC,
          CATEGORIES.METHOD_OVERRIDDEN,
          CATEGORIES.METHOD_INNER_TYPE_GETTER,
          CATEGORIES.METHOD_LOCAL_BEAN,
          CATEGORIES.METHOD_UNCATEGORIZED,
          CATEGORIES.TYPE_CALENDAR_ITEM_PROVIDER,
          CATEGORIES.TYPE_UNCATEGORIZED
          );
    return new StructuredType(type, enabled);
  }
}
