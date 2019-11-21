package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;

public abstract class NonTerminalElement extends SkeletonPart {

    public NonTerminalElement(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public boolean isTerminal() {
        return false;
    }
}
