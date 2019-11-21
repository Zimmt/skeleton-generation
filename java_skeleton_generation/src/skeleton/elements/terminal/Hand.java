package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class Hand extends TerminalElement {

    private final String id = "hand";

    public Hand(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
