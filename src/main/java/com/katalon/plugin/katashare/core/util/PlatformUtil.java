package com.katalon.plugin.katashare.core.util;

import com.katalon.platform.api.controller.Controller;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.ui.UIService;

public class PlatformUtil {
    
    public static ProjectEntity getCurrentProject() {
        return ApplicationManager.getInstance().getProjectManager().getCurrentProject();
    }
    
    public static <U extends UIService> U getUIService(Class<U> clazz) {
        return ApplicationManager.getInstance().getUIServiceManager().getService(clazz);
    }
    
    public static <C extends Controller> C getPlatformController(Class<C> clazz) {
        return ApplicationManager.getInstance().getControllerManager().getController(clazz);
    }
}
