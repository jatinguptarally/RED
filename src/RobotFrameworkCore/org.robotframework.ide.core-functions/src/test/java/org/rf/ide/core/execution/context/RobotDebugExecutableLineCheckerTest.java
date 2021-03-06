/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.execution.context;

import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.rf.ide.core.execution.context.RobotDebugExecutableLineChecker;
import org.rf.ide.core.testdata.RobotParser;
import org.rf.ide.core.testdata.model.RobotFile;

public class RobotDebugExecutableLineCheckerTest {

    RobotFile modelFile;

    @Test
    public void test() throws URISyntaxException {

        RobotParser parser = RobotModelTestProvider.getParser();
        modelFile = RobotModelTestProvider.getModelFile("test_ExeChecker_1.robot", parser);

        checkExecutableLines(new int[] { 2, 3, 4, 5, 13, 14, 16, 17, 18, 22, 23, 24, 26, 29, 32, 36, 38, 41, 43 });

        checkNotExecutableLines(new int[] { 1, 6, 7, 8, 9, 10, 11, 12, 15, 19, 20, 21, 25, 27, 28, 30, 31, 33, 34, 35,
                37, 39, 40, 42, 44 });

    }

    private void checkExecutableLines(final int[] lines) {
        for (int i = 0; i < lines.length; i++) {
            Assert.assertTrue(RobotDebugExecutableLineChecker.isExecutableLine(modelFile, lines[i]));
        }
    }

    private void checkNotExecutableLines(final int[] lines) {
        for (int i = 0; i < lines.length; i++) {
            Assert.assertFalse(RobotDebugExecutableLineChecker.isExecutableLine(modelFile, lines[i]));
        }
    }
}
