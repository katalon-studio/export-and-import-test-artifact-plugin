package com.katalon.plugin.katashare;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;
import com.katalon.plugin.katashare.composer.toolbar.handler.ExportTestArtifactHandler;
import com.katalon.plugin.katashare.composer.toolbar.handler.ImportTestArtifactHandler;
import com.katalon.plugin.katashare.constant.StringConstants;

public class PluginMenuItemDescription implements ToolItemWithMenuDescription {

    private Menu menu;
    
    @Override
    public String toolItemId() {
        return "com.katalon.plugin.katashare.PluginMenuItemDescription";
    }

    @Override
    public String name() {
        return "Katashare";
    }

    @Override
    public String iconUrl() {
        return "platform:/plugin/" + StringConstants.PLUGIN_BUNDLE_ID + "/icons/import_TC_TO_32x24.png";
    }

    @Override
    public Menu getMenu(Control parent) {
        menu = new Menu(parent);
        
        MenuItem exportTestArtifactMenuItem = new MenuItem(menu, SWT.PUSH);
        exportTestArtifactMenuItem.setText("Export Test Artifacts");
        exportTestArtifactMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ExportTestArtifactHandler handler = new ExportTestArtifactHandler(e.widget.getDisplay().getActiveShell());
                handler.execute();
            }
        });
        
        MenuItem importTestArtifactMenuItem = new MenuItem(menu, SWT.PUSH);
        importTestArtifactMenuItem.setText("Import Test Artifacts");
        importTestArtifactMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ImportTestArtifactHandler handler = new ImportTestArtifactHandler(e.widget.getDisplay().getActiveShell());
                handler.execute();
            }
        });
        return menu;
    }

}
