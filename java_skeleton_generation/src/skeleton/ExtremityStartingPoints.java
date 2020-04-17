package skeleton;

import skeleton.elements.ExtremityKind;
import skeleton.replacementRules.ExtremityPositioning;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ExtremityStartingPoints implements Serializable {

    private HashMap<ExtremityKind, List<Integer>> possiblePositionsForKinds;

    // a two/three element array of extremity positionings for each extremity starting point
    // the first entry concerns the extremity starting point that is nearest to the tail
    private final ArrayList<ExtremityPositioning[]> extremityPositioningsForStartingPoints;

    private final boolean twoExtremitiesPerGirdleAllowed;

    public ExtremityStartingPoints(boolean hasSecondShoulder, boolean twoExtremitiesPerGirdleAllowed, List<Integer> forbiddenPositions) {
        this.twoExtremitiesPerGirdleAllowed = twoExtremitiesPerGirdleAllowed;
        int countPerPoint = twoExtremitiesPerGirdleAllowed ? 2 : 1;
        if (hasSecondShoulder) {
            extremityPositioningsForStartingPoints = new ArrayList<>(Arrays.asList(new ExtremityPositioning[countPerPoint], new ExtremityPositioning[countPerPoint], new ExtremityPositioning[countPerPoint]));
        } else {
            extremityPositioningsForStartingPoints = new ArrayList<>(Arrays.asList(new ExtremityPositioning[countPerPoint], new ExtremityPositioning[countPerPoint]));
        }
        initializePossiblePositionsForKinds(forbiddenPositions);
    }

    private ExtremityStartingPoints(ArrayList<ExtremityPositioning[]> extremityKindsForStartingPoints, boolean twoExtremitiesPerGirdleAllowed, List<Integer> forbiddenPositions) {
        this.extremityPositioningsForStartingPoints = extremityKindsForStartingPoints;
        this.twoExtremitiesPerGirdleAllowed = twoExtremitiesPerGirdleAllowed;
        initializePossiblePositionsForKinds(forbiddenPositions);
    }

    public ExtremityStartingPoints newWithVariation() {
        List<ExtremityKind[]> extremityKinds = extremityPositioningsForStartingPoints.stream().map(
                sp -> Arrays.stream(sp).map(pos -> pos != null ? pos.getExtremityKind() : null).toArray(ExtremityKind[]::new)
        ).collect(Collectors.toList());
        Random random = new Random();

        int countPerPoint = twoExtremitiesPerGirdleAllowed ? 2 : 1;
        int changedStartingPoint = random.nextInt(extremityPositioningsForStartingPoints.size());
        int changedPosition = random.nextInt(countPerPoint);

        if (extremityKinds.get(changedStartingPoint)[changedPosition] == null && random.nextFloat() > 0.5) { // add an extremity
            List<ExtremityKind> possibleKinds = new ArrayList<>(possiblePositionsForKinds.size());
            possiblePositionsForKinds.forEach((k, poss) -> {
                if (poss.stream().anyMatch(i -> i == changedStartingPoint)) {
                    possibleKinds.add(k);
                }
            });
            if (possibleKinds.size() > 0) {
                ExtremityKind chosenKind = possibleKinds.get(random.nextInt(possibleKinds.size()));
                extremityKinds.get(changedStartingPoint)[changedPosition] = chosenKind;
                System.out.println("added one " + chosenKind);
            }
        }
        else if (extremityKinds.get(changedStartingPoint)[changedPosition] != null && random.nextFloat() > 0.5) { // change position or delete extremity
            ExtremityKind kind = extremityKinds.get(changedStartingPoint)[changedPosition];
            if (getFreeCountForKind(kind) > 0 && random.nextFloat() > 0.2) {
                int pos = getRandomPossiblePositionForKind(kind);
                if (extremityKinds.get(pos)[0] == null) {
                    extremityKinds.get(pos)[0] = kind;
                } else if (countPerPoint > 1 && extremityKinds.get(pos)[1] == null) {
                    extremityKinds.get(pos)[1] = kind;
                } else {
                    System.err.println("Could not move extremity!");
                }
                System.out.println("Duplicated one " + kind);
            }
            extremityKinds.get(changedStartingPoint)[changedPosition] = null;
            System.out.println("Removed one " + kind);
        } else {
            System.out.println("Did not change extremities");
        }

        ArrayList<ExtremityPositioning[]> newExtremityPositionings = extremityKinds.stream().map(
                a -> Arrays.stream(a).map(k -> k != null ? new ExtremityPositioning(k) : null).toArray(ExtremityPositioning[]::new)
        ).collect(Collectors.toCollection(ArrayList::new));
        return new ExtremityStartingPoints(newExtremityPositionings, twoExtremitiesPerGirdleAllowed, new ArrayList<>());
    }

    public int getStartingPointCount() {
        return extremityPositioningsForStartingPoints.size();
    }

    public ExtremityPositioning[] getExtremityPositioningsForStartingPoint(int startingPoint) {
        if (startingPoint >= extremityPositioningsForStartingPoints.size()) {
            return new ExtremityPositioning[0];
        } else {
            List<ExtremityPositioning> extremityPositionings = new ArrayList<>(extremityPositioningsForStartingPoints.get(startingPoint).length);
            for (int i = 0; i < extremityPositioningsForStartingPoints.get(startingPoint).length; i++) {
                if (extremityPositioningsForStartingPoints.get(startingPoint)[i] != null) {
                    extremityPositionings.add(extremityPositioningsForStartingPoints.get(startingPoint)[i]);
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
        if (!(getFreeCountAtPosition(0) == 0 && getFreeCountAtPosition(1) == 0)) {
            distributeLegs(); // otherwise there is no space to distribute
        }
        if (extremityPositioningsForStartingPoints.size() > 2 && !(getExtremityPositioningsForStartingPoint(2).length > 0 && getExtremityPositioningsForStartingPoint(1).length > 0)) {
            if (getExtremityPositioningsForStartingPoint(2).length == 0 && getExtremityPositioningsForStartingPoint(1).length == 0) {
                // System.err.println("There is a shoulder on neck, but no extremities to put on either of the shoulders.");
                return;
            }
            distributeShoulderExtremities();
        }
    }

    public void setKind(ExtremityKind kind, int count) {
        int toSet = count;
        while (toSet > 0) {
            int position = getRandomPossiblePositionForKind(kind);

            if (position < 0) { // there is no possible position
                boolean foundOption = false;

                for (Integer theoreticallyPossiblePosition : possiblePositionsForKinds.get(kind)) {
                    if (foundOption) break;
                    for (int i = 0; i < extremityPositioningsForStartingPoints.get(theoreticallyPossiblePosition).length; i++) {
                        ExtremityPositioning other = extremityPositioningsForStartingPoints.get(theoreticallyPossiblePosition)[i];
                        int otherPosition = getRandomPossiblePositionForKind(other.getExtremityKind());
                        if (otherPosition >= 0) {
                            setKindAtPosition(other.getExtremityKind(), otherPosition);
                            extremityPositioningsForStartingPoints.get(theoreticallyPossiblePosition)[i] = null;
                            setKindAtPosition(kind, theoreticallyPossiblePosition);
                            foundOption = true;
                            break;
                        }
                    }
                }
                if (!foundOption) System.err.println("Could not set " + kind.name());
                break;
            } else {
                setKindAtPosition(kind, position);
                toSet--;
            }
        }
    }

    public int getFreeCountAtPosition(int position) {
        if (position >= extremityPositioningsForStartingPoints.size()) {
            return 0;
        }
        ExtremityPositioning[] positionings = getExtremityPositioningsForStartingPoint(position);
        return extremityPositioningsForStartingPoints.get(position).length - positionings.length;
    }

    public int getFreeCountForKind(ExtremityKind kind) {
        return getFreeCount(possiblePositionsForKinds.get(kind));
    }

    public void setKindAtPosition(ExtremityKind kind, int position) {
        if (!possiblePositionsForKinds.get(kind).contains(position)) {
            return;
        }
        if (extremityPositioningsForStartingPoints.get(position)[0] == null) {
            extremityPositioningsForStartingPoints.get(position)[0] = new ExtremityPositioning(kind);
        } else if (twoExtremitiesPerGirdleAllowed && extremityPositioningsForStartingPoints.get(position)[1] == null){
            extremityPositioningsForStartingPoints.get(position)[1] = new ExtremityPositioning(kind);
        } else {
            System.err.println("Cannot set kind in specified position!");
        }
    }

    public void removeAllFromPosition(int position) {
        if (position >= extremityPositioningsForStartingPoints.size()) {
            return;
        }
        Arrays.fill(extremityPositioningsForStartingPoints.get(position), null);
    }

    /**
     * If there is an extremity girdle with no legs and one with more than one, the leg is moved.
     * (only for extremity girdles on back)
     */
    private void distributeLegs() {
        if (possiblePositionsForKinds.get(ExtremityKind.LEG).size() <= 1) {
            return;
        }
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
        int i = (new Random()).nextInt(positionings.length);
        ExtremityPositioning movedPositioning = positionings[i];
        positionings[i] = null;
        setKindAtPosition(movedPositioning.getExtremityKind(), moveTo);
    }

    private void removeKindAtPosition(ExtremityKind kind, int position) {
        for (int i = 0; i < extremityPositioningsForStartingPoints.get(position).length; i++) {
            if (extremityPositioningsForStartingPoints.get(position)[i].getExtremityKind() == kind) {
                extremityPositioningsForStartingPoints.get(position)[i] = null;
                break;
            }
        }
    }

    /**
     * @return -1 if there is no possible position for this kind
     */
    private int getRandomPossiblePositionForKind(ExtremityKind kind) {
        List<Integer> positionsForKind = possiblePositionsForKinds.get(kind);
        List<Integer> possiblePositions = new ArrayList<>(positionsForKind.size());
        for (int pos : positionsForKind) {
            if (getFreeCountAtPosition(pos) > 0) {
                possiblePositions.add(pos);
            }
        }
        if (possiblePositions.isEmpty()) {
            return -1;
        } else {
            return possiblePositions.get((new Random()).nextInt(possiblePositions.size()));
        }
    }

    private int getFreeCount(List<Integer> possiblePositions) {
        int count = 0;
        for (int pos : possiblePositions) {
            count += getFreeCountAtPosition(pos);
        }
        return count;
    }

    private void initializePossiblePositionsForKinds(List<Integer> forbiddenPositions) {
        ArrayList<Integer> wingPositions = new ArrayList<>(Arrays.asList(1, 2));
        ArrayList<Integer> armPositions = new ArrayList<>(Arrays.asList(1, 2));
        ArrayList<Integer> legPositions = new ArrayList<>(Arrays.asList(0, 1));
        ArrayList<Integer> finPositions = new ArrayList<>(Arrays.asList(0, 1, 2));

        for (Integer pos : forbiddenPositions) {
            wingPositions.remove(pos);
            armPositions.remove(pos);
            legPositions.remove(pos);
            finPositions.remove(pos);
        }

        possiblePositionsForKinds = new HashMap<>(4);
        possiblePositionsForKinds.put(ExtremityKind.WING, wingPositions);
        possiblePositionsForKinds.put(ExtremityKind.ARM, armPositions);
        possiblePositionsForKinds.put(ExtremityKind.LEG, legPositions);
        possiblePositionsForKinds.put(ExtremityKind.FIN, finPositions);
    }
}
