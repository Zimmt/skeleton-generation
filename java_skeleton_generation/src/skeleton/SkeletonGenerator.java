package skeleton;

import java.util.*;

public class SkeletonGenerator {
    private ArrayList<TerminalElement> terminalParts;
    private ArrayList<NonTerminalElement> nonTerminalParts;
    private static Random random = new Random();
    private int stepCount = 0;

    public SkeletonGenerator() {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>(Collections.singletonList(new InitialElement()));
    }

    // for testing
    public SkeletonGenerator(SimpleBone bone) {
        this.terminalParts = new ArrayList<>(Collections.singletonList(bone));
        this.nonTerminalParts = new ArrayList<>();
    }

    public boolean isFinished() {
        return nonTerminalParts.isEmpty();
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
