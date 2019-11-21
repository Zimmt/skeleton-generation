package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class Chest extends NonTerminalElement {

    private final String id = "chest";

    public Chest(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
