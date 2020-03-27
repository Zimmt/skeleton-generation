package skeleton.elements.nonterminal;

import skeleton.elements.terminal.Pelvis;
import skeleton.elements.terminal.TerminalElement;

public class Leg extends NonTerminalElement {

    private final String kind = "leg";

    public Leg(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean canBeMirrored() { return true; }

    public Pelvis getParent() {
        if (super.getParent() instanceof Pelvis) {
            return (Pelvis) super.getParent();
        } else {
            System.err.println("Parent of leg is not pelvic!");
            return null;
        }
    }
}
