package skeleton;

public class ExtremityData {
    private float wings;
    private float flooredLegs; // #legs/2, [0,2]

    private float lengthUpperArm; // [0, 1000]
    private float lengthLowerArm; // [0, 1000]
    private float lengthHand; // [0, 1000]

    private float lengthUpperLeg; // [0, 1000]
    private float lengthLowerLeg; // [0, 1000]
    private float lengthFoot; // [0, 1000]

    public ExtremityData(double wings, double flooredLegs, double lengthUpperArm, double lengthLowerArm, double lengthHand, double lengthUpperLeg, double lengthLowerLeg, double lengthFoot) {
        this.wings = (float) wings;
        this.flooredLegs = (float) flooredLegs;
        this.lengthUpperArm = (float) lengthUpperArm;
        this.lengthLowerArm = (float) lengthLowerArm;
        this.lengthHand = (float) lengthHand;
        this.lengthUpperLeg = (float) lengthUpperLeg;
        this.lengthLowerLeg = (float) lengthLowerLeg;
        this.lengthFoot = (float) lengthFoot;
    }

    public float getWings() {
        return wings;
    }

    public float getFlooredLegs() {
        return flooredLegs;
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
}
