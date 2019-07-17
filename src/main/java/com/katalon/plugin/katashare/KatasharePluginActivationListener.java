package com.katalon.plugin.katashare;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.extension.PluginActivationListener;

public class KatasharePluginActivationListener implements PluginActivationListener {

    @Override
    public void afterActivation(Plugin plugin) {
        System.out.println(plugin.getPluginId() + "has been installed.");
    }
}
