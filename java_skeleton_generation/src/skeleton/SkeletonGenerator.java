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
    private CubicBezierCurve spine;
    private boolean calculatedMirroredElements = false;

    private static Random random = new Random();
    private int stepCount = 0;
    private int nextBoneId = 0;

    public SkeletonGenerator() {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>();
        this.nonTerminalParts.add(new WholeBody(new TransformationMatrix(), this));
        this.ruleDictionary = new RuleDictionary();
        this.spine = generateSpine();
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

    public CubicBezierCurve getSpine() {
        return spine;
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

    public void calculateMirroredElements() {
        if (!isFinished() || calculatedMirroredElements) {
            System.err.println("Cannot calculate mirrored elements in an unfinished skeleton");
            return;
        }

        TerminalElement root = getTerminalRootElement();
        if (root.isMirrored()) {
            System.err.println("A root element that has to be mirrored is not allowed!");
        }

        // call with null is possible as 'parent' is only needed when element is mirrored
        List<List<TerminalElement>> childrenToAdd = recursiveCalculationOfMirroredElements(null, Optional.empty(), root);

        for (List<TerminalElement> parentChild : childrenToAdd) { // these are lists with 2 elements
            parentChild.get(0).addChild(parentChild.get(1));
            terminalParts.add(parentChild.get(1));
        }

        calculatedMirroredElements = true;
    }

    /**
     * @return a list of tuples (parent, child) where the child should be added to the parent
     * (to avoid changing objects that are iterated over
     */
    private List<List<TerminalElement>> recursiveCalculationOfMirroredElements(TerminalElement parent, Optional<TerminalElement> mirroredParent, TerminalElement currentElement) {
        List<List<TerminalElement>> childrenToAdd = new ArrayList<>();
        if (!currentElement.calculateWorldTransform().getHandedness()) {
            System.err.println("Original element has left handed coordinate system?!");
        }

        Optional<TerminalElement> mirroredElement = Optional.empty();
        if (currentElement.isMirrored()) {
            mirroredElement = Optional.of(currentElement.calculateMirroredElement(parent, mirroredParent));
            if (!mirroredElement.get().calculateWorldTransform().getHandedness()) {
                System.err.println("Generated element with left handed coordinate system!");
            }
            if (mirroredParent.isEmpty()) {
                childrenToAdd.add(Arrays.asList(parent, mirroredElement.get()));
            } else {
                childrenToAdd.add(Arrays.asList(mirroredParent.get(), mirroredElement.get()));
            }
        }

        // currentElement and it's children are always "real" elements not mirrored ones
        for (SkeletonPart child : currentElement.getChildren()) {
            if (!child.isTerminal()) {
                System.err.println("There is a non terminal element in a finished skeleton!");
                return childrenToAdd;
            }

            // if current element is mirrored, all children of it that are mirrored
            // are children of the mirrored version of the current element
            childrenToAdd.addAll(recursiveCalculationOfMirroredElements(currentElement, mirroredElement, (TerminalElement) child));
        }

        return childrenToAdd;
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
     * @return the generated vertebra
     */
    public List<TerminalElement> generateVertebraeInInterval(NonTerminalElement ancestor, Tuple2f interval, int vertebraCount,
                                                             TerminalElement firstParent, boolean dummyParent) {

        TerminalElement parent = firstParent;

        BoundingBox boundingBox = BoundingBox.defaultBox();
        Vector3f localBoxTranslation = new Vector3f(0f, -boundingBox.getYLength() / 2f, 0f); // negative half box height
        Vector3f negativeHalfBoxWidth = new Vector3f(0f, 0f, -boundingBox.getZLength() / 2f);
        localBoxTranslation.add(negativeHalfBoxWidth);

        ArrayList<TerminalElement> generatedParts = new ArrayList<>();
        float intervalLength = Math.abs(interval.y - interval.x);
        float sign = interval.y > interval.x ? 1f : -1f;

        for (int i = 0; i < vertebraCount; i++) {

            float left = interval.x + sign * (float) i / (float) vertebraCount * intervalLength;
            float right = left + sign * 1f / (float) vertebraCount * intervalLength;

            Tuple2f currentVertebraInterval;
            if (sign > 0) {
                currentVertebraInterval = new Point2f(left, right);
            } else {
                currentVertebraInterval = new Point2f(right, left);
            }

            TransformationMatrix transform = generateTransformForElementInSpineInterval(currentVertebraInterval, parent);
            transform.translate(localBoxTranslation);

            BoundingBox childBox = boundingBox.cloneBox();

            Point3f jointRotationPoint;
            if (sign > 0) { // the rotation point is at the right side of the parent
                jointRotationPoint = new Point3f(childBox.getXLength(), childBox.getYLength() / 2f, childBox.getZLength() / 2f);
            } else { // the rotation point is at the left side of the parent
                jointRotationPoint = new Point3f(0f, childBox.getYLength() / 2f, childBox.getZLength() / 2f);
            }

            Vertebra child;
            if (i == 0 && dummyParent) { // this is the real parent (dummy parent was used to calculate it)
                child = new Vertebra(transform, jointRotationPoint, childBox, null, ancestor); // root
            } else {
                child = new Vertebra(transform, jointRotationPoint, childBox, parent, ancestor);
                parent.addChild(child);
            }

            generatedParts.add(child);
            parent = child;
        }

        return generatedParts;
    }

    /**
     * The position of transform is the left point of the interval on the spine.
     * @param interval [x, y] with x < y
     */
    public TransformationMatrix generateTransformForElementInSpineInterval(Tuple2f interval, TerminalElement parent) {

        float angle = getSpineAngle(interval.x, interval.y);
        Vector3f position = new Vector3f(spine.apply3d(interval.x)); // world position

        TransformationMatrix inverseParentWorldTransform = TransformationMatrix.getInverse(parent.calculateWorldTransform());

        TransformationMatrix psi = new TransformationMatrix(position);
        psi.rotateAroundZ(angle);

        TransformationMatrix result = TransformationMatrix.multiply(inverseParentWorldTransform, psi);

        return result;
    }

    /**
     * @param t1 first point on spine
     * @param t2 second point on spine
     * @return angle between spine vector (from first to second point on spine) and the vector (1,0,0)
     */
    private float getSpineAngle(float t1, float t2) {
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
