package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;

public abstract class TerminalElement extends SkeletonPart {

    public TerminalElement(SkeletonPart parent) {
        super(parent);
    }

    public boolean isTerminal() {
        return true;
    }
}
