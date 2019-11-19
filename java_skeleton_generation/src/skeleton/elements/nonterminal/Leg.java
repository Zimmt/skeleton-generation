package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class Leg extends NonTerminalElement {

    private final String id = "leg";

    public Leg(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
