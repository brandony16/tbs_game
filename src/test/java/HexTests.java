

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import tbs_game.hexes.FractionalHex;
import tbs_game.hexes.HexPos;

public class HexTests {
    // static public void equalOffsetcoord(String name, OffsetCoord a, OffsetCoord b)
    // {
    //     if (!(a.col == b.col && a.row == b.row))
    //     {
    //         Tests.complain(name);
    //     }
    // }

    public void equalHexArray(String name, ArrayList<HexPos> a, ArrayList<HexPos> b) {
        assertEquals(name, a.size(), b.size());
        for (int i = 0; i < a.size(); i++) {
            assertEquals(name, a.get(i), b.get(i));
        }
    }

    @Test
    public void testHexArithmetic() {
        assertEquals("hex_add", new HexPos(4, -10), new HexPos(1, -3).add(new HexPos(3, -7)));
        assertEquals("hex_subtract", new HexPos(-2, 4), new HexPos(1, -3).subtract(new HexPos(3, -7)));
    }

    @Test
    public void testHexDirection() {
        assertEquals("hex_direction", new HexPos(0, -1), HexPos.direction(2));
    }

    @Test
    public void testHexNeighbor() {
        assertEquals("hex_neighbor", new HexPos(1, -3), new HexPos(1, -2).neighbor(2));
    }

    @Test
    public void testHexDiagonal() {
        assertEquals("hex_diagonal", new HexPos(-1, -1), new HexPos(1, -2).diagonalNeighbor(3));
    }

    @Test
    public void testHexDistance() {
        assertEquals("hex_distance", 7, new HexPos(3, -7).distanceTo(new HexPos(0, 0)));
    }

    @Test
    public void testHexRotateRight() {
        assertEquals("hex_rotate_right", new HexPos(1, -3).rotateRight(), new HexPos(3, -2));
    }

    @Test
    public void testHexRotateLeft() {
        assertEquals("hex_rotate_left", new HexPos(1, -3).rotateLeft(), new HexPos(-2, -1));
    }

    @Test
    public void testHexRound() {
        FractionalHex a = new FractionalHex(0.0, 0.0);
        FractionalHex b = new FractionalHex(1.0, -1.0);
        FractionalHex c = new FractionalHex(0.0, -1.0);
        assertEquals("hex_round 1", new HexPos(5, -10), new FractionalHex(0.0, 0.0).hexLerp(new FractionalHex(10.0, -20.0), 0.5).hexRound());
        assertEquals("hex_round 2", a.hexRound(), a.hexLerp(b, 0.499).hexRound());
        assertEquals("hex_round 3", b.hexRound(), a.hexLerp(b, 0.501).hexRound());
        assertEquals("hex_round 4", a.hexRound(), new FractionalHex(a.q * 0.4 + b.q * 0.3 + c.q * 0.3, a.r * 0.4 + b.r * 0.3 + c.r * 0.3).hexRound());
        assertEquals("hex_round 5", c.hexRound(), new FractionalHex(a.q * 0.3 + b.q * 0.3 + c.q * 0.4, a.r * 0.3 + b.r * 0.3 + c.r * 0.4).hexRound());
    }

    @Test
    public void testHexLinedraw() {
        equalHexArray("hex_linedraw", new ArrayList<HexPos>() {
            {
                add(new HexPos(0, 0));
                add(new HexPos(0, -1));
                add(new HexPos(0, -2));
                add(new HexPos(1, -3));
                add(new HexPos(1, -4));
                add(new HexPos(1, -5));
            }
        }, FractionalHex.hexLinedraw(new HexPos(0, 0), new HexPos(1, -5)));
    }

    // static public void testOffsetRoundtrip()
    // {
    //     for (int q = -2; q < 3; q++)
    //     {
    //         for (int r = -2; r < 3; r++)
    //         {
    //             Hex cube = new HexPos(q, r, -q - r);
    //             Tests.equalHex("conversion_roundtrip odd-q", cube, OffsetCoord.qoffsetToCube(OffsetCoord.ODD, OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, cube)));
    //             Tests.equalHex("conversion_roundtrip odd-r", cube, OffsetCoord.roffsetToCube(OffsetCoord.ODD, OffsetCoord.roffsetFromCube(OffsetCoord.ODD, cube)));
    //             Tests.equalHex("conversion_roundtrip even-q", cube, OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, cube)));
    //             Tests.equalHex("conversion_roundtrip even-r", cube, OffsetCoord.roffsetToCube(OffsetCoord.EVEN, OffsetCoord.roffsetFromCube(OffsetCoord.EVEN, cube)));
    //         }
    //     }
    //     for (int col = -2; col < 3; col++)
    //     {
    //         for (int row = -2; row < 3; row++)
    //         {
    //             OffsetCoord offset = new OffsetCoord(col, row);
    //             Tests.equalOffsetcoord("conversion_roundtrip odd-q", offset, OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, OffsetCoord.qoffsetToCube(OffsetCoord.ODD, offset)));
    //             Tests.equalOffsetcoord("conversion_roundtrip odd-r", offset, OffsetCoord.roffsetFromCube(OffsetCoord.ODD, OffsetCoord.roffsetToCube(OffsetCoord.ODD, offset)));
    //             Tests.equalOffsetcoord("conversion_roundtrip even-q", offset, OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, offset)));
    //             Tests.equalOffsetcoord("conversion_roundtrip even-r", offset, OffsetCoord.roffsetFromCube(OffsetCoord.EVEN, OffsetCoord.roffsetToCube(OffsetCoord.EVEN, offset)));
    //         }
    //     }
    // }
    // static public void testOffsetFromCube()
    // {
    //     Tests.equalOffsetcoord("offset_from_cube odd-r", new OffsetCoord(-2, 2), OffsetCoord.roffsetFromCube(OffsetCoord.ODD, new HexPos(-3, 2, 1)));
    //     Tests.equalOffsetcoord("offset_from_cube odd-r", new OffsetCoord(1, -1), OffsetCoord.roffsetFromCube(OffsetCoord.ODD, new HexPos(2, -1, -1)));
    //     Tests.equalOffsetcoord("offset_from_cube even-r", new OffsetCoord(-2, 2), OffsetCoord.roffsetFromCube(OffsetCoord.EVEN, new HexPos(-3, 2, 1)));
    //     Tests.equalOffsetcoord("offset_from_cube even-r", new OffsetCoord(2, -1), OffsetCoord.roffsetFromCube(OffsetCoord.EVEN, new HexPos(2, -1, -1)));
    //     Tests.equalOffsetcoord("offset_from_cube odd-q", new OffsetCoord(-2, 2), OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, new HexPos(-2, 3, -1)));
    //     Tests.equalOffsetcoord("offset_from_cube odd-q", new OffsetCoord(-1, -2), OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, new HexPos(-1, -1, 2)));
    //     Tests.equalOffsetcoord("offset_from_cube even-q", new OffsetCoord(-2, 2), OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, new HexPos(-2, 3, -1)));
    //     Tests.equalOffsetcoord("offset_from_cube even-q", new OffsetCoord(-1, -1), OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, new HexPos(-1, -1, 2)));
    // }
    // static public void testOffsetToCube()
    // {
    //     Tests.equalHex("offset_to_cube odd-r", new HexPos(-3, 2, 1), OffsetCoord.roffsetToCube(OffsetCoord.ODD, new OffsetCoord(-2, 2)));
    //     Tests.equalHex("offset_to_cube odd-r", new HexPos(2, -1, -1), OffsetCoord.roffsetToCube(OffsetCoord.ODD, new OffsetCoord(1, -1)));
    //     Tests.equalHex("offset_to_cube even-r", new HexPos(-3, 2, 1), OffsetCoord.roffsetToCube(OffsetCoord.EVEN, new OffsetCoord(-2, 2)));
    //     Tests.equalHex("offset_to_cube even-r", new HexPos(2, -1, -1), OffsetCoord.roffsetToCube(OffsetCoord.EVEN, new OffsetCoord(2, -1)));
    //     Tests.equalHex("offset_to_cube odd-q", new HexPos(-2, 3, -1), OffsetCoord.qoffsetToCube(OffsetCoord.ODD, new OffsetCoord(-2, 2)));
    //     Tests.equalHex("offset_to_cube odd-q", new HexPos(-1, -1, 2), OffsetCoord.qoffsetToCube(OffsetCoord.ODD, new OffsetCoord(-1, -2)));
    //     Tests.equalHex("offset_to_cube even-q", new HexPos(-2, 3, -1), OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, new OffsetCoord(-2, 2)));
    //     Tests.equalHex("offset_to_cube even-q", new HexPos(-1, -1, 2), OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, new OffsetCoord(-1, -1)));
    // }
}
