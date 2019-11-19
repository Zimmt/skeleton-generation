package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

/**
 * Hüfte
 */
public class Pelvic extends TerminalElement {

    private final String id = "pelvic";

    public Pelvic(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
