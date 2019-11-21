package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class Tail extends NonTerminalElement {

    private final String id = "tail";

    public Tail(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
