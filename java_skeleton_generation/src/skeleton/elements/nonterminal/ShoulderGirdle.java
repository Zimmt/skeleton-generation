package skeleton.elements.nonterminal;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

public class ShoulderGirdle extends NonTerminalElement {

    private final String kind = "shoulder girdle";

    private ExtremityKind extremityKind;
    private boolean onNeck;

    public ShoulderGirdle(TerminalElement parent, NonTerminalElement ancestor, ExtremityKind extremityKind, boolean onNeck) {
        super(parent, ancestor);
        this.extremityKind = extremityKind;
        this.onNeck = onNeck;
    }

    public boolean isOnNeck() {
        return onNeck;
    }

    public String getKind() {
        return kind;
    }

    public ExtremityKind getExtremityKind() {
        return extremityKind;
    }

    public boolean canBeMirrored() {
        return true;
    }
}
