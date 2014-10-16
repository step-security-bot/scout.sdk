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
package org.eclipse.scout.sdk.ui.wizard.form.fields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.scout.commons.CompositeObject;
import org.eclipse.scout.commons.annotations.ScoutSdkIgnore;
import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.extensions.runtime.classes.IRuntimeClasses;
import org.eclipse.scout.sdk.ui.executor.selection.ScoutStructuredSelection;
import org.eclipse.scout.sdk.ui.extensions.AbstractInnerTypeWizard;
import org.eclipse.scout.sdk.ui.extensions.IFormFieldExtension;
import org.eclipse.scout.sdk.ui.fields.proposal.SiblingProposal;
import org.eclipse.scout.sdk.ui.fields.table.FilteredTable;
import org.eclipse.scout.sdk.ui.fields.table.ISeparator;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.internal.extensions.FormFieldExtensionPoint;
import org.eclipse.scout.sdk.ui.wizard.AbstractScoutWizardPage;
import org.eclipse.scout.sdk.ui.wizard.AbstractWorkspaceWizardPage;
import org.eclipse.scout.sdk.util.jdt.JdtUtility;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * <h3>FormFieldSelectionWizardPage</h3>
 *
 * @author Andreas Hoegger
 * @since 1.0.8 02.03.2010
 */
public class FormFieldSelectionWizardPage extends AbstractWorkspaceWizardPage {

  private final IType m_declaringType;
  private AbstractScoutWizardPage m_nextPage;
  private Object m_currentSelection;
  private Set<IType> m_modelTypeShortList;
  private String m_typeName;
  private IJavaElement m_sibling;

  // ui fields
  private FilteredTable m_table;

  /**
   * @param pageName
   */
  public FormFieldSelectionWizardPage(IType declaringType) {
    super(FormFieldSelectionWizardPage.class.getName());
    m_declaringType = declaringType;
    setTitle(Texts.get("FormField"));
    setDescription(Texts.get("FormFieldDesc"));
  }

  @Override
  protected void createContent(Composite parent) {
    m_modelTypeShortList = new HashSet<IType>();

    IType iFormField = TypeUtility.getType(IRuntimeClasses.IFormField);
    Set<IType> abstractFormFields = TypeUtility.getAbstractTypesOnClasspath(iFormField, m_declaringType.getJavaProject(), TypeFilters.getPrimaryTypeFilter());

    ArrayList<Object> elements = new ArrayList<Object>(abstractFormFields.size() + 1);
    elements.add(new ISeparator() {
    });

    ITypeHierarchy abstractFormFieldHierarchy = TypeUtility.getLocalTypeHierarchy(abstractFormFields);
    for (IType formField : abstractFormFields) {
      if (!TypeUtility.exists(JdtUtility.getAnnotation(formField, ScoutSdkIgnore.class.getName()))) {
        IFormFieldExtension formFieldExtension = FormFieldExtensionPoint.findExtension(formField, 1, abstractFormFieldHierarchy);
        if (formFieldExtension != null && formFieldExtension.isInShortList()) {
          m_modelTypeShortList.add(formField);
        }
        elements.add(formField);
      }
    }

    // ui
    m_table = new FilteredTable(parent, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
    m_table.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        if (m_currentSelection != null) {
          m_table.getViewer().update(m_currentSelection, new String[]{"label"});
        }
        m_currentSelection = null;
        if (!event.getSelection().isEmpty()) {
          m_currentSelection = ((StructuredSelection) event.getSelection()).getFirstElement();
        }
        if (m_currentSelection != null) {
          m_table.getViewer().update(m_currentSelection, new String[]{"label"});
        }
        handleSelection(m_currentSelection);
      }
    });

    m_table.getViewer().addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent event) {
        Object selectedItem = null;
        if (!event.getSelection().isEmpty()) {
          StructuredSelection selection = (StructuredSelection) event.getSelection();
          selectedItem = selection.getFirstElement();
        }
        handleSelection(selectedItem);
        IWizardPage page = getNextPage();
        if (page == null) {
          // something must have happened getting the next page
          return;
        }

        // show the next page
        IWizardContainer container = getWizard().getContainer();
        if (container != null) {
          container.showPage(page);
        }
      }
    });

    P_ContentProvider provider = new P_ContentProvider(elements.toArray());
    m_table.getViewer().setLabelProvider(provider);
    m_table.getViewer().setContentProvider(provider);
    m_table.getViewer().setSorter(new P_TableSorter());
    m_table.getViewer().setInput(provider);

    // layout
    parent.setLayout(new GridLayout(1, true));
    GridData tableData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
    tableData.heightHint = 150;
    m_table.setLayoutData(tableData);
  }

  private void handleSelection(Object selectedItem) {
    AbstractInnerTypeWizard wizard = null;
    if (selectedItem instanceof IType) {
      IType formField = (IType) selectedItem;
      wizard = (AbstractInnerTypeWizard) FormFieldExtensionPoint.createNewWizard(formField);
      if (wizard != null) {
        ScoutStructuredSelection sel = new ScoutStructuredSelection(new Object[]{m_declaringType});
        sel.setSuperType(formField);
        sel.setTypeName(getTypeName());
        IJavaElement sibling = getSibling();
        if (TypeUtility.exists(sibling)) {
          sel.setSibling(new SiblingProposal(sibling));
        }
        wizard.init(PlatformUI.getWorkbench(), sel);
      }
    }
    if (wizard != null) {
      m_nextPage = (AbstractScoutWizardPage) wizard.getPages()[0];
    }
    else {
      m_nextPage = null;
    }
    revalidate();
  }

  @Override
  protected void validatePage(MultiStatus multiStatus) {
    multiStatus.add(getStatusFieldList());
  }

  private IStatus getStatusFieldList() {
    if (m_nextPage == null) {
      return new Status(IStatus.ERROR, ScoutSdkUi.PLUGIN_ID, Texts.get("NoFieldSelected"));
    }
    return Status.OK_STATUS;
  }

  @Override
  public IWizardPage getNextPage() {
    return m_nextPage;
  }

  public String getTypeName() {
    return m_typeName;
  }

  public void setTypeName(String typeName) {
    m_typeName = typeName;
  }

  public IJavaElement getSibling() {
    return m_sibling;
  }

  public void setSibling(IJavaElement sibling) {
    m_sibling = sibling;
  }

  private class P_ContentProvider implements IStructuredContentProvider, ITableLabelProvider {

    private Object[] m_elements;

    public P_ContentProvider(Object[] elements) {
      m_elements = elements;
    }

    @Override
    public Object[] getElements(Object inputElement) {
      return m_elements;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      if (columnIndex == 0) {
        if (element instanceof ISeparator) {
          return ScoutSdkUi.getImage(ScoutSdkUi.Separator);
        }
        return ScoutSdkUi.getImage(ScoutSdkUi.FormField);
      }
      return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (columnIndex == 0) {
        StringBuilder label = new StringBuilder();
        if (element instanceof ISeparator) {
          return "------------------ more fields ------------------";
        }
        StructuredSelection selection = (StructuredSelection) m_table.getViewer().getSelection();

        IType t = (IType) element;
        String typeName = t.getElementName();
        if (typeName.toLowerCase().startsWith("abstract")) {
          typeName = typeName.substring("abstract".length());
        }
        label.append(typeName);
        if (selection.toList().contains(element)) {
          label.append(" - ").append(t.getFullyQualifiedName());
        }
        return label.toString();
      }
      return null;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public void addListener(ILabelProviderListener listener) {

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
      return "label".equals(property);
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }
  } // end class P_ByClassProvider

  private class P_TableSorter extends ViewerSorter {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {

      CompositeObject comp1;
      if (e1 instanceof ISeparator) {
        comp1 = new CompositeObject(2);
      }
      else {
        IType modelType = (IType) e1;
        if (m_modelTypeShortList.contains(modelType)) {
          comp1 = new CompositeObject(1, modelType.getElementName(), modelType.getFullyQualifiedName());
        }
        else {
          comp1 = new CompositeObject(3, modelType.getElementName(), modelType.getFullyQualifiedName());
        }
      }

      CompositeObject comp2;
      if (e2 instanceof ISeparator) {
        comp2 = new CompositeObject(2);
      }
      else {
        IType modelType = (IType) e2;
        if (m_modelTypeShortList.contains(modelType)) {
          comp2 = new CompositeObject(1, modelType.getElementName(), modelType.getFullyQualifiedName());
        }
        else {
          comp2 = new CompositeObject(3, modelType.getElementName(), modelType.getFullyQualifiedName());
        }
      }

      return comp1.compareTo(comp2);
    }
  }

}
