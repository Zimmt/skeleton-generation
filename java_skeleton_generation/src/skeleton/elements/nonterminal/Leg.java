package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class Leg extends NonTerminalElement {

    private final String id = "leg";

    public Leg(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return true; }
}
