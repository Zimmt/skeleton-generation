package skeleton.elements.nonterminal;

import skeleton.elements.terminal.ShoulderVertebra;
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

    public ShoulderVertebra getParent() {
        if (super.getParent() instanceof ShoulderVertebra) {
            return (ShoulderVertebra) super.getParent();
        } else {
            System.err.println("Parent of shoulder girdle is no shoulder vertebra!");
            return null;
        }
    }
}
