package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;

public class BackPart extends NonTerminalElement {

    private final String kind = "back part";
    private float startPosition;

    public BackPart(TerminalElement parent, NonTerminalElement ancestor, float startPosition) {
        super(parent, ancestor);
        this.startPosition = startPosition;
    }

    public String getKind() {
        return kind;
    }

    public float getStartPosition() {
        return startPosition;
    }

    public boolean isMirrored() { return false; }
}
