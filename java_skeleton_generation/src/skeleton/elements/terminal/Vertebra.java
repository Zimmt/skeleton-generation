package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

/**
 * Wirbel
 */
public class Vertebra extends TerminalElement {

    private final String id = "vertebra";

    public Vertebra(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
