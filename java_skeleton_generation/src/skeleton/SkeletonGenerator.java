package skeleton;

import skeleton.elements.WholeBody;
import skeleton.elements.NonTerminalElement;
import skeleton.elements.SkeletonPart;
import skeleton.elements.TerminalElement;
import skeleton.replacementRules.ReplacementRule;
import skeleton.replacementRules.RuleDictionary;

import java.util.*;

public class SkeletonGenerator {
    private ArrayList<TerminalElement> terminalParts;
    private ArrayList<NonTerminalElement> nonTerminalParts;
    private static Random random = new Random();
    private int stepCount = 0;
    private RuleDictionary ruleDictionary;

    public SkeletonGenerator() {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>(Collections.singletonList(new WholeBody()));
        this.ruleDictionary = new RuleDictionary();
    }

    public void doOneStep() {
        if (isFinished()) {
            return;
        }

        stepCount++;
        NonTerminalElement nonTerminalElement = nonTerminalParts.remove(nonTerminalParts.size() - 1);

        List<ReplacementRule> rules = ruleDictionary.getRules(nonTerminalElement.getID());
        if (rules.isEmpty()) {
            System.err.println("Non terminal has no applicable rule!");
            nonTerminalParts.add(nonTerminalElement);
            return;
        }
        ReplacementRule rule = rules.get(random.nextInt(rules.size()));
        List<SkeletonPart> generatedParts = rule.apply(nonTerminalElement);
        for (SkeletonPart part : generatedParts) {
            if (part.isTerminal()) {
                terminalParts.add((TerminalElement) part);
            } else {
                nonTerminalParts.add((NonTerminalElement) part);
            }
        }
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

    public SkeletonPart getRootElement() {
        Object[] partsWithoutParent = terminalParts.stream().filter(part -> !part.hasParent()).toArray();
        if (partsWithoutParent.length > 0) {
            if (partsWithoutParent.length > 1) {
                System.err.println("Found several skeleton parts without parent!");
            }
            return (SkeletonPart) partsWithoutParent[0];
        }

        partsWithoutParent = nonTerminalParts.stream().filter(part -> !part.hasParent()).toArray();
        if (partsWithoutParent.length > 0) {
            if (partsWithoutParent.length > 1) {
                System.err.println("Found several skeleton parts without parent!");
            }
            return (SkeletonPart) partsWithoutParent[0];
        }

        return null;
    }

    public int getStepCount() {
        return stepCount;
    }

    public String toString() {
        SkeletonPart rootElement = getRootElement();
        StringBuilder skeleton = recursiveToString("", rootElement, new StringBuilder());
        return skeleton.toString();
    }

    private StringBuilder recursiveToString(String depth, SkeletonPart currentElement, StringBuilder skeleton) {

        if (currentElement.isTerminal()) {
            skeleton.append(depth).append(currentElement.getID()).append("\n");
        } else {
            skeleton.append(depth).append("*").append(currentElement.getID()).append("\n");
        }
        List<SkeletonPart> children = currentElement.getChildren();
        for (SkeletonPart child : children) {
            String newDepth = depth + " ";
            skeleton = recursiveToString(newDepth, child, skeleton);
        }
        return skeleton;
    }
}
