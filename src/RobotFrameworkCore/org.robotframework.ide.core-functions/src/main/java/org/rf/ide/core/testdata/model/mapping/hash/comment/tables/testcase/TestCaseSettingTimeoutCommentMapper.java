/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.mapping.hash.comment.tables.testcase;

import java.util.List;

import org.rf.ide.core.testdata.model.RobotFile;
import org.rf.ide.core.testdata.model.mapping.IHashCommentMapper;
import org.rf.ide.core.testdata.model.table.testCases.TestCase;
import org.rf.ide.core.testdata.model.table.testCases.TestCaseTimeout;
import org.rf.ide.core.testdata.text.read.ParsingState;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;


public class TestCaseSettingTimeoutCommentMapper implements IHashCommentMapper {

    @Override
    public boolean isApplicable(ParsingState state) {
        return (state == ParsingState.TEST_CASE_SETTING_TEST_TIMEOUT
                || state == ParsingState.TEST_CASE_SETTING_TEST_TIMEOUT_VALUE || state == ParsingState.TEST_CASE_SETTING_TEST_TIMEOUT_MESSAGE_ARGUMENTS);
    }


    @Override
    public void map(RobotToken rt, ParsingState currentState,
            RobotFile fileModel) {
        List<TestCase> testCases = fileModel.getTestCaseTable().getTestCases();
        TestCase testCase = testCases.get(testCases.size() - 1);

        List<TestCaseTimeout> timeouts = testCase.getTimeouts();
        TestCaseTimeout testCaseTimeout = timeouts.get(timeouts.size() - 1);
        testCaseTimeout.addCommentPart(rt);
    }
}