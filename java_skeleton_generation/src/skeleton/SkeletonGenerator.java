package skeleton;

import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.nonterminal.NonTerminalElement;
import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Vertebra;
import skeleton.replacementRules.ReplacementRule;
import skeleton.replacementRules.RuleDictionary;
import util.BoundingBox;
import util.CubicBezierCurve;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
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
        this.nonTerminalParts.add(new WholeBody(new TransformationMatrix(), this));
        this.ruleDictionary = new RuleDictionary();
        this.spineLocation = generateSpine();
    }

    /**
     * @return if step could be done
     */
    public boolean doOneStep() {
        if (isFinished()) {
            return false;
        }

        stepCount++;
        NonTerminalElement nonTerminalElement = nonTerminalParts.remove(nonTerminalParts.size() - 1);

        List<ReplacementRule> rules = ruleDictionary.getRules(nonTerminalElement.getKind());
        if (rules == null || rules.isEmpty()) {
            System.err.println("Non terminal " + nonTerminalElement.getKind() + " has no applicable rule!");
            nonTerminalParts.add(nonTerminalElement);
            return false;
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
        return true;
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

    public TerminalElement getTerminalRootElement() {
        Object[] partsWithoutParent = terminalParts.stream().filter(part -> !part.hasParent()).toArray();
        if (partsWithoutParent.length > 0) {
            if (partsWithoutParent.length > 1) {
                System.err.println("Found several skeleton parts without parent!");
            }
            return (TerminalElement) partsWithoutParent[0];
        }

        return null;
    }

    public SkeletonPart getRootElement() {
        TerminalElement terminalRoot = getTerminalRootElement();
        if (terminalRoot != null) {
            return terminalRoot;
        }

        Object[] partsWithoutParent = nonTerminalParts.stream().filter(part -> !part.hasParent()).toArray();
        if (partsWithoutParent.length > 0) {
            if (partsWithoutParent.length > 1) {
                System.err.println("Found several skeleton parts without parent!");
            }
            return (NonTerminalElement) partsWithoutParent[0];
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
        if (currentElement.isTerminal()) {
            TerminalElement currentTerminal = (TerminalElement) currentElement;
            skeleton.append(", bounding box scale: ");
            BoundingBox boundingBox = currentTerminal.getBoundingBox();
            skeleton.append(boundingBox.getXLength()).append(", ");
            skeleton.append(boundingBox.getYLength()).append(", ");
            skeleton.append(boundingBox.getZLength());
        }

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

    /**
     * The vertebra are generated from the left side of the interval to the right.
     * If the left float is greater than the right one, then the vertebra are generated in negative direction on the curve.
     * @param interval has to contain two floats between 0 and 1
     * @param vertebraCount number of vertebra that shall be generated (equally spaced)
     * @param firstParent element that shall be parent of the first vertebra generated or a dummy parent from which only the transform is used
     * @param dummyParent indicates if the parent of the first generated vertebra shall be the parent or null
     * @param lastChild if present, the child of the last vertebra generated
     * @return the generated vertebra
     */
    public List<TerminalElement> generateVertebraInInterval(WholeBody wholeBody, Tuple2f interval, int vertebraCount,
                                                             TerminalElement firstParent, boolean dummyParent, Optional<SkeletonPart> lastChild) {

        TerminalElement parent = firstParent;

        BoundingBox boundingBox = BoundingBox.defaultBox();
        Vector3f localBoxTranslation = new Vector3f(0f, -boundingBox.getYLength() / 2f, 0f); // negative half box height
        Vector3f negativeHalfBoxWidth = new Vector3f(0f, 0f, -boundingBox.getZLength() / 2f);
        localBoxTranslation.add(negativeHalfBoxWidth);

        CubicBezierCurve spine = wholeBody.getGenerator().getSpineLocation();

        ArrayList<TerminalElement> generatedParts = new ArrayList<>();
        float intervalLength = Math.abs(interval.y - interval.x);
        float sign = interval.y > interval.x ? 1f : -1f;

        for (int i = 0; i < vertebraCount; i++) {
            float t = interval.x + sign * (float) i / (float) vertebraCount * intervalLength;
            float tPlus1 = t + sign * 1f / (float) vertebraCount * intervalLength;

            // we have the world position of the spine and we have to get something that is relative to the parent
            TransformationMatrix transform = TransformationMatrix.getInverse(parent.getWorldTransform());

            float angle = getSpineAngle(spine, t, tPlus1);
            transform.rotateAroundZ(angle);
            Vector3f position = new Vector3f(spine.apply3d(t)); // world position
            transform.translate(position);

            BoundingBox childBox = boundingBox.cloneBox();

            Point3f jointRotationPoint = new Point3f(position);
            Vector3f offset = new Vector3f(childBox.getYVector());
            offset.add(childBox.getZVector());
            offset.scale(0.5f);
            offset.add(childBox.getXVector());
            jointRotationPoint.add(offset);

            Vertebra child;
            if (i == 0 && dummyParent) { // this is the real parent (dummy parent was used to calculate it)
                child = new Vertebra(transform, jointRotationPoint, childBox, null, wholeBody); // root
            } else {
                child = new Vertebra(transform, jointRotationPoint, childBox, parent, wholeBody);
                parent.addChild(child);
            }

            // Move child down a negative half bounding box height
            // so that spine is not at the bottom of the vertebra but pierces
            // the bounding box in the center of the left and right side.
            // Do this after the generation of the child to be able to use
            // the world transform method of the child.
            Vector3f transformedBoxTranslation = new Vector3f(localBoxTranslation);
            child.getWorldTransform().applyOnVector(transformedBoxTranslation);
            child.getTransform().translate(transformedBoxTranslation);

            generatedParts.add(child);
            parent = child;
        }

        lastChild.ifPresent(
                skeletonPart -> generatedParts.get(generatedParts.size() - 1).addChild(skeletonPart)
        );

        return generatedParts;
    }

    /**
     * @param t1 first point on spine
     * @param t2 second point on spine
     * @return angle between spine vector (from first to second point on spine) and the vector (1,0,0)
     */
    private float getSpineAngle(CubicBezierCurve spine, float t1, float t2) {
        Point3f position1 = spine.apply3d(t1);
        Point3f position2 = spine.apply3d(t2);
        Point3f diff = position2;
        diff.sub(position1);
        Vector3f spineVector = new Vector3f(diff);

        float angle = spineVector.angle(new Vector3f(1f, 0f, 0f));

        // determine in which direction we should turn
        if (position1.y > position2.y) {
            angle = -angle;
        }
        return angle;
    }
}
