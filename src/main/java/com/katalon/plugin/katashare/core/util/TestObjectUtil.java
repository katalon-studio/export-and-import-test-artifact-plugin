package com.katalon.plugin.katashare.core.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.TestObjectEntity;

public class TestObjectUtil {

    public static String getTestObjectRootFolder(ProjectEntity project) {
        return project.getFolderLocation() + File.separator + "Object Repository";
    }
    
    public static String getTestObjectParentRelativePath(ProjectEntity project, TestObjectEntity testObject) {
        String testObjectRootFolderLocation = getTestObjectRootFolder(project);
        String testObjectFolderLocation = testObject.getFolderLocation();
        if (testObjectFolderLocation.equals(testObjectRootFolderLocation)) {
            return StringUtils.EMPTY;
        } else {
            String parentRelativePath = testObjectFolderLocation
                    .substring((testObjectRootFolderLocation + File.separator).length());
            return parentRelativePath;
        }
    }
}
