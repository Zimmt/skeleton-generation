package util.pca;

public class PcaConditions {

    private Integer wings;
    private Integer flooredLegs;

    public PcaConditions() { }

    public PcaConditions(Integer wings, Integer flooredLegs) {
        this.wings = wings;
        this.flooredLegs = flooredLegs;
    }

    public boolean anyConditionPresent() {
        return wings != null || flooredLegs != null;
    }

    public int getConditionCount() {
        int count = 0;
        if (wings != null) count++;
        if (flooredLegs != null) count++;
        return count;
    }

    public void setConditions(PcaDataPointConditioned point) {
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
}
