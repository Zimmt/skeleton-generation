package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;

public class FrontPart extends NonTerminalElement {

    private final String kind = "front part";
    private float endPosition;

    public FrontPart(TerminalElement parent, NonTerminalElement ancestor, float endPosition) {
        super(parent, ancestor);
        this.endPosition = endPosition;
    }

    public String getKind() {
        return kind;
    }

    public float getEndPosition() {
        return endPosition;
    }

    public boolean isMirrored() { return false; }
}
