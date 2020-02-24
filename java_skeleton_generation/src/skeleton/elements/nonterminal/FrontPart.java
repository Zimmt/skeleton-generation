package skeleton.elements.nonterminal;

import skeleton.elements.terminal.RootVertebra;
import skeleton.elements.terminal.TerminalElement;

public class FrontPart extends NonTerminalElement {

    private final String kind = "front part";

    public FrontPart(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return false; }

    public RootVertebra getParent() {
        if (super.getParent() instanceof RootVertebra) {
            return (RootVertebra) super.getParent();
        } else {
            System.err.println("Parent of front part is no root vertebra!");
            return null;
        }
    }
}
