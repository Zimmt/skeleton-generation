package skeleton.elements.nonterminal;

import skeleton.elements.terminal.RootVertebra;
import skeleton.elements.terminal.TerminalElement;

public class BackPart extends NonTerminalElement {

    private final String kind = "back part";

    public BackPart(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean canBeMirrored() { return false; }

    public RootVertebra getParent() {
        if (super.getParent() instanceof RootVertebra) {
            return (RootVertebra) super.getParent();
        } else {
            System.err.println("Parent of back part is no root vertebra!");
            return null;
        }
    }
}
