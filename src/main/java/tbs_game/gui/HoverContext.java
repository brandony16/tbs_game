package tbs_game.gui;

import tbs_game.HexPos;

public record HoverContext(
        HexPos selected,
        HexPos hovered,
        boolean canAttack
        ) {

}
