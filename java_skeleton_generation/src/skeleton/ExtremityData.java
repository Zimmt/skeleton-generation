package skeleton;

import java.util.Random;

public class ExtremityData {
    private float wingProbability; // [0, 1]
    private float flooredLegProbability; // #legs/2, [0,2]

    private float lengthUpperArm; // [0, 1000]
    private float lengthLowerArm; // [0, 1000]
    private float lengthHand; // [0, 1000]

    private float lengthUpperLeg; // [0, 1000]
    private float lengthLowerLeg; // [0, 1000]
    private float lengthFoot; // [0, 1000]

    private int flooredLegs;
    private int wings;
    private int arms;
    private float floorHeight = 0f;
    private float flooredAnkleWristProbability;

    public ExtremityData(double wingProbability, double flooredLegProbability,
                         double lengthUpperArm, double lengthLowerArm, double lengthHand,
                         double lengthUpperLeg, double lengthLowerLeg, double lengthFoot,
                         SpineData spine) {
        this.wingProbability = (float) wingProbability;
        this.flooredLegProbability = (float) flooredLegProbability;
        this.lengthUpperArm = (float) lengthUpperArm;
        this.lengthLowerArm = (float) lengthLowerArm;
        this.lengthHand = (float) lengthHand;
        this.lengthUpperLeg = (float) lengthUpperLeg;
        this.lengthLowerLeg = (float) lengthLowerLeg;
        this.lengthFoot = (float) lengthFoot;
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

    public int getFlooredLegs() {
        return flooredLegs;
    }

    public int getWings() {
        return wings;
    }

    public int getArms() {
        return arms;
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

    private void calculateDerivedValues(SpineData spine) {
        Random random = new Random();
        calculateWings();
        calculateLegsAndFloorHeight(random.nextFloat(), spine);
        calculateArms();
    }

    private void calculateWings() {
        if (wingProbability > 0.4f) { // todo what probability is good here?
            wings = 1;
        } else {
            wings = 0;
        }
        System.out.println("wings: " + wings);
    }

    private void calculateLegsAndFloorHeight(float probability, SpineData spine) {
        if (flooredLegProbability >= 2 || (probability > 0.5f && flooredLegProbability > 1)) {
            flooredLegs = 2;
        } else if (flooredLegProbability <= 0 || (probability < 0.5f && flooredLegProbability < 1)) {
            flooredLegs = 0;
        } else {
            flooredLegs = 1;
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
            System.out.println("floored ankle probability: " + flooredAnkleWristProbability);
        }
    }


    private void calculateArms() {
        if (flooredLegs == 1 && wings == 0) {
            arms = 1;
        }
        System.out.println("Arms: " + arms);
    }
}
