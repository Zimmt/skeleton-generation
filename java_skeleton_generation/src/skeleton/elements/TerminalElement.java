package skeleton.elements;

import java.util.List;

public abstract class TerminalElement extends SkeletonPart {

    public TerminalElement(SkeletonPart parent) {
        super(parent);
    }

    public boolean isTerminal() {
        return true;
    }
}
