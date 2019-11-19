package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

/**
 * Schienbein
 */
public class Shin extends TerminalElement {

    private final String id = "shin";

    public Shin(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
