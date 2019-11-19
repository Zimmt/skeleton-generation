package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class UpperArm extends TerminalElement {

    private final String id = "upper arm";

    public UpperArm(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
