package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;

public class Arm extends NonTerminalElement {

    private final String kind = "arm";

    public Arm(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }
}
