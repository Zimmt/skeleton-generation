package skeleton.elements.nonterminal;

import skeleton.elements.terminal.ShoulderVertebra;
import skeleton.elements.terminal.TerminalElement;

public class ShoulderGirdle extends NonTerminalElement {

    private final String kind = "shoulder girdle";
    private boolean secondShoulderGirdle;

    public ShoulderGirdle(TerminalElement parent, NonTerminalElement ancestor, boolean secondShoulderGirdle) {
        super(parent, ancestor);
        this.secondShoulderGirdle = secondShoulderGirdle;
    }

    public boolean isSecondShoulderGirdle() {
        return secondShoulderGirdle;
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
