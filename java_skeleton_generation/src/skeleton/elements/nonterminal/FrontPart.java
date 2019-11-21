package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class FrontPart extends NonTerminalElement {

    private final String id = "front part";

    public FrontPart(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
