package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class Foot extends TerminalElement {

    private final String id = "foot";

    public Foot(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
