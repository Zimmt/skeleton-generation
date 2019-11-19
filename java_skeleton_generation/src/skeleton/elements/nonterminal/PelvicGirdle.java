package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

/**
 * Hüftgürtel
 */
public class PelvicGirdle extends NonTerminalElement {

    private final String id = "pelvic girdle";

    public PelvicGirdle(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
