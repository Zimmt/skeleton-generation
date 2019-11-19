package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class LowerArm extends TerminalElement {

    private final String id = "lower arm";

    public LowerArm(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
