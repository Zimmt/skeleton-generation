package skeleton.elements.nonterminal;

import skeleton.SkeletonGenerator;
import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.TerminalElement;

public abstract class NonTerminalElement extends SkeletonPart {

    public NonTerminalElement(SkeletonGenerator generator) {
        super(generator);
    }

    public NonTerminalElement(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public boolean isTerminal() {
        return false;
    }
}
