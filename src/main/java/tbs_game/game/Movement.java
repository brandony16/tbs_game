package tbs_game.game;

import java.util.HashSet;
import java.util.Set;

import tbs_game.hexes.HexPos;
import tbs_game.units.Unit;

public final class Movement {

    public void move(Game game, HexPos from, HexPos to) {
        Unit mover = game.getUnitAt(from);
        int dist = from.distanceTo(to);

        mover.spendMovementPoints(dist);

        game.moveUnitInternal(from, to);
    }

    public Set<HexPos> getReachableHexes(Game game, HexPos from) {
        Set<HexPos> reachableHexes = new HashSet<>();

        // Confirm a unit is at the tile
        Unit unit = game.getUnitAt(from);
        if (unit == null) {
            return reachableHexes;
        }

        int range = unit.getMovementPoints();

        // See if each hex is in range of the unit
        for (HexPos pos : game.getBoard().getPositions()) {
            if (from.distanceTo(pos) <= range && !game.isFriendly(pos, unit.getOwner())) {
                reachableHexes.add(pos);
            }
        }

        return reachableHexes;
    }
}
