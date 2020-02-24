package skeleton.elements.nonterminal;

import skeleton.elements.terminal.Shoulder;
import skeleton.elements.terminal.TerminalElement;

public class Arm extends NonTerminalElement {

    private final String kind = "arm";

    public Arm(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }

    public Shoulder getParent() {
        if (super.getParent() instanceof Shoulder) {
            return (Shoulder) super.getParent();
        } else {
            System.err.println("Parent of arm is no shoulder!");
            return null;
        }
    }
}
