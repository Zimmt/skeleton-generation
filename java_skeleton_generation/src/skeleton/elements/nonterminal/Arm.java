package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class Arm extends NonTerminalElement {

    private final String id = "arm";

    public Arm(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
