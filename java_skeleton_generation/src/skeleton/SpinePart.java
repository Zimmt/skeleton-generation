package skeleton;

public enum SpinePart {
    NECK,
    BACK,
    TAIL;

    /**
     * @return true only if the parts are different and if the first is left of the second
     */
    public static boolean isLeftOf(SpinePart sp1, SpinePart sp2) {
        if ((sp1 == NECK && (sp2 == BACK || sp2 == TAIL)) || (sp1 == BACK && sp2 == TAIL)) {
            return true;
        }
        return false;
    }

    public static SpinePart getBiggerSpinePart(SpinePart spinePart) {
        switch(spinePart) {
            case NECK:
                return SpinePart.BACK;
            case BACK:
                return SpinePart.TAIL;
            default:
                System.err.println("No bigger spine interval found");
                return null;
        }
    }

    public static SpinePart getSmallerSpinePart(SpinePart spinePart) {
        switch(spinePart) {
            case BACK:
                return SpinePart.NECK;
            case TAIL:
                return SpinePart.BACK;
            default:
                System.err.println("No smaller spine interval found");
                return null;
        }
    }
}
