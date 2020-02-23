package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;

public class ShoulderGirdle extends NonTerminalElement {

    private final String kind = "shoulder girdle";

    public ShoulderGirdle(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() {
        return true;
    }
}
