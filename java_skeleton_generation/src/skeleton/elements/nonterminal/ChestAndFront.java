package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;

public class ChestAndFront extends NonTerminalElement {

    private final String kind = "chest and front";

    public ChestAndFront(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return false; }
}
