package skeleton.elements.nonterminal;

public class WholeBody extends NonTerminalElement {

    private final String id = "whole body";

    public WholeBody() {
        super(null);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
