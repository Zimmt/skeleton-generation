package util.pca;

public class PcaConditions {

    private Double tailLength; // the x-difference between first control point of tail and the last

    private Integer wings;
    private Integer flooredLegs;

    public PcaConditions() { }

    public PcaConditions(Double tailLength, Integer wings, Integer flooredLegs) {
        this.tailLength = tailLength;
        this.wings = wings;
        this.flooredLegs = flooredLegs;
    }

    public boolean anyConditionPresent() {
        return hasWings() || hasFlooredLegs() || hasTailLength();
    }

    public int getConditionCount() {
        int count = 0;
        if (hasTailLength()) count++;
        if (hasWings()) count++;
        if (hasFlooredLegs()) count++;
        return count;
    }

    public int[] getPcaDimensionsWithConditions() {
        int[] dimensions = new int[getConditionCount()];
        int next = 0;
        if (hasTailLength()) dimensions[next++] = PcaDimension.TAIL4X.ordinal();
        if (hasWings()) dimensions[next++] = PcaDimension.WINGS.ordinal();
        if (hasFlooredLegs()) dimensions[next++] = PcaDimension.FLOORED_LEGS.ordinal();
        return dimensions;
    }

    /**
     * tailLength is not set as it depends on other values
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

    public Integer getWings() {
        return wings;
    }

    public boolean hasFlooredLegs() {
        return flooredLegs != null;
    }

    public Integer getFlooredLegs() {
        return flooredLegs;
    }

    public boolean hasTailLength() {
        return tailLength != null;
    }

    public Double getTailLength() {
        return tailLength;
    }
}
