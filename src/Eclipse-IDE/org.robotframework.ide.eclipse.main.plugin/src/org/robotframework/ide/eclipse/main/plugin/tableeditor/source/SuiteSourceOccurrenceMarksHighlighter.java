/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.source;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.robotframework.ide.eclipse.main.plugin.RedPlugin;

import com.google.common.base.Optional;

class SuiteSourceOccurrenceMarksHighlighter {

    private static final String MARKER_ID = "org.robotframework.ide.texteditor.occurrencesMark";

    private final IFile file;

    private final IDocument document;

    private final FindReplaceDocumentAdapter findAdapter;

    private Set<IRegion> occurencesRegions;

    SuiteSourceOccurrenceMarksHighlighter(final IFile editedFile, final IDocument document) {
        this.document = document;
        this.file = editedFile;
        this.findAdapter = new FindReplaceDocumentAdapter(document);
        this.occurencesRegions = newHashSet();
    }

    void install(final SourceViewer viewer) {
        viewer.getTextWidget().addCaretListener(new CaretListener() {

            @Override
            public void caretMoved(final CaretEvent event) {
                try {
                    final Optional<IRegion> currentRegion = getCurrentRegion(event.caretOffset);
                    if (!currentRegion.isPresent()) {
                        removeOccurencesHighlighting();
                        occurencesRegions = newHashSet();
                    } else {
                        final Set<IRegion> regions = findOccurencesRegions(currentRegion.get());
                        if (!Objects.equals(occurencesRegions, regions)) {
                            removeOccurencesHighlighting();
                            highlightOccurences(currentRegion.get(), regions);
                            occurencesRegions = regions;
                        }
                    }
                } catch (final BadLocationException e) {
                    RedPlugin.logError("Unable to create occurences markers", e);
                }

            }

        });
    }

    private Optional<IRegion> getCurrentRegion(final int offset) throws BadLocationException {
        return DocumentUtilities.findVariable(document, offset).or(DocumentUtilities.findCellRegion(document, offset));
    }

    private Set<IRegion> findOccurencesRegions(final IRegion region) throws BadLocationException {
        final Set<IRegion> regions = newHashSet();

        final String selectedText = document.get(region.getOffset(), region.getLength());

        int currentOffset = 0;
        IRegion foundedRegion = findAdapter.find(currentOffset, selectedText, true, true, !isVariable(selectedText),
                false);
        while (foundedRegion != null) {
            regions.add(foundedRegion);

            currentOffset = foundedRegion.getOffset() + foundedRegion.getLength();
            foundedRegion = findAdapter.find(currentOffset, selectedText, true, true, !isVariable(selectedText), false);
        }

        return regions;
    }

    private static boolean isVariable(final String text) {
        return Pattern.matches("[@$&%]\\{.+\\}", text);
    }

    private void removeOccurencesHighlighting() {
        try {
            file.deleteMarkers(MARKER_ID, true, IResource.DEPTH_ONE);
        } catch (final CoreException e) {
            RedPlugin.logError("Unable to remove occurences markers", e);
        }
    }

    private void highlightOccurences(final IRegion selectedRegion, final Set<IRegion> regions) throws BadLocationException {
        final String selectedText = document.get(selectedRegion.getOffset(), selectedRegion.getLength());
        final WorkspaceJob wsJob = new WorkspaceJob("Creating occurences markers") {

            @Override
            public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
                for (final IRegion region : regions) {
                    createMarker(region, selectedText);
                }
                return Status.OK_STATUS;
            }
        };
        wsJob.setSystem(true);
        wsJob.schedule();
    }

    private void createMarker(final IRegion region, final String selectedText) {
        try {
            final IMarker marker = file.createMarker(MARKER_ID);
            marker.setAttribute(IMarker.TRANSIENT, true);
            marker.setAttribute(IMarker.MESSAGE, "Occurrence of '" + selectedText + "'");
            marker.setAttribute(IMarker.CHAR_START, region.getOffset());
            marker.setAttribute(IMarker.CHAR_END, region.getOffset() + region.getLength());
        } catch (final CoreException e) {
            RedPlugin.logError("Unable to create occurences marker for '" + selectedText + "'", e);
        }
    }
}