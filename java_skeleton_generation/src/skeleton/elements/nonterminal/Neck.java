package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class Neck extends NonTerminalElement {

    private final String id = "neck";

    public Neck(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
