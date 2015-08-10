package org.robotframework.ide.eclipse.main.plugin.tableeditor.variables;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.robotframework.ide.eclipse.main.plugin.RedImages;
import org.robotframework.ide.eclipse.main.plugin.model.RobotVariable;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.ISectionFormFragment.MatchesProvider;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.MatchesHighlightingLabelProvider;
import org.robotframework.red.graphics.ImagesManager;

class VariableValueLabelProvider extends MatchesHighlightingLabelProvider {

    VariableValueLabelProvider(final MatchesProvider matchesProvider) {
        super(matchesProvider);
    }

    @Override
    public String getText(final Object element) {
        return element instanceof RobotVariable ? ((RobotVariable) element).getValue() : "";
    }

    @Override
    public StyledString getStyledText(final Object element) {
        return highlightMatches(new StyledString(getText(element)));
    }

    @Override
    public String getToolTipText(final Object element) {
        if (element instanceof RobotVariable) {
            final String tooltipText = getText(element);
            return tooltipText.isEmpty() ? "<empty>" : tooltipText;
        }
        return null;
    }

    @Override
    public Image getToolTipImage(final Object element) {
        if (element instanceof RobotVariable) {
            return ImagesManager.getImage(RedImages.getTooltipImage());
        }
        return null;
    }
}
