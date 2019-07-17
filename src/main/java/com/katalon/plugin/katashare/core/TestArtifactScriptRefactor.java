package com.katalon.plugin.katashare.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.katalon.plugin.katashare.core.util.EntityUtil;

public class TestArtifactScriptRefactor {
    private static final String CR_LEFT_PARENTHESIS = "(";
    
    private static final String CR_RIGHT_PARENTHESIS = ")";
    
    private static final String CR_PRIME = "'";
    
    private static final String CR_DOUBLE_PRIMES = "\"";
    
    private FolderType parentType;

    private boolean hasRightBracket;

    private boolean hasRightQuote;

    private boolean hasRightDoubleQuote;

    private boolean isFolder;
    
    private Map<String, String> entityIdLookup;

    public TestArtifactScriptRefactor(FolderType parentType, Map<String, String> entityIdLookup, boolean hasRightBracket,
            boolean hasRightQuote, boolean hasRightDoubleQuote) {
        this(parentType, entityIdLookup, hasRightBracket, hasRightQuote, hasRightDoubleQuote, false);
    }

    public TestArtifactScriptRefactor(FolderType parentType, Map<String, String> entityIdLookup, boolean hasRightBracket,
            boolean hasRightQuote, boolean hasRightDoubleQuote, boolean isFolder) {
        this.parentType = parentType;
        this.entityIdLookup = entityIdLookup;
        this.hasRightBracket = hasRightBracket;
        this.hasRightQuote = hasRightQuote;
        this.hasRightDoubleQuote = hasRightDoubleQuote;
        this.isFolder = isFolder;
    }

    public FolderType getParentType() {
        return parentType;
    }


    private String getReferencePrefix() {
        switch (parentType) {
            case TESTCASE:
                return "findTestCase";
            case WEBELEMENT:
                return "findTestObject";
            default:
                return StringUtils.EMPTY;
        }
    }

    private String getRelativeId(String s) {
        int firstSeparatorIdx = s.indexOf(EntityUtil.getEntityIdSeparator());
        if (firstSeparatorIdx < 0) {
            return s;
        }
        return s.substring(firstSeparatorIdx + (isFolder ? 0 : 1), s.length());
    }

    private String buildParentheses(String s) {
        return CR_LEFT_PARENTHESIS + s + (hasRightBracket ? CR_RIGHT_PARENTHESIS : "");
    }

    private String buildQuote(String s) {
        return CR_PRIME + s + (hasRightQuote ? CR_PRIME : "");
    }

    private String buildDoubleQuotes(String s) {
        return CR_DOUBLE_PRIMES + s + (hasRightDoubleQuote ? CR_DOUBLE_PRIMES : "");
    }

    private List<String> getQuotedReferences(String entityId) {
        String prefix = getReferencePrefix();
        String relativeId = getRelativeId(entityId);
        return Arrays.asList(new String[] { prefix + buildParentheses(buildQuote(entityId)),
                prefix + buildParentheses(buildQuote(relativeId)), });
    }

    private List<String> getDoubleQuotedReferenceStrings(String entityId) {
        String prefix = getReferencePrefix();
        String relativeId = getRelativeId(entityId);
        return Arrays.asList(new String[] { prefix + buildParentheses(buildDoubleQuotes(entityId)),
                prefix + buildParentheses(buildDoubleQuotes(relativeId)), });
    }

    public void replace(String oldEntityId, String newEntityId, File file) throws IOException {
        String scriptContent = getScriptContent(file);
        String relativeId = getRelativeId(newEntityId);
        String referencePrefix = getReferencePrefix();

        boolean updated = false;
        String newQuotedScript = referencePrefix + buildParentheses(buildQuote(relativeId));
        for (String potentialQuote : getQuotedReferences(oldEntityId)) {
            if (!scriptContent.contains(potentialQuote)) {
                continue;
            }

            scriptContent = scriptContent.replace(potentialQuote, newQuotedScript);
            updated = true;
        }

        String newDoubleQuotedScript = referencePrefix + buildParentheses(buildDoubleQuotes(relativeId));
        for (String potentialDoubleQuotes : getDoubleQuotedReferenceStrings(oldEntityId)) {
            if (!scriptContent.contains(potentialDoubleQuotes)) {
                continue;
            }

            scriptContent = scriptContent.replace(potentialDoubleQuotes, newDoubleQuotedScript);
            updated = true;
        }

        if (!updated) {
            return;
        }

        FileUtils.write(file, scriptContent, StandardCharsets.UTF_8);
    }

    private String getScriptContent(File scriptFile) throws IOException {
        return FileUtils.readFileToString(scriptFile, StandardCharsets.UTF_8);
    }

    public void updateReferences(List<File> files) throws IOException {
        for (File scriptFile : files) {
            for (String oldEntityId : entityIdLookup.keySet()) {
                replace(oldEntityId, entityIdLookup.get(oldEntityId), scriptFile);
            }
        }
    }

    public static TestArtifactScriptRefactor createForTestCaseEntity(Map<String, String> entityIdLookup) {
        return new TestArtifactScriptRefactor(FolderType.TESTCASE, entityIdLookup, true, true, true);
    }

    public static TestArtifactScriptRefactor createForTestObjectEntity(Map<String, String> entityIdLookup) {
        return new TestArtifactScriptRefactor(FolderType.WEBELEMENT, entityIdLookup, false, true, true);
    }
}
