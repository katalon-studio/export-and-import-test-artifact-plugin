package com.katalon.plugin.katashare.core.util;

import java.io.File;

import com.katalon.platform.api.model.ProjectEntity;

public class ProfileUtil {

    public static String getProfileRootFolder(ProjectEntity project) {
        return project.getFolderLocation() + File.separator + "Profiles";
    }
}
