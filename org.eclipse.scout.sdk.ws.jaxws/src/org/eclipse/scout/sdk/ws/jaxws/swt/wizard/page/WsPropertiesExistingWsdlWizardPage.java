/*******************************************************************************
 * Copyright (c) 2011 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Wiehl (BSI Business Systems Integration AG) - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.ws.jaxws.swt.wizard.page;

import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.beans.BasicPropertySupport;
import org.eclipse.scout.commons.xmlparser.ScoutXmlDocument;
import org.eclipse.scout.commons.xmlparser.ScoutXmlDocument.ScoutXmlElement;
import org.eclipse.scout.sdk.ScoutSdk;
import org.eclipse.scout.sdk.ui.fields.StyledTextField;
import org.eclipse.scout.sdk.ui.fields.proposal.ContentProposalEvent;
import org.eclipse.scout.sdk.ui.fields.proposal.DefaultProposalProvider;
import org.eclipse.scout.sdk.ui.fields.proposal.IContentProposalEx;
import org.eclipse.scout.sdk.ui.fields.proposal.IProposalAdapterListener;
import org.eclipse.scout.sdk.ui.fields.proposal.ProposalTextField;
import org.eclipse.scout.sdk.ui.wizard.AbstractWorkspaceWizardPage;
import org.eclipse.scout.sdk.workspace.IScoutBundle;
import org.eclipse.scout.sdk.ws.jaxws.JaxWsIcons;
import org.eclipse.scout.sdk.ws.jaxws.JaxWsSdk;
import org.eclipse.scout.sdk.ws.jaxws.Texts;
import org.eclipse.scout.sdk.ws.jaxws.resource.ResourceFactory;
import org.eclipse.scout.sdk.ws.jaxws.swt.model.SunJaxWsBean;
import org.eclipse.scout.sdk.ws.jaxws.util.JaxWsSdkUtility;
import org.eclipse.scout.sdk.ws.jaxws.util.JaxWsSdkUtility.SeparatorType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class WsPropertiesExistingWsdlWizardPage extends AbstractWorkspaceWizardPage {

  public static final String PROP_SERVICE = "service";
  public static final String PROP_PORT = "port";
  public static final String PROP_PORT_TYPE = "portType";
  public static final String PROP_DERIVE_OTHER_NAME = "deriveOtherNames";
  public static final String PROP_ALIAS = "alias";
  public static final String PROP_URL_PATTERN = "urlPattern";
  public static final String PROP_WSDL_DEFINITION = "wsdlDefinition";

  private BasicPropertySupport m_propertySupport;

  private IScoutBundle m_bundle;

  private ProposalTextField m_serviceField;
  private ProposalTextField m_portField;
  private StyledTextField m_portTypeField;
  private Button m_deriveOtherNameButton;
  private StyledTextField m_alias;
  private StyledTextField m_urlPattern;

  private ScoutXmlDocument m_sunJaxWsXml;
  private Set<String> m_illegalAliasNames;
  private Set<String> m_illegalUrlPatterns;

  private WebserviceEnum m_webserviceEnum;

  public WsPropertiesExistingWsdlWizardPage(IScoutBundle bundle, WebserviceEnum webserviceEnum) {
    super(WsPropertiesExistingWsdlWizardPage.class.getName());
    setTitle(Texts.get("ConfigureWebserviceProperties"));
    setDescription(Texts.get("ClickNextToContinue"));

    m_bundle = bundle;
    m_webserviceEnum = webserviceEnum;
    m_propertySupport = new BasicPropertySupport(this);
    m_sunJaxWsXml = ResourceFactory.getSunJaxWsResource(bundle).loadXml();

    setDeriveOtherNames(true);
    loadIllegalValues();
  }

  @Override
  protected void createContent(Composite parent) {
    m_serviceField = new ProposalTextField(parent);
    m_serviceField.setLabelText(Texts.get("Service"));
    m_serviceField.addProposalAdapterListener(new IProposalAdapterListener() {
      @Override
      public void proposalAccepted(ContentProposalEvent event) {
        IContentProposalEx proposal = event.proposal;
        if (proposal != null) {
          setServiceInternal(((P_ServiceProposal) proposal).getService());
        }
        else {
          setServiceInternal(null);
        }
        pingStateChanging();
      }
    });

    m_portField = new ProposalTextField(parent);
    m_portField.setLabelText(Texts.get("Port"));
    m_portField.addProposalAdapterListener(new IProposalAdapterListener() {
      @Override
      public void proposalAccepted(ContentProposalEvent event) {
        IContentProposalEx proposal = event.proposal;
        if (proposal != null) {
          setPortInternal(((P_PortProposal) proposal).getPort());
        }
        else {
          setPortInternal(null);
        }
        pingStateChanging();
      }
    });
    m_portField.setEnabled(false);

    m_portTypeField = getFieldToolkit().createStyledTextField(parent, Texts.get("PortType"));
    m_portTypeField.setEnabled(false);

    if (m_webserviceEnum == WebserviceEnum.Provider) {
      m_deriveOtherNameButton = new Button(parent, SWT.CHECK);
      m_deriveOtherNameButton.setSelection(isDeriveOtherNames());
      m_deriveOtherNameButton.setText(Texts.get("DeriveOtherNamesFormPortTypeChosen"));
      m_deriveOtherNameButton.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
          setDeriveOtherNames(m_deriveOtherNameButton.getSelection());
          if (isDeriveOtherNames()) {
            deriveOtherNames();
          }
        }
      });

      m_alias = getFieldToolkit().createStyledTextField(parent, Texts.get("Alias"));
      m_alias.addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
          setAliasInternal(m_alias.getText());
          pingStateChanging();
        }
      });

      m_urlPattern = getFieldToolkit().createStyledTextField(parent, Texts.get("UrlPattern"));
      String configuredUrlPattern = JaxWsSdkUtility.normalizePath(JaxWsSdkUtility.getJaxWsAlias(m_bundle), SeparatorType.BothType);
      if (configuredUrlPattern != null) {
        m_urlPattern.setReadOnlyPrefix(configuredUrlPattern);
      }
      m_urlPattern.addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
          setUrlPatternInternal(m_urlPattern.getText());
          pingStateChanging();
        }
      });
    }

    // layout
    parent.setLayout(new FormLayout());

    FormData formData = new FormData();
    formData.top = new FormAttachment(0, 0);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    m_serviceField.setLayoutData(formData);

    formData = new FormData();
    formData.top = new FormAttachment(m_serviceField, 5, SWT.BOTTOM);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    m_portField.setLayoutData(formData);

    formData = new FormData();
    formData.top = new FormAttachment(m_portField, 5, SWT.BOTTOM);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    m_portTypeField.setLayoutData(formData);

    if (m_webserviceEnum == WebserviceEnum.Provider) {
      formData = new FormData();
      formData.top = new FormAttachment(m_portTypeField, 5, SWT.BOTTOM);
      formData.left = new FormAttachment(40, 5);
      formData.right = new FormAttachment(100, 0);
      m_deriveOtherNameButton.setLayoutData(formData);

      formData = new FormData();
      formData.top = new FormAttachment(m_deriveOtherNameButton, 30, SWT.BOTTOM);
      formData.left = new FormAttachment(0, 0);
      formData.right = new FormAttachment(100, 0);
      m_alias.setLayoutData(formData);

      formData = new FormData();
      formData.top = new FormAttachment(m_alias, 5, SWT.BOTTOM);
      formData.left = new FormAttachment(0, 0);
      formData.right = new FormAttachment(100, 0);
      m_urlPattern.setLayoutData(formData);
    }
  }

  @Override
  protected void validatePage(MultiStatus multiStatus) {
    if (!isControlCreated()) {
      return;
    }

    if (m_sunJaxWsXml == null || m_sunJaxWsXml.getRoot() == null) {
      multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("XdoesNotExistDueToInexistenceOrCorruptResource", ResourceFactory.getSunJaxWsResource(m_bundle).getFile().getName())));
      return;
    }
    if (getWsdlDefinition() == null) {
      multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("WsdlCouldNotBeParsed")));
      return;
    }

    if (getServiceProposals().length == 0) {
      multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, "WSDL definition must contain one service element at least.\nPlease ensure to have specified the main WSDL file and not an imported, dependent one."));
      return;
    }

    // alias
    if (m_alias != null) {
      if (StringUtility.isNullOrEmpty(getAlias())) {
        multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("XMustNotBeEmpty", m_alias.getLabelText())));
      }
      else if (m_illegalAliasNames.contains(getAlias())) {
        multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("XWithYDoesAlreadyExist", m_alias.getLabelText(), getAlias())));
      }
      else {
        IStatus status = m_bundle.getProject().getWorkspace().validateName(getAlias(), IResource.FILE);
        if (status.isOK() == false) {
          multiStatus.add(status);
        }
      }
    }
    if (m_urlPattern != null) {
      // URL pattern
      String configuredUrlPattern = JaxWsSdkUtility.getJaxWsAlias(m_bundle);
      if (StringUtility.isNullOrEmpty(getUrlPattern()) || getUrlPattern().equals(configuredUrlPattern)) {
        multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("XMustNotBeEmpty", m_urlPattern.getLabelText())));
      }
      else if (!getUrlPattern().startsWith(configuredUrlPattern)) {
        multiStatus.add(new Status(IStatus.WARNING, ScoutSdk.PLUGIN_ID, Texts.get("XshouldStartWithY", m_urlPattern.getLabelText(), configuredUrlPattern)));
      }
      else if (getUrlPattern().matches(".*\\s+.*")) { // check for whitespaces
        multiStatus.add(new Status(IStatus.WARNING, ScoutSdk.PLUGIN_ID, Texts.get("UrlXshouldNotContainWhitespaces", getUrlPattern())));
      }
      else if (m_illegalUrlPatterns.contains(getUrlPattern())) {
        multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("XWithYDoesAlreadyExist", m_urlPattern.getLabelText(), getUrlPattern())));
      }
    }

    if (getService() == null) {
      multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("XMustNotBeEmpty", m_serviceField.getLabelText())));
    }
    else if (getPort() == null) {
      multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("XMustNotBeEmpty", m_portField.getLabelText())));
    }
    else if (getPortType() == null) {
      multiStatus.add(new Status(IStatus.ERROR, ScoutSdk.PLUGIN_ID, Texts.get("XMustNotBeEmpty", m_portTypeField.getLabelText())));
    }
  }

  public Definition getWsdlDefinition() {
    return (Definition) m_propertySupport.getProperty(PROP_WSDL_DEFINITION);
  }

  /**
   * Main input for wizard page
   * 
   * @param wsdlDefinition
   */
  public void setWsdlDefinition(Definition wsdlDefinition) {
    m_propertySupport.setProperty(PROP_WSDL_DEFINITION, wsdlDefinition);

    setStateChanging(true);
    try {
      setService(null);
      setPort(null);
      setPortType(null);

      // reload proposals of service
      P_ServiceProposal[] serviceProposals = getServiceProposals();
      DefaultProposalProvider provider = new DefaultProposalProvider();
      provider.setShortList(serviceProposals);
      m_serviceField.setContentProposalProvider(provider);

      // default proposal for service --> ports are reloaded by setting the service
      if (serviceProposals.length > 0) {
        setService(serviceProposals[0].getService());
      }
      else {
        setService(null);
      }

      m_serviceField.setEnabled(serviceProposals.length > 0);
    }
    finally {
      setStateChanging(false);
    }
  }

  public void setAlias(String alias) {
    try {
      setStateChanging(true);
      setAliasInternal(alias);
      if (isControlCreated() && m_alias != null) {
        m_alias.setText(alias);
      }
    }
    finally {
      setStateChanging(false);
    }
  }

  private void setAliasInternal(String alias) {
    m_propertySupport.setPropertyString(PROP_ALIAS, alias);
  }

  public String getAlias() {
    return m_propertySupport.getPropertyString(PROP_ALIAS);
  }

  public void setUrlPattern(String urlPattern) {
    try {
      setStateChanging(true);
      setUrlPatternInternal(urlPattern);
      if (isControlCreated() && m_urlPattern != null) {
        m_urlPattern.setText(urlPattern);
      }
    }
    finally {
      setStateChanging(false);
    }
  }

  private void setUrlPatternInternal(String urlPattern) {
    m_propertySupport.setPropertyString(PROP_URL_PATTERN, urlPattern);
  }

  public String getUrlPattern() {
    return m_propertySupport.getPropertyString(PROP_URL_PATTERN);
  }

  public void setService(Service service) {
    try {
      setStateChanging(true);
      setServiceInternal(service);

      if (isControlCreated()) {
        if (service != null) {
          m_serviceField.acceptProposal(new P_ServiceProposal(service));
        }
        else {
          m_serviceField.acceptProposal(null);
        }
      }
    }
    finally {
      setStateChanging(false);
    }
  }

  private void setServiceInternal(Service service) {
    m_propertySupport.setProperty(PROP_SERVICE, service);

    // reload proposals of port
    P_PortProposal[] portProposals = getPortProposals();
    DefaultProposalProvider provider = new DefaultProposalProvider();
    provider.setShortList(portProposals);
    if (isControlCreated()) {
      m_portField.setEnabled(service != null);
      m_portField.setContentProposalProvider(provider);
    }

    // default proposal
    if (portProposals.length > 0) {
      setPort(portProposals[0].getPort());
    }
    else {
      setPort(null);
    }
  }

  public Service getService() {
    return (Service) m_propertySupport.getProperty(PROP_SERVICE);
  }

  public void setPort(Port port) {
    try {
      setStateChanging(true);
      setPortInternal(port);
      if (isControlCreated()) {
        if (port != null) {
          m_portField.acceptProposal(new P_PortProposal(port));
        }
        else {
          m_portField.acceptProposal(null);
        }
      }
    }
    finally {
      setStateChanging(false);
    }
  }

  private void setPortInternal(Port port) {
    m_propertySupport.setProperty(PROP_PORT, port);

    // set port type
    if (port != null && port.getBinding() != null && port.getBinding().getPortType() != null) {
      setPortType(port.getBinding().getPortType());
    }
    else {
      setPortType(null);
    }
  }

  public Port getPort() {
    return (Port) m_propertySupport.getProperty(PROP_PORT);
  }

  public void setPortType(PortType portType) {
    try {
      setStateChanging(true);
      setPortTypeInternal(portType);
      if (isControlCreated()) {
        if (portType != null) {
          m_portTypeField.setText(StringUtility.nvl(portType.getQName().getLocalPart(), ""));
          m_portTypeField.setToolTipText(StringUtility.nvl(portType.getQName().toString(), ""));
        }
        else {
          m_portTypeField.setText("");
        }
      }
    }
    finally {
      setStateChanging(false);
    }
  }

  private void setPortTypeInternal(PortType portType) {
    m_propertySupport.setProperty(PROP_PORT_TYPE, portType);
    if (isControlCreated() && m_deriveOtherNameButton != null) {
      m_deriveOtherNameButton.setEnabled(portType != null);
      m_deriveOtherNameButton.setSelection(portType != null);
    }
    deriveOtherNames();
  }

  public PortType getPortType() {
    return (PortType) m_propertySupport.getProperty(PROP_PORT_TYPE);
  }

  public void setDeriveOtherNames(boolean deriveOtherNames) {
    try {
      setStateChanging(true);
      setDeriveOtherNamesInternal(deriveOtherNames);
      if (isControlCreated()) {
        m_deriveOtherNameButton.setSelection(deriveOtherNames);
      }
    }
    finally {
      setStateChanging(false);
    }
  }

  private void setDeriveOtherNamesInternal(boolean deriveOtherNames) {
    m_propertySupport.setPropertyBool(PROP_DERIVE_OTHER_NAME, deriveOtherNames);
  }

  public boolean isDeriveOtherNames() {
    return m_propertySupport.getPropertyBool(PROP_DERIVE_OTHER_NAME);
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    m_propertySupport.addPropertyChangeListener(listener);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    m_propertySupport.removePropertyChangeListener(listener);
  }

  private void loadIllegalValues() {
    Set<String> illegalAliases = new HashSet<String>();
    Set<String> illegalUrlPatterns = new HashSet<String>();

    for (Object xmlSunJaxWs : m_sunJaxWsXml.getRoot().getChildren(StringUtility.join(":", m_sunJaxWsXml.getRoot().getNamePrefix(), SunJaxWsBean.XML_ENDPOINT))) {
      SunJaxWsBean sunJaxWsBean = new SunJaxWsBean((ScoutXmlElement) xmlSunJaxWs);
      illegalAliases.add(sunJaxWsBean.getAlias());
      illegalUrlPatterns.add(sunJaxWsBean.getUrlPattern());
    }

    m_illegalAliasNames = illegalAliases;
    m_illegalUrlPatterns = illegalUrlPatterns;
  }

  private void deriveOtherNames() {
    if (!isDeriveOtherNames()) {
      return;
    }

    String portTypeName = null;
    PortType portType = getPortType();

    if (portType != null) {
      portTypeName = portType.getQName().getLocalPart();
      if (portTypeName != null) {
        portTypeName = JaxWsSdkUtility.getPlainPortTypeName(portTypeName) + "Service";
      }
    }

    setAlias(portTypeName);
    setUrlPattern(portTypeName);
  }

  private P_ServiceProposal[] getServiceProposals() {
    List<P_ServiceProposal> proposals = new LinkedList<P_ServiceProposal>();

    if (getWsdlDefinition() != null) {
      for (Object service : getWsdlDefinition().getServices().values()) {
        proposals.add(new P_ServiceProposal((Service) service));
      }
    }

    return proposals.toArray(new P_ServiceProposal[proposals.size()]);
  }

  private P_PortProposal[] getPortProposals() {
    List<P_PortProposal> proposals = new LinkedList<P_PortProposal>();

    if (getService() != null) {
      for (Object port : getService().getPorts().values()) {
        proposals.add(new P_PortProposal((Port) port));
      }
    }

    return proposals.toArray(new P_PortProposal[proposals.size()]);
  }

  private class P_ServiceProposal implements IContentProposalEx {

    private Service m_service;

    private P_ServiceProposal(Service service) {
      m_service = service;
    }

    @Override
    public int getCursorPosition(boolean selected, boolean expertMode) {
      return 0;
    }

    @Override
    public Image getImage(boolean selected, boolean expertMode) {
      return JaxWsSdk.getImage(JaxWsIcons.Service);
    }

    @Override
    public String getLabel(boolean selected, boolean expertMode) {
      if (selected) {
        return m_service.getQName().toString();
      }
      else {
        return m_service.getQName().getLocalPart();
      }
    }

    public Service getService() {
      return m_service;
    }
  }

  private class P_PortProposal implements IContentProposalEx {

    private Port m_port;

    private P_PortProposal(Port port) {
      m_port = port;
    }

    @Override
    public int getCursorPosition(boolean selected, boolean expertMode) {
      return 0;
    }

    @Override
    public Image getImage(boolean selected, boolean expertMode) {
      return JaxWsSdk.getImage(JaxWsIcons.Port);
    }

    @Override
    public String getLabel(boolean selected, boolean expertMode) {
      if (selected) {
        return "{" + m_port.getBinding().getPortType().getQName().getNamespaceURI() + "}" + m_port.getName();
      }
      else {
        return m_port.getName();
      }
    }

    public Port getPort() {
      return m_port;
    }
  }
}
