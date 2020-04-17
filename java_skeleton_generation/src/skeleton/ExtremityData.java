package skeleton;

import skeleton.elements.ExtremityKind;
import skeleton.replacementRules.ExtremityPositioning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ExtremityData implements Serializable {
    private final static float minNeckLengthForSecondShoulder = 100f;

    // PCA data
    private float wingProbability; // [0, 1]
    private float flooredLegProbability; // #legs/2, [0,2]

    private float lengthUpperArm; // [0, 1000]
    private float lengthLowerArm; // [0, 1000]
    private float lengthHand; // [0, 1000]

    private float lengthUpperLeg; // [0, 1000]
    private float lengthLowerLeg; // [0, 1000]
    private float lengthFoot; // [0, 1000]

    // non-PCA / user input
    private transient UserInput userInput; // is empty if the class is reconstructed by deserialization

    // derived / calculated values
    private int flooredLegs;
    private int wings;
    private int arms;
    private int fins;

    private float floorHeight = 0f;
    private float flooredAnkleWristProbability;
    private boolean shoulderOnNeck;
    private final ExtremityStartingPoints extremityStartingPoints;

    private final transient Random random = new Random();

    public ExtremityData(double wingProbability, double flooredLegProbability,
                         double lengthUpperArm, double lengthLowerArm, double lengthHand,
                         double lengthUpperLeg, double lengthLowerLeg, double lengthFoot,
                         SpineData spine, UserInput userInput) {
        this.wingProbability = (float) Math.max(wingProbability, 0);
        this.flooredLegProbability = (float) Math.max(flooredLegProbability, 0);
        initializeExtremityLengths(lengthUpperArm, lengthLowerArm, lengthHand, lengthUpperLeg, lengthLowerLeg, lengthFoot);
        this.userInput = userInput;
        this.shoulderOnNeck = spine.hasNeck(); // there can be no second shoulder if there is no neck!
        if (userInput.getSecondShoulder() != null) {
            shoulderOnNeck = shoulderOnNeck && userInput.getSecondShoulder();
            if (shoulderOnNeck != userInput.getSecondShoulder()) {
                System.out.println("Could not generate a second shoulder as there is no neck to place it on!");
            }
        } else {
            shoulderOnNeck = shoulderOnNeck && spine.getNeck().apply(0f).y - spine.getNeck().apply(1f).y > minNeckLengthForSecondShoulder;
        }
        List<Integer> forbiddenPositions = new ArrayList<>();
        if (getBackExtremityLength() == 0) {
            forbiddenPositions.add(0);
        }
        if (getFrontExtremityLength() == 0) {
            forbiddenPositions.add(1);
            forbiddenPositions.add(2);
        }
        this.extremityStartingPoints = new ExtremityStartingPoints(shoulderOnNeck, userInput.twoExtremitiesPerGirdleAllowed(), forbiddenPositions);
        calculateDerivedValues(spine);
    }

    /**
     * leaves wing and floored legs probability, user input and number of extremities empty
     * (but they are not needed as extremityStartingPoints is provided)
     */
    public ExtremityData(double lengthUpperArm, double lengthLowerArm, double lengthHand,
                         double lengthUpperLeg, double lengthLowerLeg, double lengthFoot,
                         ExtremityStartingPoints extremityStartingPoints, SpineData spine) {
        initializeExtremityLengths(lengthUpperArm, lengthLowerArm, lengthHand, lengthUpperLeg, lengthLowerLeg, lengthFoot);
        this.extremityStartingPoints = extremityStartingPoints;
        calculateFloorHeightAndAnkleWristProb(spine);
    }

    public float getLengthUpperArm() {
        return lengthUpperArm;
    }

    public float getLengthLowerArm() {
        return lengthLowerArm;
    }

    public float getLengthHand() {
        return lengthHand;
    }

    public float getLengthUpperLeg() {
        return lengthUpperLeg;
    }

    public float getLengthLowerLeg() {
        return lengthLowerLeg;
    }

    public float getLengthFoot() {
        return lengthFoot;
    }

    public float getFloorHeight() {
        return floorHeight;
    }

    public float getFlooredAnkleWristProbability() {
        return flooredAnkleWristProbability;
    }

    public void setFlooredAnkleWristProbability(boolean probability) {
        this.flooredAnkleWristProbability = probability ? 1f : 0f;
    }

    public boolean hasShoulderOnNeck() {
        return shoulderOnNeck;
    }

    public boolean hasWings() {
        return wings > 0;
    }

    /**
     * @param position is counting from the back (so pelvic in a normal animal would be 0)
     */
    public ExtremityPositioning[] getExtremityPositioningsForStartingPoint(int position) {
        return extremityStartingPoints.getExtremityPositioningsForStartingPoint(position);
    }

    public ExtremityStartingPoints getExtremityStartingPoints() {
        return extremityStartingPoints;
    }

    /**
     * sets between 2 and 6 extremities
     */
    private void calculateDerivedValues(SpineData spine) {
        if (userInput != null) {
            setUserSetExtremities();
            calculateAndSetLegsAndFloorHeight(spine);
            calculateAndSetWings();
            calculateAndSetArmsAndFins();
            postprocessExtremities();
        } else {
            System.out.println("Cannot calculate derived values without user input!");
        }
    }

    /**
     * Distribute extremities.
     */
    private void postprocessExtremities() {
        extremityStartingPoints.distributeExtremities();
    }

    private void setUserSetExtremities() {
        if (userInput.hasFlooredLegs()) {
            flooredLegs = userInput.getFlooredLegs();
            extremityStartingPoints.setKind(ExtremityKind.LEG, flooredLegs);
        }
        if (userInput.hasWings()) {
            wings = userInput.getWings();
            extremityStartingPoints.setKind(ExtremityKind.WING, wings);
        }
        if (userInput.hasArms()) {
            arms = userInput.getArms();
            extremityStartingPoints.setKind(ExtremityKind.ARM, arms);
        }
        if (userInput.hasFins()) {
            fins = userInput.getFins();
            extremityStartingPoints.setKind(ExtremityKind.FIN, fins);
        }
    }

    /**
     * legs: user input or calculated by flooredLegProbability
     */
    private void calculateAndSetLegsAndFloorHeight(SpineData spine) {
        if (!userInput.hasFlooredLegs() && extremityStartingPoints.getFreeCountForKind(ExtremityKind.LEG) > 0) {
            float probability = random.nextFloat();
            boolean moreLegs = probability > (flooredLegProbability % 1);
            if (moreLegs) {
                flooredLegs = (int) Math.ceil(flooredLegProbability);
            } else {
                flooredLegs = (int) Math.floor(flooredLegProbability);
            }
            flooredLegs = Math.min(extremityStartingPoints.getFreeCountForKind(ExtremityKind.LEG), flooredLegs);
            flooredLegs = Math.max(0, flooredLegs);
            extremityStartingPoints.setKind(ExtremityKind.LEG, flooredLegs);
        }
        System.out.println("floored legs: " + flooredLegs);
        calculateFloorHeightAndAnkleWristProb(spine);
    }

    // Must be called _after_ number of floored legs is set in extremityStartingPoints
    private void calculateFloorHeightAndAnkleWristProb(SpineData spine) {
        float minFloorHeight = 0f;
        float bentRatio = 0.8f; // 1 means, that extremities can be completely vertical stretched out
        int backLegs = (int) Arrays.stream(extremityStartingPoints.getExtremityPositioningsForStartingPoint(0)).filter(e -> e.getExtremityKind() == ExtremityKind.LEG).count();
        int frontLegs = (int) Arrays.stream(extremityStartingPoints.getExtremityPositioningsForStartingPoint(1)).filter(e -> e.getExtremityKind() == ExtremityKind.LEG).count();

        if (backLegs > 0) {
            float pelvicHeight = spine.getBack().getControlPoint3().y;
            float legLength = getBackExtremityLength();
            if (lengthUpperLeg + lengthLowerLeg >= pelvicHeight) {
                flooredAnkleWristProbability = 1f;
            } else if (legLength < pelvicHeight) {
                flooredAnkleWristProbability = 0f;
                minFloorHeight = pelvicHeight - bentRatio * legLength;
            } else {
                flooredAnkleWristProbability = (legLength - pelvicHeight) / lengthFoot;
                minFloorHeight = pelvicHeight - bentRatio * lengthUpperLeg - bentRatio * lengthLowerLeg;
            }
        }

        if (frontLegs > 0) {
            float shoulderHeight = spine.getBack().getControlPoint0().y;
            float armLength = getFrontExtremityLength();
            if (flooredAnkleWristProbability >= 1f || lengthUpperArm + lengthLowerArm >= shoulderHeight) {
                flooredAnkleWristProbability = 1f;
            } else if (flooredAnkleWristProbability <= 0f || armLength < shoulderHeight) {
                if (flooredAnkleWristProbability > 0f) {
                    //System.out.println("arms are too short, but legs not");
                    minFloorHeight = shoulderHeight - bentRatio*armLength;
                } else {
                    minFloorHeight = Math.max(minFloorHeight, shoulderHeight - bentRatio*armLength);
                }
                flooredAnkleWristProbability = 0f;
            } else {
                float flooredWristProbability = (armLength - shoulderHeight) / lengthHand;
                flooredAnkleWristProbability = (flooredWristProbability + flooredAnkleWristProbability) / 2f;
                minFloorHeight = Math.max(minFloorHeight, shoulderHeight - bentRatio*lengthUpperArm - bentRatio*lengthLowerArm);
            }
        }

        if (minFloorHeight > 0f) {
            floorHeight = minFloorHeight;
        }
        System.out.println("Floor height: " + floorHeight);
        //System.out.println("floored ankle probability: " + flooredAnkleWristProbability);

    }

    /**
     * wings: user input or calculated by wingProbability (but then max 1 per shoulder)
     */
    private void calculateAndSetWings() {
        if (!userInput.hasWings() && extremityStartingPoints.getFreeCountForKind(ExtremityKind.WING) > 0) {
            int freeWingCount = extremityStartingPoints.getFreeCountForKind(ExtremityKind.WING);
            if (random.nextDouble() < wingProbability) {
                wings = 1;
            }
            if (shoulderOnNeck && freeWingCount > 1 && random.nextDouble() < wingProbability) {
                wings++;
            }
            extremityStartingPoints.setKind(ExtremityKind.WING, wings);
        }
        System.out.println("wings: " + wings);
    }

    /**
     * arms: user input or calculated by wingProbability (but then max 1 per shoulder)
     * fins: user input or one per empty extremity starting point
     */
    private void calculateAndSetArmsAndFins() {
        if (!userInput.hasArms() && extremityStartingPoints.getFreeCountForKind(ExtremityKind.ARM) > 0) {
            int freeArmCount = extremityStartingPoints.getFreeCountForKind(ExtremityKind.ARM);
            if (random.nextDouble() < wingProbability) {
                arms = 1;
            }
            if (shoulderOnNeck && freeArmCount > 1 && random.nextDouble() < wingProbability) {
                arms++;
            }
            extremityStartingPoints.setKind(ExtremityKind.ARM, arms);
        }

        if (!userInput.hasFins()) {
            for (int i = 0; i < extremityStartingPoints.getStartingPointCount(); i++) {
                if (extremityStartingPoints.getFreeCountAtPosition(i) >= 2) {
                    extremityStartingPoints.setKindAtPosition(ExtremityKind.FIN, i);
                    fins += 1;
                }
            }
        }
        System.out.println("arms: " + arms);
        System.out.println("fins: " + fins);
    }

    private void initializeExtremityLengths(double lengthUpperArm, double lengthLowerArm, double lengthHand,
                                           double lengthUpperLeg, double lengthLowerLeg, double lengthFoot) {
        this.lengthUpperArm = (float) Math.max(lengthUpperArm, 0);
        this.lengthLowerArm = (float) Math.max(lengthLowerArm, 0);
        this.lengthHand = (float) Math.max(lengthHand, 0);
        this.lengthUpperLeg = (float) Math.max(lengthUpperLeg, 0);
        this.lengthLowerLeg = (float) Math.max(lengthLowerLeg, 0);
        this.lengthFoot = (float) Math.max(lengthFoot, 0);
    }

    private float getFrontExtremityLength() {
        return lengthUpperArm + lengthLowerArm + lengthHand;
    }

    private float getBackExtremityLength() {
        return lengthUpperLeg + lengthLowerLeg + lengthFoot;
    }
}
