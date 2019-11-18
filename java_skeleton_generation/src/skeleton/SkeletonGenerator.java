package skeleton;

import java.util.*;

public class SkeletonGenerator {
    private ArrayList<TerminalElement> terminalParts;
    private ArrayList<NonTerminalElement> nonTerminalParts;
    private static Random random = new Random();
    private int stepCount = 0;
    private SkeletonPart rootElement;

    public SkeletonGenerator() {
        InitialElement initialElement = new InitialElement();
        this.rootElement = initialElement;
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>(Collections.singletonList(initialElement));
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

    public String toString() {
        if (!isFinished()) {
            return "";
        }
        StringBuilder skeleton = recursiveToString(new StringBuilder(), rootElement, new StringBuilder());
        return skeleton.toString();
    }

    private StringBuilder recursiveToString(StringBuilder depth, SkeletonPart currentElement, StringBuilder skeleton) {

        if (currentElement.isTerminal()) {
            skeleton.append(depth + currentElement.getID() + "\n");
        } else {
            skeleton.append(depth + "*" + currentElement.getID() + "\n");
        }
        List<SkeletonPart> children = currentElement.getChildren();
        for (SkeletonPart child : children) {
            skeleton = recursiveToString(depth.append(" "), child, skeleton);
        }
        return skeleton;
    }
}
