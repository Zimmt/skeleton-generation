package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public abstract class TerminalElement extends SkeletonPart {

    public TerminalElement(SkeletonPart parent, SkeletonPart ancestor) {
        super(parent, ancestor);
    }

    public boolean isTerminal() {
        return true;
    }
}
