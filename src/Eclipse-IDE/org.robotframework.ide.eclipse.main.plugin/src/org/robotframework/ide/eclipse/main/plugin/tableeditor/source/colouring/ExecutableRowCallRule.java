package org.robotframework.ide.eclipse.main.plugin.tableeditor.source.colouring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.rf.ide.core.testdata.text.read.IRobotLineElement;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;

import com.google.common.base.Optional;


public class ExecutableRowCallRule implements ISyntaxColouringRule {

    private final IToken textToken;

    public ExecutableRowCallRule(final IToken textToken) {
        this.textToken = textToken;
    }

    @Override
    public boolean isApplicable(final IRobotLineElement token) {
        return token instanceof RobotToken;
    }

    @Override
    public Optional<PositionedTextToken> evaluate(final IRobotLineElement token, final int offsetInToken,
            final List<IRobotLineElement> analyzedTokens) {
        final List<IRobotTokenType> types = token.getTypes();
        final IRobotTokenType type = types.get(0);

        if (isAction(type) || isActionArgument(type)) {
            final List<RobotToken> tokensBeforeInLine = getTokensFromLine(analyzedTokens, token.getLineNumber());

            for (int i = 0; i < tokensBeforeInLine.size(); i++) {
                final RobotToken prevToken = tokensBeforeInLine.get(i);
                if (!prevToken.getTypes().contains(RobotTokenType.VARIABLE_USAGE)
                        && !prevToken.getTypes().contains(RobotTokenType.ASSIGNMENT)
                        && !prevToken.getText().isEmpty()
                        && !prevToken.getTypes().contains(RobotTokenType.PRETTY_ALIGN_SPACE)
                        && !prevToken.getText().equals("\\")) {
                    return Optional.absent();
                }
            }

            if (!token.getTypes().contains(RobotTokenType.VARIABLE_USAGE)) {
                return Optional
                        .of(new PositionedTextToken(textToken, token.getStartOffset(), token.getText().length()));
            }
        }
        return Optional.absent();
    }

    private boolean isActionArgument(final IRobotTokenType type) {
        return type == RobotTokenType.KEYWORD_ACTION_ARGUMENT || type == RobotTokenType.TEST_CASE_ACTION_ARGUMENT;
    }

    private boolean isAction(final IRobotTokenType type) {
        return type == RobotTokenType.KEYWORD_ACTION_NAME || type == RobotTokenType.TEST_CASE_ACTION_NAME;
    }

    private List<RobotToken> getTokensFromLine(final List<IRobotLineElement> analyzedTokens, final int line) {
        final List<RobotToken> tokens = new ArrayList<>();
        for (int i = analyzedTokens.size() - 1; i >= 0; i--) {
            final IRobotLineElement element = analyzedTokens.get(i);
            if (element.getLineNumber() != line) {
                break;
            } else if (element instanceof RobotToken) {
                tokens.add(0, (RobotToken) element);
            }
        }
        return tokens;
    }
}
