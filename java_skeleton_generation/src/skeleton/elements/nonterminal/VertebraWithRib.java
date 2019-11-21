package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class VertebraWithRib extends NonTerminalElement {

    private final String id = "vertebra with rib";

    public VertebraWithRib(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
