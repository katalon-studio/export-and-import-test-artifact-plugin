package com.katalon.plugin.katashare.constant;

import org.eclipse.osgi.util.NLS;

public class MessageConstants {
    
    private static final String MESSAGE_FILE_NAME = "ConstantMessages";
    
    static {
        NLS.initializeMessages("com.katalon.plugin.katashare.constant." + MESSAGE_FILE_NAME, MessageConstants.class);
    }
    
    public static String ERROR;
    
    public static String INFO;
    
    public static String DIALOG_TITLE_EXPORT_TEST_ARTIFACTS;
    
    public static String LBL_SELECT_TEST_CASE;
    
    public static String LBL_SELECT_TEST_OBJECT;
    
    public static String LBL_SELECT_PROFILES;
    
    public static String COL_TEST_CASE_NUMBER;
    
    public static String COL_TEST_CASE_ID;
    
    public static String COL_TEST_OBJECT_ID;
    
    public static String COL_PROFILE_ID;
    
    public static String TOOL_ITEM_ADD;
    
    public static String TOOL_ITEM_DELETE;
    
    public static String LBL_EXPORT_LOCATION;
    
    public static String BTN_BROWSE;
    
    public static String MSG_INVALID_EXPORT_LOCATION;
    
    public static String MSG_UNABLE_TO_EXPORT_TEST_ARTIFACTS;
    
    public static String MSG_UNABLE_TO_IMPORT_TEST_ARTIFACTS;

    public static String DIALOG_TITLE_IMPORT_TEST_ARTIFACTS;
    
    public static String LBL_CHOOSE_IMPORT_FILE;
    
    public static String LBL_CHOOSE_TEST_CASE_IMPORT_LOCATION;
    
    public static String LBL_CHOOSE_TEST_OBJECT_IMPORT_LOCATION;
    
    public static String MSG_INVALID_IMPORT_FILE;
    
    public static String MSG_INVALID_TEST_CASE_IMPORT_LOCATION;
    
    public static String MSG_INVALID_TEST_OBJECT_IMPORT_LOCATION;
    
    public static String MSG_TEST_ARTIFACTS_EXPORTED_SUCCESSFULLY;
    
    public static String MSG_TEST_ARTIFACTS_IMPORTED_SUCCESSFULLY;
}
