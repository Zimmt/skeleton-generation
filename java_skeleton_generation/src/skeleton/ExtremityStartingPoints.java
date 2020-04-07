package skeleton;

import skeleton.elements.ExtremityKind;
import skeleton.replacementRules.ExtremityPositioning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ExtremityStartingPoints implements Serializable {

    private static int[] possibleWingPositions = new int[] {1, 2};
    private static int[] possibleLegPositions = new int[] {0, 1};
    private static int[] possibleArmPositions = new int[] {1, 2};
    private static int[] possibleFinPositions = new int[] {0, 1, 2};

    // a two/three element array of extremity positionings for each extremity starting point
    // the first entry concerns the extremity starting point that is nearest to the tail
    private ArrayList<ExtremityPositioning[]> extremityKindsForStartingPoints;

    private boolean twoExtremitiesPerGirdleAllowed;

    private Random random = new Random();

    public ExtremityStartingPoints(boolean hasSecondShoulder, boolean twoExtremitiesPerGirdleAllowed) {
        this.twoExtremitiesPerGirdleAllowed = twoExtremitiesPerGirdleAllowed;
        int countPerPoint = twoExtremitiesPerGirdleAllowed ? 2 : 1;
        if (hasSecondShoulder) {
            extremityKindsForStartingPoints = new ArrayList<>(Arrays.asList(new ExtremityPositioning[countPerPoint], new ExtremityPositioning[countPerPoint], new ExtremityPositioning[countPerPoint]));
        } else {
            extremityKindsForStartingPoints = new ArrayList<>(Arrays.asList(new ExtremityPositioning[countPerPoint], new ExtremityPositioning[countPerPoint]));
        }
    }

    public boolean hasShoulderOnNeck() {
        return extremityKindsForStartingPoints.size() > 2;
    }

    public int getStartingPointCount() {
        return extremityKindsForStartingPoints.size();
    }

    public ExtremityPositioning[] getExtremityPositioningsForStartingPoint(int startingPoint) {
        if (startingPoint >= extremityKindsForStartingPoints.size()) {
            return new ExtremityPositioning[0];
        } else {
            List<ExtremityPositioning> extremityPositionings = new ArrayList<>(extremityKindsForStartingPoints.get(startingPoint).length);
            for (int i = 0; i < extremityKindsForStartingPoints.get(startingPoint).length; i++) {
                if (extremityKindsForStartingPoints.get(startingPoint)[i] != null) {
                    extremityPositionings.add(extremityKindsForStartingPoints.get(startingPoint)[i]);
                }
            }
            return extremityPositionings.toArray(ExtremityPositioning[]::new);
        }
    }

    /**
     * If there is an extremity girdle with no legs and one with more than one, the leg is moved.
     * There should be no empty shoulder if there are enough extremities.
     */
    public void distributeExtremities() {
        if (!twoExtremitiesPerGirdleAllowed) {
            return; // this makes no sense if there is max one extremity per point
        }
        if (!(getFreeExtremityCountAtPosition(0) == 0 && getFreeExtremityCountAtPosition(1) == 0)) {
            distributeLegs(); // otherwise there is no space to distribute
        }
        if (hasShoulderOnNeck() && !(getExtremityPositioningsForStartingPoint(2).length > 0 && getExtremityPositioningsForStartingPoint(1).length > 0)) {
            if (getExtremityPositioningsForStartingPoint(2).length == 0 && getExtremityPositioningsForStartingPoint(1).length == 0) {
                System.err.println("There is a shoulder on neck, but no extremities to put on either of the shoulders.");
                return;
            }
            distributeShoulderExtremities();
        }
    }

    /**
     * If there is an extremity girdle with no legs and one with more than one, the leg is moved.
     * (only for extremity girdles on back)
     */
    private void distributeLegs() {
        int legCountPelvis = (int) Arrays.stream(getExtremityPositioningsForStartingPoint(0)).filter(e -> e.getExtremityKind() == ExtremityKind.LEG).count();
        int legCountShoulder = (int) Arrays.stream(getExtremityPositioningsForStartingPoint(1)).filter((e -> e.getExtremityKind() == ExtremityKind.LEG)).count();
        if (legCountPelvis > 1 && legCountShoulder == 0) {
            removeKindAtPosition(ExtremityKind.LEG, 0);
            setKindAtPosition(ExtremityKind.LEG, 1);
        } else if (legCountPelvis == 0 && legCountShoulder > 1) {
            removeKindAtPosition(ExtremityKind.LEG, 1);
            setKindAtPosition(ExtremityKind.LEG, 0);
        }
    }

    /**
     * Move a random extremity from the shoulder with extremities to the one with none.
     * If the shoulder with extremities has only one extremity and would be left empty then, nothing is done.
     */
    private void distributeShoulderExtremities() {
        int moveFrom = getExtremityPositioningsForStartingPoint(2).length == 0 ? 1 : 2;
        int moveTo = moveFrom == 1 ? 2 : 1;
        if (getExtremityPositioningsForStartingPoint(moveFrom).length < 2) {
            return; // if an extremity would be moved from here there would be none left
        }

        ExtremityPositioning[] positionings = getExtremityPositioningsForStartingPoint(moveFrom);
        int i = random.nextInt(positionings.length);
        ExtremityPositioning movedPositioning = positionings[i];
        positionings[i] = null;
        setKindAtPosition(movedPositioning.getExtremityKind(), moveTo);
    }

    public void setWings(int wingCount) {
        setKindAtPositions(ExtremityKind.WING, wingCount, possibleWingPositions);
    }

    public void setLegs(int legCount) {
        setKindAtPositions(ExtremityKind.LEG, legCount, possibleLegPositions);
    }

    public void setArms(int armCount) {
        setKindAtPositions(ExtremityKind.ARM, armCount, possibleArmPositions);
    }

    public void setFins(int finCount) {
        setKindAtPositions(ExtremityKind.FIN, finCount, possibleFinPositions);
    }

    public int getFreeExtremityCountAtPosition(int position) {
        if (position >= extremityKindsForStartingPoints.size()) {
            return 0;
        }
        ExtremityPositioning[] positionings = getExtremityPositioningsForStartingPoint(position);
        return extremityKindsForStartingPoints.get(position).length - positionings.length;
    }

    public int getFreeWingCount() {
        return getFreeCount(possibleWingPositions);
    }

    public int getFreeArmCount() {
        return getFreeCount(possibleArmPositions);
    }

    public int getFreeLegCount() {
        return getFreeCount(possibleLegPositions);
    }

    public int getFreeFinCount() {
        return getFreeCount(possibleFinPositions);
    }

    public void setKindAtPosition(ExtremityKind kind, int position) {
        if (extremityKindsForStartingPoints.get(position)[0] == null) {
            extremityKindsForStartingPoints.get(position)[0] = new ExtremityPositioning(kind);
        } else if (extremityKindsForStartingPoints.get(position)[1] == null){
            extremityKindsForStartingPoints.get(position)[1] = new ExtremityPositioning(kind);
        } else {
            System.err.println("Cannot set kind in specified position!");
        }
    }

    private void removeKindAtPosition(ExtremityKind kind, int position) {
        for (int i = 0; i < extremityKindsForStartingPoints.get(position).length; i++) {
            if (extremityKindsForStartingPoints.get(position)[i].getExtremityKind() == kind) {
                extremityKindsForStartingPoints.get(position)[i] = null;
                break;
            }
        }
    }

    private void setKindAtPositions(ExtremityKind kind, int count, int[] positions) {
        int toSet = count;
        while (toSet > 0) {
            List<Integer> possiblePositions = new ArrayList<>(positions.length);
            for (int pos : positions) {
                if (getFreeExtremityCountAtPosition(pos) > 0) {
                    possiblePositions.add(pos);
                }
            }
            if (possiblePositions.isEmpty()) {
                System.err.println("Could not set all extremities");
                break;
            } else {
                int position = possiblePositions.get(random.nextInt(possiblePositions.size()));
                setKindAtPosition(kind, position);
                toSet--;
            }
        }
    }

    private int getFreeCount(int[] possiblePositions) {
        int count = 0;
        for (int pos : possiblePositions) {
            count += getFreeExtremityCountAtPosition(pos);
        }
        return count;
    }
}
