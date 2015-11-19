/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.core.testData.model;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import org.robotframework.ide.core.testData.text.read.recognizer.RobotToken;


public abstract class ATags<T> extends AModelElement<T> {

    private final RobotToken declaration;
    private final List<RobotToken> tags = new ArrayList<>();
    private final List<RobotToken> comment = new ArrayList<>();


    protected ATags(final RobotToken declaration) {
        this.declaration = declaration;
    }


    @Override
    public boolean isPresent() {
        return (declaration != null);
    }


    public RobotToken getDeclaration() {
        return declaration;
    }


    public List<RobotToken> getTags() {
        return Collections.unmodifiableList(tags);
    }


    public void addTag(final RobotToken tag) {
        tags.add(tag);
    }


    public List<RobotToken> getComment() {
        return Collections.unmodifiableList(comment);
    }


    public void addCommentPart(final RobotToken rt) {
        this.comment.add(rt);
    }


    @Override
    public FilePosition getBeginPosition() {
        return declaration.getFilePosition();
    }


    @Override
    public List<RobotToken> getElementTokens() {
        List<RobotToken> tokens = new ArrayList<>();
        if (isPresent()) {
            tokens.add(getDeclaration());
            tokens.addAll(getTags());
            tokens.addAll(getComment());
        }

        return tokens;
    }
}
