package skeleton.elements.terminal;

import skeleton.SpinePart;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Vector3f;

public class RibVertebra extends Vertebra {

    private final String kind = "rib vertebra";

    public RibVertebra(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                       boolean positiveXDir, SpinePart spinePart, float jointSpinePosition) {
        super(transform, boundingBox, parent, ancestor, positiveXDir, spinePart, jointSpinePosition);
    }

    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox, BoundingBox normalVertebraBoundingBox) {
        return new Vector3f(0f, -boundingBox.getYLength() + normalVertebraBoundingBox.getYLength() / 2f, -boundingBox.getZLength() / 2f);
    }

    public String getKind() {
        return kind;
    }
}
