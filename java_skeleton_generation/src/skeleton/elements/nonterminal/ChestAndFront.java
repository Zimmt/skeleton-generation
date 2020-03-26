package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Vertebra;

public class ChestAndFront extends NonTerminalElement {

    private final String kind = "chest and front";

    public ChestAndFront(TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean canBeMirrored() { return false; }

    public Vertebra getParent() {
        if (super.getParent() instanceof Vertebra) {
            return (Vertebra) super.getParent();
        } else {
            System.err.println("Parent of chest and front is no vertebra!");
            return null;
        }
    }
}
