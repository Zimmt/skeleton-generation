package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class BackPart extends NonTerminalElement {

    private final String id = "back part";

    public BackPart(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
