package util.pca;

import java.io.Serializable;

public class PcaConditions implements Serializable {

    private Double neckYLength; // the y-difference between first control point of neck and the last
    private Double tailXLength; // the x-difference between first control point of tail and the last

    private Double wings;
    private Double flooredLegs;

    public PcaConditions() { }

    public PcaConditions(Double neckYLength, Double tailXLength, Double wings, Double flooredLegs) {
        this.neckYLength = neckYLength;
        this.tailXLength = tailXLength;
        this.wings = wings;
        this.flooredLegs = flooredLegs;
    }

    public boolean anyConditionPresent() {
        return hasWings() || hasFlooredLegs() || hasTailLength() || hasNeckLength();
    }

    public int getConditionCount() {
        int count = 0;
        if (hasNeckLength()) count++;
        if (hasTailLength()) count++;
        if (hasWings()) count++;
        if (hasFlooredLegs()) count++;
        return count;
    }

    public int[] getPcaDimensionsWithConditions() {
        int[] dimensions = new int[getConditionCount()];
        int next = 0;
        if (hasNeckLength()) dimensions[next++] = PcaDimension.BACK1Y.ordinal();
        if (hasTailLength()) dimensions[next++] = PcaDimension.TAIL4X.ordinal();
        if (hasWings()) dimensions[next++] = PcaDimension.WINGS.ordinal();
        if (hasFlooredLegs()) dimensions[next++] = PcaDimension.FLOORED_LEGS.ordinal();
        return dimensions;
    }

    /**
     * neckLength and tailLength is not set as they depend on other values
     */
    public void setAbsoluteConditions(PcaDataPointConditioned point) {
        if (wings != null) {
            point.setWings((double) wings);
        }
        if (flooredLegs != null) {
            point.setFlooredLegs((double) flooredLegs);
        }
    }

    public boolean hasWings() {
        return wings != null;
    }

    public Double getWings() {
        return wings;
    }

    public boolean hasFlooredLegs() {
        return flooredLegs != null;
    }

    public Double getFlooredLegs() {
        return flooredLegs;
    }

    public boolean hasTailLength() {
        return tailXLength != null;
    }

    public Double getTailXLength() {
        return tailXLength;
    }

    public boolean hasNeckLength() {
        return neckYLength != null;
    }

    public Double getNeckYLength() {
        return neckYLength;
    }
}
