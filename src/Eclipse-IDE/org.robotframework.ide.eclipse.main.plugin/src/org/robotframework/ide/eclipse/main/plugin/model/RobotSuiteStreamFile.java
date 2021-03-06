/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.content.IContentDescriber;
import org.rf.ide.core.testdata.RobotParser;
import org.rf.ide.core.testdata.model.RobotFileOutput;
import org.rf.ide.core.testdata.model.RobotProjectHolder;
import org.robotframework.ide.eclipse.main.plugin.RedPlugin;
import org.robotframework.ide.eclipse.main.plugin.project.ASuiteFileDescriber;
import org.robotframework.ide.eclipse.main.plugin.project.RobotSuiteFileDescriber;
import org.robotframework.ide.eclipse.main.plugin.project.TsvRobotSuiteFileDescriber;

public class RobotSuiteStreamFile extends RobotSuiteFile {

    private final String name;
    private final InputStream input;
    private final boolean readOnly;

    public RobotSuiteStreamFile(final String name, final InputStream input, final boolean readOnly) {
        super(null, null);
        this.name = name;
        this.input = input;
        this.readOnly = readOnly;
    }

    @Override
    public String getFileExtension() {
        return name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : null;
    }

    @Override
    protected String getContentTypeId() {
        final String id = super.getContentTypeId();
        if (id != null) {
            return id;
        }
        contentTypeId = null;
        if (input != null) {
            if ("__init__.robot".equals(name) || "__init__.tsv".equals(name) || "__init__.txt".equals(name)) {
                contentTypeId = ASuiteFileDescriber.INIT_FILE_CONTENT_ID;
                return contentTypeId;
            }

            int validationResult;
            try {
                final String fileExt = getFileExtension();
                final ASuiteFileDescriber desc = fileExt.toLowerCase().equals("tsv") ? new TsvRobotSuiteFileDescriber()
                        : new RobotSuiteFileDescriber();

                validationResult = desc.describe(input, null);
                if (validationResult == IContentDescriber.VALID) {
                    contentTypeId = ASuiteFileDescriber.SUITE_FILE_CONTENT_ID;
                } else {
                    contentTypeId = ASuiteFileDescriber.RESOURCE_FILE_CONTENT_ID;
                }
            } catch (final IOException e) {
                // null will be returned
            }
        }
        return contentTypeId;
    }

    @Override
    public List<RobotSuiteFileSection> getSections() {
        return getSections(new ParsingStrategy() {

            @Override
            public RobotFileOutput parse() {
                final RobotParser parser = RobotParser
                        .create(new RobotProjectHolder(RedPlugin.getDefault().getActiveRobotInstallation()));
                return parser.parseEditorContent("", new File(name));
            }
        });
    }

    @Override
    protected ParsingStrategy createReparsingStrategy(final String newContent) {
        return new ParsingStrategy() {

            @Override
            public RobotFileOutput parse() {
                final RobotParser parser = RobotParser
                        .create(new RobotProjectHolder(RedPlugin.getDefault().getActiveRobotInstallation()));
                return parser.parseEditorContent(newContent, new File(name));
            }
        };
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEditable() {
        return !readOnly;
    }

    @Override
    public void refreshOnFileChange() {
        // do nothing
    }
}
