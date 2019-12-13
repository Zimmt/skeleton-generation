package skeleton;

import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.nonterminal.NonTerminalElement;
import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.TerminalElement;
import skeleton.replacementRules.ReplacementRule;
import skeleton.replacementRules.RuleDictionary;
import util.BoundingBox;
import util.CubicBezierCurve;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.*;

public class SkeletonGenerator {

    private ArrayList<TerminalElement> terminalParts;
    private ArrayList<NonTerminalElement> nonTerminalParts;
    private RuleDictionary ruleDictionary;
    private CubicBezierCurve spineLocation;

    private static Random random = new Random();
    private int stepCount = 0;
    private int nextBoneId = 0;

    public SkeletonGenerator() {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>();
        this.nonTerminalParts.add(new WholeBody(
                new TransformationMatrix(),
                BoundingBox.defaultBox().scale(new Vector3f(14f, 8f, 1f)
                ),
                this));
        this.ruleDictionary = new RuleDictionary();
        this.spineLocation = generateSpine();
    }

    public void doOneStep() {
        if (isFinished()) {
            return;
        }

        stepCount++;
        NonTerminalElement nonTerminalElement = nonTerminalParts.remove(nonTerminalParts.size() - 1);

        List<ReplacementRule> rules = ruleDictionary.getRules(nonTerminalElement.getKind());
        if (rules == null || rules.isEmpty()) {
            System.err.println("Non terminal " + nonTerminalElement.getKind() + " has no applicable rule!");
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

    public int getNextBoneId() {
        nextBoneId++;
        return nextBoneId - 1;
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

    public CubicBezierCurve getSpineLocation() {
        return spineLocation;
    }

    public String toString() {
        SkeletonPart rootElement = getRootElement();
        return recursiveToString("|-- ", rootElement);
    }

    private String recursiveToString(String depth, SkeletonPart currentElement) {

        StringBuilder skeleton = new StringBuilder("\u001B[32m").append(depth);
        if (currentElement.isMirrored()) {
            skeleton.append("2x ");
        }
        if (!currentElement.isTerminal()) {
            skeleton.append("*");
        }
        skeleton.append(currentElement.getKind()).append("\u001B[90m").append(" (");

        // ancestors
        /*SkeletonPart ancestor = currentElement.getAncestor();
        while (ancestor != null) {
            skeleton.append(ancestor.getID());
            if (ancestor.hasAncestor()) {
                skeleton.append(", ");
            }
            ancestor = ancestor.getAncestor();
        }*/

        // position
        Point3f position = currentElement.getWorldPosition();
        skeleton.append("position: ").append(position);

        // bounding box dimensions
        skeleton.append(", bounding box scale: ");
        BoundingBox boundingBox = currentElement.getBoundingBox();
        skeleton.append(boundingBox.getXLength()).append(", ");
        skeleton.append(boundingBox.getYLength()).append(", ");
        skeleton.append(boundingBox.getZLength());

        skeleton.append(")").append("\u001B[0m").append("\n"); // reset color to white

        List<SkeletonPart> children = currentElement.getChildren();
        for (SkeletonPart child : children) {
            skeleton.append(recursiveToString("    " + depth, child));
        }
        return skeleton.toString();
    }

    private CubicBezierCurve generateSpine() {
        Point2f p0 = new Point2f(2f, 6f);
        Point2f p1 = new Point2f(4f, 2f);
        Point2f p2 = new Point2f(10f, 6f);
        Point2f p3 = new Point2f(12f, 2f);

        return new CubicBezierCurve(p0, p1, p2, p3);
    }
}
