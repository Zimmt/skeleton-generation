package skeleton.elements;

import java.util.List;

public abstract class NonTerminalElement extends SkeletonPart {

    public NonTerminalElement(SkeletonPart parent) {
        super(parent);
    }

    public boolean isTerminal() {
        return false;
    }
}
