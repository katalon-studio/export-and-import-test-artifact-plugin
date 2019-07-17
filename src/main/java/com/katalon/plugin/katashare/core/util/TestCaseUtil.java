package com.katalon.plugin.katashare.core.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.TestCaseEntity;

public class TestCaseUtil {

    public static String getTestCaseRootFolder(ProjectEntity project) {
        return project.getFolderLocation() + File.separator + "Test Cases";
    }
    
    public static String getTestScriptRootFolder(ProjectEntity project) {
        return project.getFolderLocation() + File.separator + "Scripts";
    }
    
    public static String getTestCaseParentRelativePath(ProjectEntity project, TestCaseEntity testCase) {
        String testCaseRootFolderLocation = getTestCaseRootFolder(project);
        String testCaseFolderLocation = testCase.getFolderLocation();
        if (testCaseFolderLocation.equals(testCaseRootFolderLocation)) {
            return StringUtils.EMPTY;
        } else {
            String parentRelativePath = testCaseFolderLocation
                    .substring((testCaseRootFolderLocation + File.separator).length());
            return parentRelativePath;
        }
    }
    
    public static String getTestScriptParentRelativePath(ProjectEntity project, TestCaseEntity testCase) {
        String testScriptRootFolderLocation = getTestScriptRootFolder(project);
        String testScriptLocation = testCase.getScriptFile().getAbsolutePath();
        String testScriptParentPath = FilenameUtils.getFullPath(testScriptLocation);
        String testScriptParentRelativePath = testScriptParentPath
                .substring((testScriptRootFolderLocation + File.separator).length());
        return testScriptParentRelativePath;
    }
}
