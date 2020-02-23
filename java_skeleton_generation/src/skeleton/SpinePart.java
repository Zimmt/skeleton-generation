package skeleton;

public enum SpinePart {
    NECK,
    BACK,
    TAIL;

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
