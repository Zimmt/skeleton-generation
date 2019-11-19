package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

/**
 * Oberschenkel
 */
public class Thigh extends TerminalElement {

    private final String id = "thigh";

    public Thigh(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
