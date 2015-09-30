/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.source;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;


class SuiteSourceIndentLineEditStrategy implements IAutoEditStrategy {

    @Override
    public void customizeDocumentCommand(final IDocument document, final DocumentCommand command) {
        if (command.length == 0 && command.text != null
                && TextUtilities.endsWith(document.getLegalLineDelimiters(), command.text) != -1) {
            autoIndentAfterNewLine(document, command);
        }
    }

    private void autoIndentAfterNewLine(final IDocument document, final DocumentCommand command) {
        if (command.offset == -1 || document.getLength() == 0) {
            return;
        }

        try {
            final int shift = (command.offset == document.getLength() ? command.offset - 1 : command.offset);

            final IRegion lineRegion = document.getLineInformationOfOffset(shift);
            final int start = lineRegion.getOffset();
            final int end = findEndOfWhiteSpace(document, start, command.offset);

            final StringBuffer buf = new StringBuffer(command.text);
            if (end > start) {
                final String whitespacesFromPreviousLine = document.get(start, end - start);
                buf.append(whitespacesFromPreviousLine);
            }
            command.text = buf.toString();

        } catch (final BadLocationException e) {
            // stop work
        }
    }

    private int findEndOfWhiteSpace(final IDocument document, final int start, final int end)
            throws BadLocationException {
        int offset = start;
        while (offset < end) {
            final char c = document.getChar(offset);
            if (c != ' ' && c != '\t') {
                return offset;
            }
            offset++;
        }
        return end;
    }
}