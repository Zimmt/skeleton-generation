package skeleton;

public class ExtremityData {
    private double wings;
    private double flooredLegs; // #legs/2, [0,2]

    private double lengthUpperArm; // [0, 1000]
    private double lengthLowerArm; // [0, 1000]
    private double lengthHand; // [0, 1000]

    private double lengthUpperLeg; // [0, 1000]
    private double lengthLowerLeg; // [0, 1000]
    private double lengthFoot; // [0, 1000]

    public ExtremityData(double wings, double flooredLegs, double lengthUpperArm, double lengthLowerArm, double lengthHand, double lengthUpperLeg, double lengthLowerLeg, double lengthFoot) {
        this.wings = wings;
        this.flooredLegs = flooredLegs;
        this.lengthUpperArm = lengthUpperArm;
        this.lengthLowerArm = lengthLowerArm;
        this.lengthHand = lengthHand;
        this.lengthUpperLeg = lengthUpperLeg;
        this.lengthLowerLeg = lengthLowerLeg;
        this.lengthFoot = lengthFoot;
    }

    public double getWings() {
        return wings;
    }

    public double getFlooredLegs() {
        return flooredLegs;
    }

    public double getLengthUpperArm() {
        return lengthUpperArm;
    }

    public double getLengthLowerArm() {
        return lengthLowerArm;
    }

    public double getLengthHand() {
        return lengthHand;
    }

    public double getLengthUpperLeg() {
        return lengthUpperLeg;
    }

    public double getLengthLowerLeg() {
        return lengthLowerLeg;
    }

    public double getLengthFoot() {
        return lengthFoot;
    }
}
