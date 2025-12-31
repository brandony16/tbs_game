package tbs_game.gui;

import tbs_game.hexes.HexPos;

public record HoverContext(
        HexPos selected,
        HexPos hovered,
        boolean canAttack
        ) {

}
