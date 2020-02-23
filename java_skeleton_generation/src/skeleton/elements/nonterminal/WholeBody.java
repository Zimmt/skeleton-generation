package skeleton.elements.nonterminal;

import skeleton.SkeletonGenerator;

public class WholeBody extends NonTerminalElement {

    private final String kind = "whole body";

    // the transform here specifies the position in relation to the world origin
    public WholeBody(SkeletonGenerator generator) {
        super(generator);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return false; }
}
