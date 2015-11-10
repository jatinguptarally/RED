/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.source.hyperlinks;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.robotframework.ide.eclipse.main.plugin.navigator.actions.ShowLibrarySourceAction;
import org.robotframework.ide.eclipse.main.plugin.project.library.LibrarySpecification;


/**
 * @author Michal Anglart
 *
 */
public class LibrarySourceHyperlink implements IHyperlink {

    private final IRegion source;

    private final IProject project;

    private final LibrarySpecification libSpec;

    public LibrarySourceHyperlink(final IRegion from, final IProject project, final LibrarySpecification libSpec) {
        this.source = from;
        this.project = project;
        this.libSpec = libSpec;
    }

    @Override
    public IRegion getHyperlinkRegion() {
        return source;
    }

    @Override
    public String getTypeLabel() {
        return null;
    }

    @Override
    public String getHyperlinkText() {
        return "Open Definition";
    }

    @Override
    public void open() {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        ShowLibrarySourceAction.openLibrarySource(page, project, libSpec);
    }
}