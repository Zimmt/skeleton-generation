package skeleton;

import skeleton.elements.ExtremityKind;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ExtremityData {
    private static int maxExtremityCount = 4;

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

    // a two element array of extremity kinds for each extremity starting point
    // the first entry concerns the extremity starting point that is nearest to the tail
    private List<ExtremityKind[]> extremityKindsForStartingPoints;

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

    /**
     * @param position is counting from the back (so pelvic in a normal animal would be 0)
     */
    public ExtremityKind[] getExtremityKindsForStartingPoint(int position) {
        return extremityKindsForStartingPoints.get(position);
    }

    /**
     * sets between 2 and 4 extremities
     */
    private void calculateDerivedValues(SpineData spine) {
        calculateWings();
        calculateLegsAndFloorHeight(spine);
        calculateArmsAndFins();
        calculateExtremityPositions();
    }

    private void calculateExtremityPositions() {
        extremityKindsForStartingPoints = Arrays.asList(new ExtremityKind[2], new ExtremityKind[2]);
        int legCount = 0;
        int finCount = 0;
        int wingCount = 0;
        int armCount = 0;

        // first back extremity
        if (flooredLegs > 0) {
            extremityKindsForStartingPoints.get(0)[0] = ExtremityKind.FLOORED_LEG;
            legCount++;
        } else if (fins > 0) {
            extremityKindsForStartingPoints.get(0)[0] = ExtremityKind.FIN;
            finCount++;
        }

        // first front extremity
        if (wings > 0) {
            extremityKindsForStartingPoints.get(1)[0] = ExtremityKind.WING;
            wingCount++;
        } else if (arms > 0) {
            extremityKindsForStartingPoints.get(1)[0] = ExtremityKind.NON_FLOORED_LEG;
            armCount++;
        } else if (flooredLegs > legCount) {
            extremityKindsForStartingPoints.get(1)[0] = ExtremityKind.FLOORED_LEG;
            legCount++;
        } else if (fins > finCount) {
            extremityKindsForStartingPoints.get(1)[0] = ExtremityKind.FIN;
            finCount++;
        }

        // second front extremity
        if (wings > wingCount) {
            extremityKindsForStartingPoints.get(1)[1] = ExtremityKind.WING;
            wingCount++;
        } else if (arms > armCount) {
            extremityKindsForStartingPoints.get(1)[1] = ExtremityKind.NON_FLOORED_LEG;
            armCount++;
        } else if (flooredLegs > legCount) {
            extremityKindsForStartingPoints.get(1)[1] = ExtremityKind.FLOORED_LEG;
            legCount++;
        } else if (fins > finCount) {
            extremityKindsForStartingPoints.get(1)[1] = ExtremityKind.FIN;
            finCount++;
        }

        // second back extremity
        if (flooredLegs > legCount) {
            extremityKindsForStartingPoints.get(0)[1] = ExtremityKind.FLOORED_LEG;
            legCount++;
        } else if (fins > finCount) {
            extremityKindsForStartingPoints.get(0)[1] = ExtremityKind.FIN;
            finCount++;
        }

        if (legCount + armCount + wingCount + finCount != flooredLegs + arms + wings + fins) {
            System.err.println("Something went wrong with extremity sorting!");
        }
        if (extremityKindsForStartingPoints.get(0)[0] == null && extremityKindsForStartingPoints.get(0)[1] == null) {
            System.err.println("Back extremities missing?");
        }
        if (extremityKindsForStartingPoints.get(1)[0] == null && extremityKindsForStartingPoints.get(1)[1] == null) {
            System.err.println("Front extremities missing?");
        }
    }

    /**
     * arms: user input or calculated by wingProbability if there is still space for arms
     * fins: user input or, if extremity count < 2, 2 - extremity count
     */
    private void calculateArmsAndFins() {
        if (userInput.hasArms()) {
            arms = userInput.getArms();
        } else if (userInput.getTotal() < maxExtremityCount && wings < 2 && random.nextFloat() < wingProbability) {
            arms = 1;
        } else {
            arms = 0;
        }

        if (userInput.hasFins()) {
            fins = userInput.getFins();
        } else if (flooredLegs == 0) {
            fins = (wings + arms == 0) ? 2 : 1;
        } else if (flooredLegs + wings + arms < 2) {
            fins = 1;
        } else {
            fins = 0;
        }
        System.out.println("arms: " + arms);
        System.out.println("fins: " + fins);
    }

    /**
     * wings: user input or calculated by wingProbability
     */
    private void calculateWings() {
        if (userInput.hasWings()) {
            wings = userInput.getWings();
        } else if (userInput.getTotal() < maxExtremityCount && random.nextFloat() < wingProbability) { // todo wing probability too small?
            wings = 1;
        } else {
            wings = 0;
        }
        System.out.println("wings: " + wings);
    }

    /**
     * legs: user input or calculated by flooredLegProbability
     */
    private void calculateLegsAndFloorHeight(SpineData spine) {
        if (userInput.hasFlooredLegs()) {
            flooredLegs = userInput.getFlooredLegs();
        } else {
            float probability = random.nextFloat();
            boolean moreLegs = probability > (flooredLegProbability % 1);
            if (moreLegs) {
                flooredLegs = (int) Math.ceil(flooredLegProbability);
            } else {
                flooredLegs = (int) Math.floor(flooredLegProbability);
            }
            flooredLegs = Math.min(4, flooredLegs);
            flooredLegs = Math.max(0, flooredLegs);
        }
        System.out.println("floored legs: " + flooredLegs);
        calculateFloorHeight(spine);
    }

    // Must be called _after_ number of floored legs is calculated
    private void calculateFloorHeight(SpineData spine) {
        if (flooredLegs > 0) {
            float minFloorHeight = 0f;
            float bentRatio = 0.8f; // 1 means, that extremities can be completely vertical stretched out

            float pelvicHeight = spine.getBack().getControlPoint3().y;
            float legLength = lengthUpperLeg + lengthLowerLeg + lengthFoot;
            if (lengthUpperLeg + lengthLowerLeg >= pelvicHeight) {
                flooredAnkleWristProbability = 1f;
            } else if (legLength < pelvicHeight) {
                flooredAnkleWristProbability = 0f;
                minFloorHeight = pelvicHeight - bentRatio*legLength;
            } else {
                flooredAnkleWristProbability = (legLength - pelvicHeight) / lengthFoot;
                minFloorHeight = pelvicHeight - bentRatio*lengthUpperLeg - bentRatio*lengthLowerLeg;
            }

            if (flooredLegs > 1) {
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
    }
}
