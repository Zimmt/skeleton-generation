package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class Tail extends NonTerminalElement {

    private final String id = "tail";

    public Tail(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
