package tbs_game.gui;

import tbs_game.hexes.AxialPos;

public record HoverContext(
        AxialPos selected,
        AxialPos hovered,
        boolean canAttack
        ) {

}
