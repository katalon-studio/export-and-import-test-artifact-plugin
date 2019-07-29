package com.katalon.plugin.katashare.composer.toolbar.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.ui.UISynchronizeService;
import com.katalon.plugin.katashare.composer.toolbar.dialog.ImportTestArtifactDialog;
import com.katalon.plugin.katashare.composer.toolbar.dialog.ImportTestArtifactDialog.ImportTestArtifactDialogResult;
import com.katalon.plugin.katashare.constant.StringConstants;
import com.katalon.plugin.katashare.core.FileCompressionException;
import com.katalon.plugin.katashare.core.TestArtifactScriptRefactor;
import com.katalon.plugin.katashare.core.util.EntityUtil;
import com.katalon.plugin.katashare.core.util.FileUtil;
import com.katalon.plugin.katashare.core.util.PlatformUtil;
import com.katalon.plugin.katashare.core.util.TestCaseUtil;
import com.katalon.plugin.katashare.core.util.ZipUtil;

import ch.qos.logback.classic.Logger;

public class ImportTestArtifactHandler {

    private Logger logger = (Logger) LoggerFactory.getLogger(ImportTestArtifactHandler.class);

    private Shell activeShell;

    public ImportTestArtifactHandler(Shell shell) {
        this.activeShell = shell;
    }
    public void execute() {
        ImportTestArtifactDialog dialog = new ImportTestArtifactDialog(activeShell);
        if (dialog.open() == Window.OK) {
            ImportTestArtifactDialogResult result = dialog.getResult();
            String importFileLocation = result.getImportFileLocation();
            String testCaseImportLocation = result.getTestCaseImportLocation();
            String testObjectImportLocation = result.getTestObjectImportLocation();
            try {
                importTestArtifacts(importFileLocation, testCaseImportLocation, testObjectImportLocation);
            } catch (Exception e) {
                MessageDialog.openError(activeShell, StringConstants.ERROR,
                        StringConstants.MSG_UNABLE_TO_IMPORT_TEST_ARTIFACTS);
                logger.error(StringConstants.MSG_UNABLE_TO_IMPORT_TEST_ARTIFACTS, e);
            }
        }
    }

    private void importTestArtifacts(String importFileLocation, String testCaseImportLocation,
            String testObjectImportLocation) throws IOException, FileCompressionException {
        File importFile = new File(importFileLocation);
        if (!importFile.exists()) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, StringConstants.MSG_INVALID_IMPORT_FILE);
            return;
        }

        if (!testCaseImportLocation.startsWith("Test Cases")) {
            MessageDialog.openError(activeShell, StringConstants.ERROR,
                    StringConstants.MSG_INVALID_TEST_CASE_IMPORT_LOCATION);
            return;
        }

        if (!testObjectImportLocation.startsWith("Object Repository")) {
            MessageDialog.openError(activeShell, StringConstants.ERROR,
                    StringConstants.MSG_INVALID_TEST_OBJECT_IMPORT_LOCATION);
            return;
        }

        Job importArtifactsJob = new Job("Importing test artifacts...") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    File tempFolder = Files.createTempDirectory("import-test-artifacts-").toFile();
                    ZipUtil.extractAll(importFile, tempFolder);
    
                    if (!FileUtil.isEmptyFolder(tempFolder)) {
                        File sourceFolder = tempFolder.listFiles()[0];
                        if (sourceFolder.isDirectory()) {
                            File testCaseImportFolder = null;
                            File testScriptImportFolder = null;
                            File testObjectImportFolder = null;
    
                            testCaseImportFolder = importTestCases(sourceFolder, testCaseImportLocation);
    
                            if (testCaseImportFolder != null) {
                                testScriptImportFolder = importTestScripts(sourceFolder, testCaseImportFolder);
                            }
    
                            testObjectImportFolder = importTestObjects(sourceFolder, testObjectImportLocation);
    
                            if (testObjectImportFolder != null && testScriptImportFolder != null) {
                                Map<String, String> testObjectIdLookup = collectTestObjectIds(testObjectImportFolder);
                                List<File> scriptFiles = FileUtil.listFilesWithExtension(testScriptImportFolder, "groovy");
                                TestArtifactScriptRefactor refactor = TestArtifactScriptRefactor
                                        .createForTestObjectEntity(testObjectIdLookup);
                                refactor.updateReferences(scriptFiles);
                            }
    
                            if (testCaseImportFolder != null && testScriptImportFolder != null) {
                                Map<String, String> testCaseIdLookup = collectTestCaseIds(testCaseImportFolder);
                                List<File> scriptFiles = FileUtil.listFilesWithExtension(testScriptImportFolder, "groovy");
                                TestArtifactScriptRefactor refactor = TestArtifactScriptRefactor
                                        .createForTestCaseEntity(testCaseIdLookup);
                                refactor.updateReferences(scriptFiles);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return new Status(Status.ERROR, "com.katalon.plugin.katashare", "Error importing test artifacts",
                            e);
                }
                return Status.OK_STATUS;
            }
        };
        
        importArtifactsJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (importArtifactsJob.getResult().isOK()) {
                    PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                        MessageDialog.openInformation(activeShell, StringConstants.INFO,
                                StringConstants.MSG_TEST_ARTIFACTS_IMPORTED_SUCCESSFULLY);
                    });
                }
            }
        });
        
        importArtifactsJob.setUser(true);
        importArtifactsJob.schedule();
    }

    private File importTestCases(File sourceFolder, String testCaseImportLocation) throws IOException {
        File sharedTestCaseFolder = new File(sourceFolder, "shared-test-cases");
        if (!FileUtil.isEmptyFolder(sharedTestCaseFolder)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();

            String importFolderRelativePath = StringUtils.replace(testCaseImportLocation, EntityUtil.getEntityIdSeparator(),
                    File.separator);
            File importFolder = new File(project.getFolderLocation(), importFolderRelativePath);

            FileUtils.copyDirectory(sharedTestCaseFolder, importFolder);

            return importFolder;
        } else {
            return null;
        }
    }

    private File importTestScripts(File sourceFolder, File testCaseImportFolder) throws IOException {
        File sharedTestScriptFolder = new File(sourceFolder, "shared-test-scripts");
        if (!FileUtil.isEmptyFolder(sharedTestScriptFolder)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();

            String importFolderRelativePath = testCaseImportFolder.getAbsolutePath()
                    .substring(TestCaseUtil.getTestCaseRootFolder(project).length());
            String importFolderLocation = TestCaseUtil.getTestScriptRootFolder(project) + importFolderRelativePath;
            Files.createDirectories(Paths.get(importFolderLocation));
            File importFolder = new File(importFolderLocation);

            FileUtils.copyDirectory(sharedTestScriptFolder, importFolder);

            return importFolder;
        } else {
            return null;
        }
    }

    private File importTestObjects(File sourceFolder, String testObjectImportLocation) throws IOException {
        File sharedTestObjectFolder = new File(sourceFolder, "shared-test-objects");
        if (!FileUtil.isEmptyFolder(sharedTestObjectFolder)) {
            ProjectEntity project = PlatformUtil.getCurrentProject();

            String importFolderRelativePath = StringUtils.replace(testObjectImportLocation, EntityUtil.getEntityIdSeparator(),
                    File.separator);
            File importFolder = new File(project.getFolderLocation(), importFolderRelativePath);

            FileUtils.copyDirectory(sharedTestObjectFolder, importFolder);

            return importFolder;
        } else {
            return null;
        }
    }

    private Map<String, String> collectTestObjectIds(File testObjectImportFolder) throws IOException {
        ProjectEntity project = PlatformUtil.getCurrentProject();
        Map<String, String> testObjectIdLookup = new HashMap<>();
        Files.walk(Paths.get(testObjectImportFolder.getAbsolutePath())).filter(
                p -> Files.isRegularFile(p) && FilenameUtils.getExtension(p.toFile().getAbsolutePath()).equals("rs"))
                .forEach(p -> {
                    String path = p.toFile().getAbsolutePath();
                    String pathWithoutExtension = FilenameUtils.removeExtension(path);
                    String newRelativeId = pathWithoutExtension
                            .substring((project.getFolderLocation() + File.separator).length());
                    newRelativeId = StringUtils.replace(newRelativeId, File.separator,
                            EntityUtil.getEntityIdSeparator());
                    String oldRelativeId = "Object Repository" + File.separator + pathWithoutExtension
                            .substring((testObjectImportFolder.getAbsolutePath() + File.separator).length());
                    oldRelativeId = StringUtils.replace(oldRelativeId, File.separator,
                            EntityUtil.getEntityIdSeparator());
                    testObjectIdLookup.put(oldRelativeId, newRelativeId);
                });
        return testObjectIdLookup;
    }

    private Map<String, String> collectTestCaseIds(File testCaseImportFolder) throws IOException {
        ProjectEntity project = PlatformUtil.getCurrentProject();
        Map<String, String> testCaseIdLookup = new HashMap<>();
        Files.walk(Paths.get(testCaseImportFolder.getAbsolutePath())).filter(
                p -> Files.isRegularFile(p) && FilenameUtils.getExtension(p.toFile().getAbsolutePath()).equals("tc"))
                .forEach(p -> {
                    String path = p.toFile().getAbsolutePath();
                    String pathWithoutExtension = FilenameUtils.removeExtension(path);
                    String newRelativeId = pathWithoutExtension
                            .substring((project.getFolderLocation() + File.separator).length());
                    newRelativeId = StringUtils.replace(newRelativeId, File.separator,
                            EntityUtil.getEntityIdSeparator());
                    String oldRelativeId = "Test Cases" + File.separator + pathWithoutExtension
                            .substring((testCaseImportFolder.getAbsolutePath() + File.separator).length());
                    oldRelativeId = StringUtils.replace(oldRelativeId, File.separator,
                            EntityUtil.getEntityIdSeparator());
                    testCaseIdLookup.put(oldRelativeId, newRelativeId);
                });
        return testCaseIdLookup;
    }
}
