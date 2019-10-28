package skeleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SkeletonGenerator {
    private List<TerminalElement> terminalParts;
    private List<NonTerminalElement> nonTerminalParts;
    private Random random;
    private int stepCount;

    public SkeletonGenerator() {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = Collections.singletonList(new InitialElement());
        this.random = new Random();
    }

    public boolean isFinished() {
        return nonTerminalParts.isEmpty();
    }

    public void doOneStep() {
        if (isFinished()) {
            return;
        }
        stepCount++;
        NonTerminalElement nonTerminal = nonTerminalParts.remove(nonTerminalParts.size() - 1);
        List<ReplacementRule> rules = nonTerminal.getRules();

        // choose one rule (for now do it randomly)
        ReplacementRule rule = rules.get(random.nextInt(rules.size()));
        List<SkeletonPart> newParts = rule.apply();
        for (SkeletonPart part : newParts) {
            if (part instanceof TerminalElement) {
                terminalParts.add((TerminalElement) part);
            } else {
                nonTerminalParts.add((NonTerminalElement) part);
            }
        }
    }

    public List<TerminalElement> getTerminalParts() {
        return terminalParts;
    }

    public List<NonTerminalElement> getNonTerminalParts() {
        return nonTerminalParts;
    }

    public int getStepCount() {
        return stepCount;
    }
}
