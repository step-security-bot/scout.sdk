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
package org.eclipse.scout.sdk.ui.fields.proposal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.scout.commons.CompareUtility;
import org.eclipse.scout.commons.EventListenerList;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.sdk.ScoutIdeProperties;
import org.eclipse.scout.sdk.ui.ScoutSdkUi;
import org.eclipse.scout.sdk.ui.fields.TextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * <h3>ProposalTextField</h3> ...
 */
public class ProposalTextField extends TextField {

  public static int TYPE_INITIAL_SHOW_POPUP = 1 << 1;
  public static int TYPE_SEARCH_EXPERT_MODE = 1 << 2;
  public static int TYPE_NO_LABEL = 1 << 10;
  // public static int TYPE_FIRE_ON_FOCUS_LOST=1 << 14;

  private IContentProposalProvider m_proposalProvider;

  private Button m_popupButton;
  private ProposalPopup m_popup;
  private P_ProposalFieldListener m_proposalFieldListener;
  private IContentProposalEx m_loadingProposal = new ISeparatorProposal() {
    public String getLabel(boolean selected, boolean expertMode) {
      return "Loading...";
    }

    public Image getImage(boolean selected, boolean expertMode) {
      return ScoutSdkUi.getImage(ScoutSdkUi.ToolProgress);
    }

    public int getCursorPosition(boolean selected, boolean expertMode) {
      return 0;
    }
  };
  private P_ProposalLoaderJob m_currentLoader = null;
  private Object lockProposalAdpter = new Object();
  private IContentProposalEx m_selectedProposal = null;

  private IContentProposalEx m_focusGainedProposal = null;
  private IContentProposalEx m_lastFiredProposal = null;
  private P_RequestPattern m_lastRequestPattern;
  private EventListenerList m_eventListeners = new EventListenerList();
  private Lock m_updateLock = new Lock();
  private Lock m_focusLock = new Lock();
  private IProposalPopupListener m_popupListener = new P_PopupListener();
  private int m_type;
  private boolean m_searchExpertMode;

  public ProposalTextField(Composite parent) {
    this(parent, null);
  }

  public ProposalTextField(Composite parent, IContentProposalProvider proposalProvider) {
    this(parent, proposalProvider, 0);
  }

  public ProposalTextField(Composite parent, IContentProposalProvider proposalProvider, int type) {
    super(parent);
    m_type = type;
    setContentProposalProvider(proposalProvider);
    init();
  }

  public void setContentProposalProvider(IContentProposalProvider provider) {
    if (!CompareUtility.equals(provider, m_proposalProvider)) {
      m_lastRequestPattern = null;
      acceptProposal(null);
      m_proposalProvider = provider;
      if (m_proposalProvider == null) {
        detachProposalListener(getTextComponent());
      }
      else {
        attachProposalListener(getTextComponent());
      }
    }
  }

  public IContentProposalProvider getContentProposalProvider() {
    return m_proposalProvider;
  }

  private void attachProposalListener(StyledText textComponent) {
    if (m_proposalFieldListener == null) {
      m_proposalFieldListener = new P_ProposalFieldListener();
      textComponent.addListener(SWT.KeyDown, m_proposalFieldListener);
      textComponent.addListener(SWT.KeyUp, m_proposalFieldListener);
      textComponent.addListener(SWT.Modify, m_proposalFieldListener);
      textComponent.addListener(SWT.FocusOut, m_proposalFieldListener);
      textComponent.addListener(SWT.FocusIn, m_proposalFieldListener);
      textComponent.addListener(SWT.Traverse, m_proposalFieldListener);
      textComponent.addListener(SWT.MouseUp, m_proposalFieldListener);
      textComponent.addListener(SWT.Verify, m_proposalFieldListener);
    }
  }

  private void detachProposalListener(StyledText textComponent) {

    if (m_proposalFieldListener != null) {
      textComponent.removeListener(SWT.KeyDown, m_proposalFieldListener);
      textComponent.removeListener(SWT.KeyUp, m_proposalFieldListener);
      textComponent.removeListener(SWT.Modify, m_proposalFieldListener);
      textComponent.removeListener(SWT.FocusOut, m_proposalFieldListener);
      textComponent.removeListener(SWT.FocusIn, m_proposalFieldListener);
      textComponent.removeListener(SWT.Traverse, m_proposalFieldListener);
      textComponent.removeListener(SWT.MouseUp, m_proposalFieldListener);
      textComponent.removeListener(SWT.Verify, m_proposalFieldListener);
      m_proposalFieldListener = null;
    }
  }

  private void init() {
    Label label = getLabelComponent();
    StyledText text = getTextComponent();
    FormData labelData = (FormData) label.getLayoutData();
    FormData textData = (FormData) text.getLayoutData();
    if ((m_type & TYPE_NO_LABEL) != 0) {
      label.setVisible(false);
      labelData.right = new FormAttachment(0, 0);
      textData.left = new FormAttachment(0, 0);
    }
  }

  @Override
  protected void createContent(Composite parent) {
    super.createContent(parent);

    Label label = getLabelComponent();
    StyledText text = getTextComponent();
    m_popupButton = new Button(parent, SWT.PUSH);
    m_popupButton.setImage(ScoutSdkUi.getImage(ScoutSdkUi.ToolDropdown));
    m_popupButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        try {
          if (m_updateLock.aquire()) {
            if (m_popup != null) {
              m_popup.close();
            }
            else {
              getTextComponent().setSelection(0);
              getTextComponent().setFocus();
              updateProposals();
            }
          }
        }
        finally {
          m_updateLock.release();
        }
      }
    });
    parent.setTabList(new Control[]{text});

    // layout
    parent.setLayout(new FormLayout());
    FormData labelData = new FormData();
    labelData.top = new FormAttachment(0, 0);
    labelData.left = new FormAttachment(0, 0);
    labelData.right = new FormAttachment(40, 0);
    labelData.bottom = new FormAttachment(100, 0);
    label.setLayoutData(labelData);

    FormData textData = new FormData();
    textData.top = new FormAttachment(0, 0);
    textData.left = new FormAttachment(label, 5);
    textData.right = new FormAttachment(m_popupButton, -2);
    textData.bottom = new FormAttachment(100, 0);
    text.setLayoutData(textData);

    FormData buttonData = new FormData(ScoutIdeProperties.TOOL_BUTTON_SIZE, ScoutIdeProperties.TOOL_BUTTON_SIZE);
    buttonData.top = new FormAttachment(0, 0);
    buttonData.right = new FormAttachment(100, 0);
    buttonData.bottom = new FormAttachment(100, 0);
    m_popupButton.setLayoutData(buttonData);
  }

  public void addProposalAdapterListener(IProposalAdapterListener listener) {
    m_eventListeners.add(IProposalAdapterListener.class, listener);
  }

  public void removeProposalAdapterListener(IProposalAdapterListener listener) {
    m_eventListeners.remove(IProposalAdapterListener.class, listener);

  }

  public IContentProposalProvider getProposalProvider() {
    return m_proposalProvider;
  }

  protected P_RequestPattern getLastRequestPattern() {
    return m_lastRequestPattern;
  }

  protected void notifyAcceptProposalUpdateUi(IContentProposalEx proposal) {
    try {
      if (m_updateLock.aquire()) {
        if (proposal != null) {
          getTextComponent().setText(proposal.getLabel(false, m_searchExpertMode));
          getTextComponent().setSelection(proposal.getCursorPosition(m_searchExpertMode, false));
        }
        else {
          getTextComponent().setText("");
          getTextComponent().setSelection(0);
        }
      }
    }
    finally {
      m_updateLock.release();
    }
    m_selectedProposal = proposal;
    notifyAcceptProposal(proposal);
  }

  protected void notifyAcceptProposal(IContentProposalEx proposal) {
    ContentProposalEvent event = new ContentProposalEvent(this);
    event.proposal = proposal;
    if (m_lastRequestPattern == null) {
      event.text = "";
      event.cursorPosition = 0;
    }
    else {
      event.text = m_lastRequestPattern.getSearchText();
      event.cursorPosition = m_lastRequestPattern.getPosition();
    }
    for (IProposalAdapterListener l : m_eventListeners.getListeners(IProposalAdapterListener.class)) {
      l.proposalAccepted(event);
    }
    m_lastFiredProposal = proposal;
  }

  public synchronized void acceptProposal(IContentProposalEx selectedProposal) {

    if (selectedProposal instanceof ISeparatorProposal) {
      return;
    }
    else if (selectedProposal instanceof ICustomProposal) {
      handleCustomProposalSelected((ICustomProposal) selectedProposal);
      return;
    }
    // update ui
    try {
      if (m_updateLock.aquire()) {
        if (selectedProposal == null) {
          getTextComponent().setText("");
          getTextComponent().setSelection(1);
        }
        else {
          getTextComponent().setText(selectedProposal.getLabel(false, m_searchExpertMode));
          getTextComponent().setSelection(selectedProposal.getCursorPosition(m_searchExpertMode, false));
        }
      }
    }
    finally {
      m_updateLock.release();
    }
    if (!CompareUtility.equals(m_selectedProposal, selectedProposal)) {
      m_selectedProposal = selectedProposal;
      notifyAcceptProposal(m_selectedProposal);
    }
    closePopup();
    // if(m_selectedProposal == null){
    // if(selectedProposal == null){
    // return;
    // }
    // else{
    // if(m_updateLock.aquire()){
    // try{
    // getTextComponent().setText(selectedProposal.getLabel(false, m_searchExpertMode));
    // getTextComponent().setSelection(selectedProposal.getCursorPosition(m_searchExpertMode, false));
    // }
    // finally{
    // m_updateLock.release();
    // }
    // }
    // notifyAcceptProposalUpdateUi(selectedProposal);
    //
    // System.out.println("input: " + getText());
    // // if((m_type & TYPE_FIRE_ON_FOCUS_LOST) == 0){
    // notifyAcceptProposal(m_selectedProposal);
    // // }
    // }
    // }
    // else{
    // // update text
    //
    // try{
    // if(m_updateLock.aquire()){
    // String text="";
    // int cusorPosition=0;
    // if(selectedProposal != null){
    // text=selectedProposal.getLabel(m_searchExpertMode, false);
    // cusorPosition=selectedProposal.getCursorPosition(m_searchExpertMode, false);
    // }
    // getTextComponent().setText(text);
    // getTextComponent().setSelection(cusorPosition);
    // }
    // }
    // finally{
    // m_updateLock.release();
    // }
    // if(!m_selectedProposal.equals(selectedProposal)){
    // m_selectedProposal=selectedProposal;
    // // if((m_type & TYPE_FIRE_ON_FOCUS_LOST) == 0){
    // notifyAcceptProposal(m_selectedProposal);
    // // }
    //
    // }
    // }

  }

  /**
   * Overwrite to provide custom proposal handling
   * 
   * @param proposal
   */
  protected void handleCustomProposalSelected(ICustomProposal proposal) {

  }

  @Override
  public void setText(String text) {
    if (m_updateLock.aquire()) {
      try {
        if (text == null) {
          text = "";
        }
        super.setText(text);
      }
      finally {
        m_updateLock.release();
      }
    }
  }

  @Override
  public void setEditable(boolean editable) {
    super.setEditable(editable);
    if (m_popupButton != null && !m_popupButton.isDisposed()) {
      m_popupButton.setEnabled(editable);
    }
  }

  @Override
  public boolean getEditable() {
    boolean editable = super.getEditable();
    if (m_popupButton != null && !m_popupButton.isDisposed()) {
      editable = editable && m_popupButton.getEnabled();
    }
    return editable;
  }

  @Override
  public boolean isEditable() {
    boolean editable = super.isEditable();
    if (m_popupButton != null && !m_popupButton.isDisposed()) {
      editable = editable && m_popupButton.getEnabled();
    }
    return editable;
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (m_popupButton != null && !m_popupButton.isDisposed()) {
      m_popupButton.setEnabled(enabled);
    }
  }

  @Override
  public boolean getEnabled() {
    boolean enabled = super.getEnabled();
    if (m_popupButton != null && !m_popupButton.isDisposed()) {
      enabled = enabled && m_popupButton.getEnabled();
    }
    return enabled;
  }

  @Override
  public boolean isEnabled() {
    boolean enabled = super.isEnabled();
    if (m_popupButton != null && !m_popupButton.isDisposed()) {
      enabled = enabled && m_popupButton.getEnabled();
    }
    return enabled;
  }

  // ########################
  // Popup Handling
  // ########################
  protected synchronized void closePopup() {
    if (m_popup != null) {
      m_popup.close();
    }
  }

  private synchronized void updateProposals() {

    P_RequestPattern searchPattern = new P_RequestPattern(getText(), getSelection().x);
    if (searchPattern.equals(m_lastRequestPattern)) {
      showProposals(m_lastRequestPattern);
      return;
    }
    if (m_currentLoader != null) {
      m_currentLoader.cancel();
    }
    if (m_popup != null) {
      m_popup.setProposals(new IContentProposalEx[]{m_loadingProposal});
    }
    m_currentLoader = new P_ProposalLoaderJob(searchPattern);
    m_currentLoader.schedule();
  }

  private synchronized void textModified() {
    String text = getText();
    int cursorPosition = getSelection().x;
    if (cursorPosition > 0 && m_selectedProposal != null && m_selectedProposal.getLabel(m_searchExpertMode, false).equals(text.substring(0, cursorPosition))) {
      return;
    }
    updateProposals();
  }

  private boolean isProposalFieldFocusOwner() {
    if (m_popup != null) {
      if (m_popup.isFocusOwner()) {
        return true;
      }
      else {
        Shell[] shells = m_popup.getShell().getShells();
        if ((shells != null && shells.length > 0)) {
          return true;
        }
      }
    }
    if (getTextComponent() != null && !getTextComponent().isDisposed()) {
      if (getTextComponent().isFocusControl()) {
        return true;
      }
    }
    if (m_popupButton != null && !m_popupButton.isDisposed()) {
      return m_popupButton.isFocusControl();
    }
    return false;
  }

  private synchronized void showProposals(P_RequestPattern searchPattern) {
    m_lastRequestPattern = searchPattern;
    if (m_lastRequestPattern.getProposals().length == 0) {
      if (m_popup != null) {
        closePopup();
      }
    }
    else {
      if (m_popup == null) {
        openPopup();
      }
      m_popup.setExpertMode(m_searchExpertMode);
      m_popup.setProposals(m_lastRequestPattern.getProposals());
    }
  }

  private synchronized void openPopup() {
    m_popup = new ProposalPopup(getTextComponent(), m_proposalProvider.supportsExpertMode(), m_searchExpertMode);
    m_popup.open();
    m_popup.getShell().addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent event) {
        if (m_popup != null) {
          m_popup.removePopupListener(m_popupListener);
        }
        m_popup = null;
      }
    });
    m_popup.getShell().addShellListener(new ShellAdapter() {
      @Override
      public void shellDeactivated(ShellEvent e) {
        getDisplay().asyncExec(new Runnable() {
          @Override
          public void run() {
            if (!isProposalFieldFocusOwner()) {
              closePopup();
            }
          }
        });
      }
    });
    m_popup.addPopupListener(m_popupListener);
  }

  private boolean equalProposals(IContentProposalEx prop1, IContentProposalEx prop2) {
    if (prop1 == null) {
      return prop2 == null;
    }
    else {
      return prop1.equals(prop2);
    }
  }

  public IContentProposalEx getSelectedProposal() {
    return m_selectedProposal;
  }

  private class P_ProposalLoaderJob extends Job {

    private final P_RequestPattern m_requestPattern;
    private IProgressMonitor m_monitor;

    public P_ProposalLoaderJob(P_RequestPattern requestPattern) {
      super("Load proposals");
      m_requestPattern = requestPattern;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
      m_monitor = monitor;
      IContentProposalEx[] proposals;
      synchronized (lockProposalAdpter) {
        if (m_searchExpertMode) {
          proposals = m_proposalProvider.getProposalsExpertMode(m_requestPattern.getSearchText(),
              m_requestPattern.getPosition(), monitor);
        }
        else {
          proposals = m_proposalProvider.getProposals(m_requestPattern.getSearchText(),
              m_requestPattern.getPosition(), monitor);
        }
      }
      if (m_monitor.isCanceled()) {
        return Status.OK_STATUS;
      }
      m_requestPattern.setProposals(proposals);
      getDisplay().syncExec(new Runnable() {
        public void run() {
          if (m_monitor.isCanceled()) {
            return;
          }
          showProposals(m_requestPattern);
        }
      });
      return Status.OK_STATUS;
    }

    void setCanceled() {
      m_monitor.setCanceled(true);
    }

  } // end P_ProposalLoaderJob

  private class P_ProposalFieldListener implements Listener {
    public void handleEvent(Event event) {
      switch (event.type) {
        case SWT.Modify: {
          if (m_updateLock.aquire()) {
            try {
              // acceptProposal(null);
              textModified();
              // notifyTextModified();
            }
            finally {
              m_updateLock.release();
            }
          }
          break;
        }
        case SWT.Verify: {
          if ("\t".equals(event.text)) {
            event.doit = false;
          }
          break;
        }
        case SWT.KeyUp: {
          switch (event.keyCode) {
            case SWT.ESC:
              closePopup();
              event.doit = false;
              break;
            case SWT.ARROW_DOWN:
              if (m_popup == null) {
                updateProposals();
              }
              else {
                m_popup.setFocus();
              }
              break;
            case SWT.ARROW_LEFT:
            case SWT.ARROW_RIGHT:
              textModified();
              break;
            case 'e': {
              if (event.stateMask == SWT.CONTROL) {
                m_searchExpertMode = !m_searchExpertMode;
                m_lastRequestPattern = null;
                updateProposals();
              }
              break;
            }
            default:
              break;
          }
          break;
        }
        case SWT.FocusOut: {
          // if(m_popup == null){
          // String text=getText();
          // String input="";
          // if(m_selectedProposal != null){
          // input=m_selectedProposal.getLabel(false, m_searchExpertMode);
          // }
          // if(!StringUtility.equalsIgnoreCase(text, input)){
          // acceptProposal(null);
          // }
          // }
          // else{
          getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
              if (m_popupButton != null && !m_popupButton.isDisposed()) {
                if (CompareUtility.equals(getDisplay().getFocusControl(), m_popupButton)) {
                  return;
                }
              }
              if (!isProposalFieldFocusOwner() && !isDisposed()) {
                String text = getText();
                String input = "";
                if (m_selectedProposal != null) {
                  input = m_selectedProposal.getLabel(false, m_searchExpertMode);
                }
                if (!StringUtility.equalsIgnoreCase(text, input)) {
                  acceptProposal(null);
                }
                // notifyAcceptProposal(m_selectedProposal);
                closePopup();
              }
              // if(m_popup != null && !m_popup.isFocusOwner()){
              // }
              // else{
              // String text=getText();
              // String input="";
              // if(m_selectedProposal != null){
              // input=m_selectedProposal.getLabel(false, m_searchExpertMode);
              // }
              // if(!StringUtility.equalsIgnoreCase(text, input)){
              // acceptProposal(null);
              // }
              // // notifyAcceptProposal(m_selectedProposal);
              // }
            }

          });
          // }

          // if((m_type & TYPE_FIRE_ON_FOCUS_LOST) != 0){
          //
          // if(m_popup == null){
          // // focus lost forever
          // if(!equalProposals(m_focusGainedProposal, m_selectedProposal)){
          // notifyAcceptProposal(m_selectedProposal);
          // }
          // }
          // else{
          // // check if next focus owner is the popup
          // getDisplay().asyncExec(new Runnable(){
          // public void run(){
          // if(m_popup != null && !m_popup.isFocusOwner()){
          // if(!equalProposals(m_focusGainedProposal, m_selectedProposal)){
          // notifyAcceptProposal(m_selectedProposal);
          // }
          // m_popup.close();
          // }
          // }
          // });
          // }
          // }
          // else{
          // getDisplay().asyncExec(new Runnable(){
          // public void run(){
          // if(m_popup != null && !m_popup.isFocusOwner()){
          // m_popup.close();
          // }
          // }
          // });
          // }
          break;
        } // end FocusOut
        case SWT.FocusIn: {
          if (m_focusLock.aquire()) {
            try {
              m_focusGainedProposal = m_selectedProposal;
            }
            finally {
              m_focusLock.release();
            }
          }
          if ((m_type & TYPE_INITIAL_SHOW_POPUP) != 0) {
            updateProposals();
          }
          break;
        } // end FocusIn
        case SWT.MouseUp: {
          if (getText().length() > 0) textModified();
          break;
        } // end MouseUp
        case SWT.Traverse: {
          switch (event.keyCode) {
            case SWT.ESC:
              if (m_popup != null) {
                event.doit = false;
              }
              break;
            case SWT.CR:
              if (m_popup != null) {

                acceptProposal(m_popup.getSelectedProposal());
                event.doit = false;
              }
              break;
            case SWT.TAB:
            case SWT.LF:
              if (m_popup != null) {
                event.doit = false;
                m_popup.setFocus();
              }
              break;
            default:
              break;
          }
          break;
        } // end Traverse
        default:
          break;
      }

    }
  } // end class P_ProposalFieldListener

  protected class P_RequestPattern {
    private final int m_position;
    private final String m_searchText;
    private IContentProposalEx[] m_proposals;

    public P_RequestPattern(String text, int position) {
      m_searchText = text;
      m_position = position;
    }

    public int getPosition() {
      return m_position;
    }

    public String getSearchText() {
      return m_searchText;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof P_RequestPattern)) {
        return false;
      }
      P_RequestPattern toCheck = (P_RequestPattern) obj;
      return ((getPosition() == toCheck.getPosition()) && (getSearchText().equals(toCheck.getSearchText())));
    }

    public IContentProposalEx[] getProposals() {
      return m_proposals;
    }

    public void setProposals(IContentProposalEx[] proposals) {
      m_proposals = proposals;
    }
  } // end class P_RequestPattern

  private class P_PopupListener implements IProposalPopupListener {
    public void popupChanged(ProposalPopupEvent event) {
      switch (event.getType()) {
        case ProposalPopupEvent.TYPE_PROPOSAL_ACCEPTED:
          try {
            m_focusLock.aquire();
            acceptProposal((IContentProposalEx) event.getData(ProposalPopupEvent.IDENTIFIER_SELECTED_PROPOSAL));
            getTextComponent().traverse(SWT.TRAVERSE_TAB_NEXT);
          }
          finally {
            m_focusLock.release();
          }
          break;
        case ProposalPopupEvent.TYPE_SEARCH_SHORTENED:
          m_searchExpertMode = ((Boolean) event.getData(ProposalPopupEvent.IDENTIFIER_SELECTION_SEARCH_SHORTENED)).booleanValue();
          m_lastRequestPattern = null;
          updateProposals();
          break;
        case ProposalPopupEvent.TYPE_POPUP_CLOSED:
          if (m_focusLock.aquire()) {
            try {
              if (!CompareUtility.equals(m_lastFiredProposal, m_selectedProposal)) {
                // if(m_selectedProposal != null){
                // notifyAcceptProposal(m_selectedProposal);
                // }
              }
              m_popup.close();

            }
            finally {
              m_focusLock.release();
            }
          }
          break;
        default:
          break;
      }
    }
  } // end class P_PopupListener

}
