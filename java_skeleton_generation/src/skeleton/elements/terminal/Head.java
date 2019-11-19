package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class Head extends TerminalElement {

    private final String id = "head";

    public Head(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
