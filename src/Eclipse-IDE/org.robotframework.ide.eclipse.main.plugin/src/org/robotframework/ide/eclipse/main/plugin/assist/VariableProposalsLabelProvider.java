/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.assist;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Stylers;
import org.eclipse.swt.graphics.Image;
import org.robotframework.red.graphics.ImagesManager;
import org.robotframework.red.viewers.RedCommonLabelProvider;

class VariableProposalsLabelProvider extends RedCommonLabelProvider {

    @Override
    public Image getImage(final Object element) {
        return ImagesManager.getImage(((VariableContentProposal) element).getImage());
    }

    @Override
    public String getText(final Object element) {
        return getStyledText(element).getString();
    }

    @Override
    public StyledString getStyledText(final Object element) {
        final VariableContentProposal proposal = (VariableContentProposal) element;
        final StyledString label = new StyledString(proposal.getLabel());
        label.setStyle(0, proposal.getMatchingPrefix().length(), Stylers.Common.MARKED_PREFIX_STYLER);
        return label;
    }
}
