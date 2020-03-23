package skeleton;

import skeleton.elements.ExtremityKind;

import java.util.Arrays;
import java.util.Random;

public class ExtremityData {
    private static int maxExtremityCount = 6;

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
    private UserInput userInput;

    // derived / calculated values
    private int flooredLegs;
    private int wings;
    private int arms;
    private int fins;
    private float floorHeight = 0f;
    private float flooredAnkleWristProbability;
    private ExtremityStartingPoints extremityStartingPoints;

    private Random random = new Random();

    public ExtremityData(double wingProbability, double flooredLegProbability,
                         double lengthUpperArm, double lengthLowerArm, double lengthHand,
                         double lengthUpperLeg, double lengthLowerLeg, double lengthFoot,
                         SpineData spine, UserInput userInput) {
        this.wingProbability = (float) wingProbability;
        this.flooredLegProbability = (float) flooredLegProbability;
        this.lengthUpperArm = (float) lengthUpperArm;
        this.lengthLowerArm = (float) lengthLowerArm;
        this.lengthHand = (float) lengthHand;
        this.lengthUpperLeg = (float) lengthUpperLeg;
        this.lengthLowerLeg = (float) lengthLowerLeg;
        this.lengthFoot = (float) lengthFoot;
        this.userInput = userInput;
        this.extremityStartingPoints = new ExtremityStartingPoints(userInput.hasSecondShoulder());
        calculateDerivedValues(spine);
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

    public boolean hasSecondShoulder() {
        return extremityStartingPoints.hasSecondShoulder();
    }

    /**
     * @param position is counting from the back (so pelvic in a normal animal would be 0)
     */
    public ExtremityKind[] getExtremityKindsForStartingPoint(int position) {
        return extremityStartingPoints.getExtremityKindsForStartingPoint(position);
    }

    /**
     * sets between 2 and 6 extremities
     */
    private void calculateDerivedValues(SpineData spine) {
        setUserSetExtremities();
        calculateAndSetLegsAndFloorHeight(spine);
        calculateAndSetWings();
        calculateAndSetArmsAndFins();
    }

    private void setUserSetExtremities() {
        if (userInput.hasFlooredLegs()) {
            flooredLegs = userInput.getFlooredLegs();
            extremityStartingPoints.setLegs(flooredLegs);
        }
        if (userInput.hasWings()) {
            wings = userInput.getWings();
            extremityStartingPoints.setWings(wings);
        }
        if (userInput.hasArms()) {
            arms = userInput.getArms();
            extremityStartingPoints.setArms(arms);
        }
        if (userInput.hasFins()) {
            fins = userInput.getFins();
            extremityStartingPoints.setFins(fins);
        }
    }

    /**
     * legs: user input or calculated by flooredLegProbability
     */
    private void calculateAndSetLegsAndFloorHeight(SpineData spine) {
        if (!userInput.hasFlooredLegs() && extremityStartingPoints.getFreeLegCount() > 0) {
            float probability = random.nextFloat();
            boolean moreLegs = probability > (flooredLegProbability % 1);
            if (moreLegs) {
                flooredLegs = (int) Math.ceil(flooredLegProbability);
            } else {
                flooredLegs = (int) Math.floor(flooredLegProbability);
            }
            flooredLegs = Math.min(extremityStartingPoints.getFreeLegCount(), flooredLegs);
            flooredLegs = Math.max(0, flooredLegs);
            extremityStartingPoints.setLegs(flooredLegs);
        }
        System.out.println("floored legs: " + flooredLegs);
        calculateFloorHeight(spine);
    }

    // Must be called _after_ number of floored legs is calculated
    private void calculateFloorHeight(SpineData spine) {
        float minFloorHeight = 0f;
        float bentRatio = 0.8f; // 1 means, that extremities can be completely vertical stretched out
        int backLegs = (int) Arrays.stream(extremityStartingPoints.getExtremityKindsForStartingPoint(0)).filter(e -> e == ExtremityKind.FLOORED_LEG).count();
        int frontLegs = (int) Arrays.stream(extremityStartingPoints.getExtremityKindsForStartingPoint(1)).filter(e -> e == ExtremityKind.FLOORED_LEG).count();

        if (backLegs > 0) {
            float pelvicHeight = spine.getBack().getControlPoint3().y;
            float legLength = lengthUpperLeg + lengthLowerLeg + lengthFoot;
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
            float armLength = lengthUpperArm + lengthLowerArm + lengthHand;
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
        if (!userInput.hasWings() && extremityStartingPoints.getFreeWingCount() > 0) {
            int freeWingCount = extremityStartingPoints.getFreeWingCount();
            if (random.nextDouble() < wingProbability) {
                wings = 1;
            }
            if (userInput.hasSecondShoulder() && freeWingCount > 1 && random.nextDouble() < wingProbability) {
                wings++;
            }
            extremityStartingPoints.setWings(wings);
        }
        System.out.println("wings: " + wings);
    }

    /**
     * arms: user input or calculated by wingProbability (but then max 1 per shoulder)
     * fins: user input or one per empty extremity starting point
     */
    private void calculateAndSetArmsAndFins() {
        if (!userInput.hasArms() && extremityStartingPoints.getFreeArmCount() > 0) {
            int freeArmCount = extremityStartingPoints.getFreeWingCount();
            if (random.nextDouble() < wingProbability) {
                arms = 1;
            }
            if (userInput.hasSecondShoulder() && freeArmCount > 1 && random.nextDouble() < wingProbability) {
                arms++;
            }
            extremityStartingPoints.setArms(arms);
        }

        if (!userInput.hasFins()) {
            for (int i = 0; i < extremityStartingPoints.getStartingPointCount(); i++) {
                if (extremityStartingPoints.getFreeExtremityCountAtPosition(i) >= 2) {
                    extremityStartingPoints.setKindAtPosition(ExtremityKind.FIN, i);
                    fins += 1;
                }
            }
        }
        System.out.println("arms: " + arms);
        System.out.println("fins: " + fins);
    }
}
