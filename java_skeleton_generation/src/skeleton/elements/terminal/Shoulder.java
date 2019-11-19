package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class Shoulder extends TerminalElement {

    private final String id = "shoulder";

    public Shoulder(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
