package skeleton;

import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.Joint;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.terminal.Rib;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Vertebra;
import skeleton.replacementRules.ReplacementRule;
import skeleton.replacementRules.RuleDictionary;
import util.BoundingBox;
import util.TransformationMatrix;
import util.pca.PcaHandler;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;
import java.util.*;

public class SkeletonGenerator {

    private ArrayList<TerminalElement> terminalParts;
    private ArrayList<NonTerminalElement> nonTerminalParts;
    private RuleDictionary ruleDictionary;

    private PcaHandler pcaHandler;
    private SkeletonMetaData skeletonMetaData;

    private boolean calculatedMirroredElements = false;
    private static Random random = new Random();
    private int stepCount = 0;
    private int nextBoneId = 0;

    public SkeletonGenerator(PcaHandler pcaHandler, UserInput userInput) {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>();
        this.nonTerminalParts.add(new WholeBody(this));
        this.ruleDictionary = new RuleDictionary();
        this.pcaHandler = pcaHandler;
        this.skeletonMetaData = new SkeletonMetaData(pcaHandler.getRandomPcaDataPoint(), userInput);
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

    public SkeletonMetaData getSkeletonMetaData() {
        return skeletonMetaData;
    }

    public int getStepCount() {
        return stepCount;
    }

    public String toString() {
        SkeletonPart rootElement = getRootElement();
        return recursiveToString("|-- ", rootElement);
    }

    private String recursiveToString(String depth, SkeletonPart currentElement) {

        StringBuilder skeleton = new StringBuilder("\u001B[32m").append(depth);
        if (currentElement.canBeMirrored()) {
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
        if (currentElement.isTerminal()) {
            Point3f position = ((TerminalElement) currentElement).getWorldPosition();
            skeleton.append("position: ").append(position);
        }

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
        if (root.canBeMirrored()) {
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
        if (currentElement.canBeMirrored()) {
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

    /**
     * The vertebra are generated from the left side of the interval to the right.
     * If the left float is greater than the right one, then the vertebra are generated in negative direction on the curve.
     * Child vertebrae are added to their parents.
     * For scale SpineData values for y and z are used, x is the maximum space available.
     * @param interval has to contain two floats between 0 and 1
     * @param vertebraCount number of vertebra that shall be generated (equally spaced)
     * @param firstParent element that shall be parent of the first vertebra generated
     * @return the generated vertebra
     */
    public List<TerminalElement> generateVertebraeInInterval(NonTerminalElement ancestor, SpinePart spinePart, Tuple2f interval, int vertebraCount,
                                                             TerminalElement firstParent, Joint firstParentJoint) {

        ArrayList<TerminalElement> generatedParts = new ArrayList<>();
        if (interval.x < 0 || interval.y < 0 || interval.x > 1 || interval.y > 1) {
            System.err.println(String.format("Invalid interval [%f, %f]", interval.x, interval.y));
            return generatedParts;
        }

        BoundingBox boundingBox = new BoundingBox(new Vector3f(1f, SpineData.vertebraYScale, SpineData.vertebraZScale)); // x scale is replaced anyway

        float totalIntervalLength = Math.abs(interval.y - interval.x);
        float sign = interval.y > interval.x ? 1f : -1f;
        float oneIntervalStep = sign * totalIntervalLength / (float) vertebraCount;;

        Vertebra parent = null;
        for (int i = 0; i < vertebraCount; i++) {

            float spinePosition;
            float childSpineEndPosition;
            if (parent == null) {
                spinePosition = interval.x;
                childSpineEndPosition = spinePosition + oneIntervalStep;
                if (firstParentJoint instanceof SpineOrientedJoint) {
                    ((SpineOrientedJoint) firstParentJoint).setChildSpineEndPosition(childSpineEndPosition, spinePart);
                }
            } else {
                spinePosition = parent.getSpineJoint().getSpinePosition();
                if (i == vertebraCount-1) {
                    childSpineEndPosition = interval.y;
                } else {
                    childSpineEndPosition = spinePosition + oneIntervalStep;
                }
                parent.getSpineJoint().setChildSpineEndPosition(childSpineEndPosition, spinePart);
            }

            BoundingBox childBox = boundingBox.cloneBox();
            Point2f startPosition = ancestor.getGenerator().getSkeletonMetaData().getSpine().getPart(spinePart).apply(spinePosition);
            Point2f endPosition = ancestor.getGenerator().getSkeletonMetaData().getSpine().getPart(spinePart).apply(childSpineEndPosition);
            childBox.setXLength((float) Math.sqrt(
                    Math.pow(startPosition.x - endPosition.x, 2) +
                    Math.pow(startPosition.y - endPosition.y, 2)));

            Joint joint = parent == null ? firstParentJoint : parent.getSpineJoint();
            TransformationMatrix transform = joint.calculateChildTransform(childBox);
            transform.translate(Vertebra.getLocalTranslationFromJoint(childBox));

            Vertebra child;
            TerminalElement parentElement = parent == null ? firstParent : parent;
            child = new Vertebra(transform, childBox, parentElement, ancestor, sign > 0, spinePart, childSpineEndPosition);
            parentElement.addChild(child);
            generatedParts.add(child);

            boolean rib = spinePart == SpinePart.BACK && getSkeletonMetaData().getSpine().isInChestInterval(spinePosition);
            if (rib) {
                generatedParts.add(generateRibForVertebra(ancestor, child));
            }

            parent = child;
        }

        return generatedParts;
    }

    private Rib generateRibForVertebra(NonTerminalElement ancestor, Vertebra vertebra) {
        float xzScale = vertebra.getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(
                xzScale,
                Rib.yScale,
                xzScale
        ));
        TransformationMatrix transform = vertebra.getRibJoint().calculateChildTransform(boundingBox);
        transform.translate(Rib.getLocalTranslationFromJoint(boundingBox));

        Rib rib = new Rib(transform, boundingBox, vertebra, ancestor);
        vertebra.addChild(rib);

        return rib;
    }
}
