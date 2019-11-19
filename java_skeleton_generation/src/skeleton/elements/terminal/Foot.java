package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class Foot extends TerminalElement {

    private final String id = "foot";

    public Foot(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
