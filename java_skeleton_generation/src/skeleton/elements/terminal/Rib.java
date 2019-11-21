package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class Rib extends TerminalElement {

    private final String id = "rib";

    public Rib(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
