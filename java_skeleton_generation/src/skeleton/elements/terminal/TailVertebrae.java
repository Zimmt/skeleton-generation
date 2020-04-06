package skeleton.elements.terminal;

import skeleton.SpinePart;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

/**
 * scaling:
 * x: 3 * x-scale of normal vertebra
 */
public class TailVertebrae extends Vertebra {

    private final String kind = "tail_vertebrae";

    public TailVertebrae(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                         boolean positiveXDir, SpinePart spinePart, float jointSpinePosition) {
        super(transform, boundingBox, parent, ancestor, positiveXDir, spinePart, jointSpinePosition);
    }

    @Override
    public String getKind() {
        return kind;
    }
}
