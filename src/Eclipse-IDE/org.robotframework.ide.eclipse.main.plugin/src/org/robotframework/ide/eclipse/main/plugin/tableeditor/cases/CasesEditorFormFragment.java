package org.robotframework.ide.eclipse.main.plugin.tableeditor.cases;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.robotframework.ide.eclipse.main.plugin.RedPlugin;
import org.robotframework.ide.eclipse.main.plugin.model.RobotCase;
import org.robotframework.ide.eclipse.main.plugin.model.RobotCasesSection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotElement;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordCall;
import org.robotframework.ide.eclipse.main.plugin.model.RobotModelEvents;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFileSection;
import org.robotframework.ide.eclipse.main.plugin.model.cmd.CreateFreshCaseCommand;
import org.robotframework.ide.eclipse.main.plugin.model.cmd.CreateFreshKeywordCallCommand;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotElementEditingSupport.NewElementsCreator;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotSuiteEditorEvents;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.code.CodeEditorFormFragment;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.code.MatchesFilter;

class CasesEditorFormFragment extends CodeEditorFormFragment {

    private MatchesCollection matches;

    @Override
    protected void createSettingsTable(final Composite composite) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void whenElementSelectionChanged(final RobotElement selectedElement) {
        // TODO Auto-generated method stub

    }

    @Override
    protected ITreeContentProvider createContentProvider() {
        return new CasesContentProvider();
    }

    @Override
    protected String getViewerMenuId() {
        return "org.robotframework.ide.eclipse.editor.page.cases.contextMenu";
    }

    @Override
    protected String getHeaderMenuId() {
        return "org.robotframework.ide.eclipse.editor.page.cases.header.contextMenu";
    }

    @Override
    protected boolean sectionIsDefined() {
        return fileModel.findSection(RobotCasesSection.class).isPresent();
    }

    @Override
    protected RobotSuiteFileSection getSection() {
        return (RobotSuiteFileSection) fileModel.findSection(RobotCasesSection.class).orNull();
    }

    @Override
    protected NewElementsCreator provideNewElementsCreator() {
        return new NewElementsCreator() {

            @Override
            public RobotElement createNew(final Object parent) {
                if (parent instanceof RobotCasesSection) {
                    final RobotCasesSection section = (RobotCasesSection) parent;
                    commandsStack.execute(new CreateFreshCaseCommand(section, true));
                    return section.getChildren().get(section.getChildren().size() - 1);
                } else if (parent instanceof RobotCase) {
                    final RobotCase testCase = (RobotCase) parent;
                    commandsStack.execute(new CreateFreshKeywordCallCommand(testCase, true));
                    return testCase.getChildren().get(testCase.getChildren().size() - 1);
                }
                return null;
            }
        };
    }

    @Override
    protected int calculateLongestArgumentsList() {
        int max = RedPlugin.getDefault().getPreferences().getMimalNumberOfArgumentColumns();
        final RobotSuiteFileSection section = getSection();
        if (section != null) {
            for (final RobotElement testCase : section.getChildren()) {
                for (final RobotElement nestedElement : testCase.getChildren()) {
                    final RobotKeywordCall call = (RobotKeywordCall) nestedElement;
                    max = Math.max(max, call.getArguments().size());
                }
            }
        }
        return max;
    }

    @Override
    public MatchesCollection collectMatches(final String filter) {
        if (filter.isEmpty()) {
            return null;
        } else {
            final CasesMatchesCollection casesMatches = new CasesMatchesCollection();
            casesMatches.collect((RobotElement) viewer.getInput(), filter);
            return casesMatches;
        }
    }

    @Override
    protected MatchesProvider getMatchesProvider() {
        return new MatchesProvider() {
            @Override
            public MatchesCollection getMatches() {
                return matches;
            }
        };
    }

    @Inject
    @Optional
    private void whenUserRequestedFiltering(@UIEventTopic(RobotSuiteEditorEvents.SECTION_FILTERING_TOPIC
            + "/Test_Cases") final MatchesCollection matches) {
        this.matches = matches;

        try {
            viewer.getTree().setRedraw(false);
            if (matches == null) {
                viewer.collapseAll();
                viewer.setFilters(new ViewerFilter[0]);
            } else {
                viewer.expandAll();
                viewer.setFilters(new ViewerFilter[] { new MatchesFilter(matches) });
            }
        } finally {
            viewer.getTree().setRedraw(true);
        }
    }

    @Inject
    @Optional
    private void whenCaseIsAddedOrRemoved(
            @UIEventTopic(RobotModelEvents.ROBOT_CASE_STRUCTURAL_ALL) final RobotSuiteFileSection section) {
        if (section.getSuiteFile() == fileModel) {
            viewer.refresh();
            setDirty();
        }
    }

    @Inject
    @Optional
    private void whenCaseIsAdded(@UIEventTopic(RobotModelEvents.ROBOT_CASE_ADDED) final RobotSuiteFileSection section) {
        if (section.getSuiteFile() == fileModel) {
            viewer.setComparator(null);
            viewer.getTree().setSortColumn(null);
        }
    }

    @Inject
    @Optional
    private void whenKeywordCallIsAddedOrRemoved(
            @UIEventTopic(RobotModelEvents.ROBOT_KEYWORD_CALL_STRUCTURAL_ALL) final RobotCase testCase) {
        if (testCase.getSuiteFile() == fileModel) {
            viewer.refresh(testCase);
            setDirty();
        }
    }

    @Inject
    @Optional
    private void whenCaseDetailChanges(
            @UIEventTopic(RobotModelEvents.ROBOT_CASE_DETAIL_CHANGE_ALL) final RobotCase testCase) {
        if (testCase.getSuiteFile() == fileModel) {
            viewer.update(testCase, null);
            setDirty();
        }
    }

    @Inject
    @Optional
    private void whenKeywordCallDetailChanges(
            @UIEventTopic(RobotModelEvents.ROBOT_KEYWORD_CALL_DETAIL_CHANGE_ALL) final RobotKeywordCall keywordCall) {
        if (keywordCall.getParent() instanceof RobotCase && keywordCall.getSuiteFile() == fileModel) {
            viewer.update(keywordCall, null);
            setDirty();
        }
    }
}
