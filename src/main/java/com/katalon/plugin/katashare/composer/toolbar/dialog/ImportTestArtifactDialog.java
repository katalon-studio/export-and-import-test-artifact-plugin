package com.katalon.plugin.katashare.composer.toolbar.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.model.FolderEntity;
import com.katalon.platform.api.ui.DialogActionService;
import com.katalon.plugin.katashare.constant.StringConstants;
import com.katalon.plugin.katashare.core.util.PlatformUtil;

public class ImportTestArtifactDialog extends Dialog {
    
    private Text txtImportFile;
    
    private Button btnBrowseImportFile;
    
    private Text txtTestCaseImportLocation;
    
    private Button btnBrowseTestCaseImportLocation;
    
    private Text txtTestObjectImportLocation;
    
    private Button btnBrowseTestObjectImportLocation;

    private ImportTestArtifactDialogResult dialogResult;
    
    public ImportTestArtifactDialog(Shell parentShell) {
        super(parentShell);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridLayout glBody = new GridLayout(3, false);
        body.setLayout(glBody);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 500;
        body.setLayoutData(gdBody);
        
        Label lblChooseImportFile = new Label(body, SWT.NONE);
        lblChooseImportFile.setText(StringConstants.LBL_CHOOSE_IMPORT_FILE);
        
        txtImportFile = new Text(body, SWT.BORDER);
        txtImportFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        btnBrowseImportFile = new Button(body, SWT.NONE);
        btnBrowseImportFile.setText(StringConstants.BTN_BROWSE);
        
        Label lblChooseTestCaseImportLocation = new Label(body, SWT.NONE);
        lblChooseTestCaseImportLocation.setText(StringConstants.LBL_CHOOSE_TEST_CASE_IMPORT_LOCATION);
        
        txtTestCaseImportLocation = new Text(body, SWT.BORDER);
        txtTestCaseImportLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        btnBrowseTestCaseImportLocation = new Button(body, SWT.NONE);
        btnBrowseTestCaseImportLocation.setText(StringConstants.BTN_BROWSE);
        
        Label lblChooseTestObjectImportLocation = new Label(body, SWT.NONE);
        lblChooseTestObjectImportLocation.setText(StringConstants.LBL_CHOOSE_TEST_OBJECT_IMPORT_LOCATION);;
        
        txtTestObjectImportLocation = new Text(body, SWT.BORDER);
        txtTestObjectImportLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        btnBrowseTestObjectImportLocation = new Button(body, SWT.NONE);
        btnBrowseTestObjectImportLocation.setText(StringConstants.BTN_BROWSE);
        
        registerControlListeners();
        
        return body;
    };
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
    
    private void registerControlListeners() {
        txtImportFile.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                validateInput();
            }
        });
        
        btnBrowseImportFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell());
                fileDialog.setFilterExtensions(new String[] {"*.zip"});
                String importFileLocation = fileDialog.open();
                txtImportFile.setText(importFileLocation);
                validateInput();
            }
        });
        
        txtTestCaseImportLocation.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                validateInput();
            }
        });
        
        btnBrowseTestCaseImportLocation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell activeShell = e.widget.getDisplay().getActiveShell();
                try {
                    FolderEntity selectedFolder = PlatformUtil.getUIService(DialogActionService.class)
                            .showTestCaseFolderSelectionDialog(activeShell, "Select test case folder");
                    String selectedFolderId = selectedFolder.getId();
                    txtTestCaseImportLocation.setText(selectedFolderId);
                } catch (PlatformException ex) {
                    MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
                }
                validateInput();
            }
        });
        
        txtTestObjectImportLocation.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                validateInput();
            }
        });
        
        btnBrowseTestObjectImportLocation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell activeShell = e.widget.getDisplay().getActiveShell();
                try {
                    FolderEntity selectedFolder = PlatformUtil.getUIService(DialogActionService.class)
                            .showTestObjectFolderSelectionDialog(activeShell, "Select test object folder");
                    String selectedFolderId = selectedFolder.getId();
                    txtTestObjectImportLocation.setText(selectedFolderId);
                } catch (PlatformException ex) {
                    MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
                }
                validateInput();
            }
        });
    }
    
    private void validateInput() {
        boolean isValid = StringUtils.isNotBlank(txtImportFile.getText())
                && StringUtils.isNotBlank(txtTestCaseImportLocation.getText())
                && StringUtils.isNotBlank(txtTestObjectImportLocation.getText());
        getButton(IDialogConstants.OK_ID).setEnabled(isValid);
    }
    
    @Override
    protected void okPressed() {
        dialogResult = new ImportTestArtifactDialogResult();
        dialogResult.setImportFileLocation(txtImportFile.getText());
        dialogResult.setTestCaseImportLocation(txtTestCaseImportLocation.getText());
        dialogResult.setTestObjectImportLocation(txtTestObjectImportLocation.getText());
        super.okPressed();
    }
    
    public ImportTestArtifactDialogResult getResult() {
        return dialogResult;
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    public class ImportTestArtifactDialogResult {
        private String importFileLocation;
        
        private String testCaseImportLocation;
        
        private String testObjectImportLocation;

        public String getImportFileLocation() {
            return importFileLocation;
        }

        public void setImportFileLocation(String importFileLocation) {
            this.importFileLocation = importFileLocation;
        }

        public String getTestCaseImportLocation() {
            return testCaseImportLocation;
        }

        public void setTestCaseImportLocation(String testCaseImportLocation) {
            this.testCaseImportLocation = testCaseImportLocation;
        }

        public String getTestObjectImportLocation() {
            return testObjectImportLocation;
        }

        public void setTestObjectImportLocation(String testObjectImportLocation) {
            this.testObjectImportLocation = testObjectImportLocation;
        }
    }
}
