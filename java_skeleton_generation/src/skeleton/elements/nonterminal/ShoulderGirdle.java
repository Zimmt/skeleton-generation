package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public class ShoulderGirdle extends NonTerminalElement {

    private final String id = "shoulder girdle";

    public ShoulderGirdle(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
