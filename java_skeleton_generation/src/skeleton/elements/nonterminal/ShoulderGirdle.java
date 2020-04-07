package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import skeleton.replacementRules.ExtremityPositioning;

public class ShoulderGirdle extends NonTerminalElement {

    private final String kind = "shoulder girdle";

    private ExtremityPositioning extremityPositioning;
    private boolean onNeck;

    public ShoulderGirdle(TerminalElement parent, NonTerminalElement ancestor, ExtremityPositioning extremityPositioning, boolean onNeck) {
        super(parent, ancestor);
        this.extremityPositioning = extremityPositioning;
        this.onNeck = onNeck;
    }

    public boolean isOnNeck() {
        return onNeck;
    }

    public String getKind() {
        return kind;
    }

    public ExtremityPositioning getExtremityPositioning() {
        return extremityPositioning;
    }

    public boolean canBeMirrored() {
        return true;
    }
}
