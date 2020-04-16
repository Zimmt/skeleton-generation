package skeleton;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.NonTerminalElement;
import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.terminal.TerminalElement;
import skeleton.replacementRules.ReplacementRule;
import skeleton.replacementRules.RuleDictionary;
import util.BoundingBox;
import util.pca.PcaDataPoint;
import util.pca.PcaHandler;

import javax.vecmath.Point3f;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class SkeletonGenerator {

    private ArrayList<TerminalElement> terminalParts;
    private ArrayList<NonTerminalElement> nonTerminalParts;
    private RuleDictionary ruleDictionary;

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
        this.skeletonMetaData = new SkeletonMetaData(pcaHandler, userInput);
    }

    /**
     * reads meta data from file
     */
    public SkeletonGenerator(String skeletonMetaDataFilePath) throws IOException {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>();
        this.nonTerminalParts.add(new WholeBody(this));
        this.ruleDictionary = new RuleDictionary();
        this.skeletonMetaData = readMetaDataFromFile(skeletonMetaDataFilePath);
    }

    /**
     * Reads skeleton meta data from file and creates a variation of that based on pca results
     */
    public SkeletonGenerator(String skeletonMetaDataFilePath, List<PcaDataPoint> pcaInputData) throws IOException {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>();
        this.nonTerminalParts.add(new WholeBody(this));
        this.ruleDictionary = new RuleDictionary();
        this.skeletonMetaData = readMetaDataFromFile(skeletonMetaDataFilePath).newWithVariation(pcaInputData);
    }

    /**
     * @param pcaDataPointName is used to construct skeleton meta data (user input is ignored except from head kind)
     * if data point with this name does not exist skeleton meta data is constructed only from user input
     */
    public SkeletonGenerator(PcaHandler pcaHandler, String pcaDataPointName, UserInput userInput, boolean createVariation) {
        this.terminalParts = new ArrayList<>();
        this.nonTerminalParts = new ArrayList<>();
        this.nonTerminalParts.add(new WholeBody(this));
        this.ruleDictionary = new RuleDictionary();
        PcaDataPoint point = pcaHandler.getPcaDataPointByName(pcaDataPointName);
        if (point == null) {
            System.err.println("This example data point does not exist!");
            this.skeletonMetaData = new SkeletonMetaData(pcaHandler, userInput);
        } else  {
            this.skeletonMetaData = new SkeletonMetaData(pcaHandler, point, userInput);
        }
        if (createVariation) {
            this.skeletonMetaData = skeletonMetaData.newWithVariation(pcaHandler.getDataPoints());
        }
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

    private SkeletonMetaData readMetaDataFromFile(String filePath) throws IOException {
        SkeletonMetaData metaData = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            metaData = (SkeletonMetaData) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Could not read skeleton meta data from file!");
            e.printStackTrace();
        }
        return metaData;
    }
}
