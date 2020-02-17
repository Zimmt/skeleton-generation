package skeleton;

import util.pca.PcaDataPoint;

public class SkeletonMetaData {
    private SpinePosition spine;
    private ExtremityData extremities;
    private double weight;

    public SkeletonMetaData(PcaDataPoint p) {
        this.spine = new SpinePosition(p.getSpine());
        this.extremities = new ExtremityData(p.getWings(), p.getFlooredLegs(),
                p.getLengthUpperArm(), p.getLengthLowerArm(), p.getLengthHand(),
                p.getLengthUpperLeg(), p.getLengthLowerLeg(), p.getLengthFoot());
        this.weight = p.getWeight();
    }

    public SpinePosition getSpine() {
        return spine;
    }

    public ExtremityData getExtremities() {
        return extremities;
    }

    public double getWeight() {
        return weight;
    }
}
