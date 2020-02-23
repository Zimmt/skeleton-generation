package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;

public class Leg extends NonTerminalElement {

    private final String kind = "leg";

    public Leg(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }
}
