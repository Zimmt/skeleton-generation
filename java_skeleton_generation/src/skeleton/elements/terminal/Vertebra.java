package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public class Vertebra extends TerminalElement {

    private final String id = "vertebra";

    public Vertebra(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
