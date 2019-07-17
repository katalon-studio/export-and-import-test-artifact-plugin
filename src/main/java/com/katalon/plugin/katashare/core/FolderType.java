package com.katalon.plugin.katashare.core;

public enum FolderType {
    TESTCASE("Test case"),
    WEBELEMENT("Object");
    
    private final String text;

    private FolderType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
