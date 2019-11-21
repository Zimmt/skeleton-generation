package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class UpperArm extends TerminalElement {

    private final String id = "upper arm";

    public UpperArm(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
