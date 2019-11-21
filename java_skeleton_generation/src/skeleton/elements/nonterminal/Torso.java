package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class Torso extends NonTerminalElement {

    private final String id = "torso";

    public Torso(SkeletonPart ancestor) {
        super(null, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
