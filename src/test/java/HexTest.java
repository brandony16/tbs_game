
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import tbs_game.hexes.FractionalHex;
import tbs_game.hexes.AxialPos;
import tbs_game.hexes.OffsetPos;

public class HexTest {
    // static public void equalOffsetcoord(String name, OffsetCoord a, OffsetCoord b)
    // {
    //     if (!(a.col == b.col && a.row == b.row))
    //     {
    //         Tests.complain(name);
    //     }
    // }

    public void equalHexArray(ArrayList<AxialPos> a, ArrayList<AxialPos> b) {
        assertEquals(a.size(), b.size());
        for (int i = 0; i < a.size(); i++) {
            assertEquals(a.get(i), b.get(i));
        }
    }

    @Test
    void testHexArithmetic() {
        assertEquals(new AxialPos(4, -10), new AxialPos(1, -3).add(new AxialPos(3, -7)));
        assertEquals(new AxialPos(-2, 4), new AxialPos(1, -3).subtract(new AxialPos(3, -7)));
    }

    @Test
    void testHexDirection() {
        assertEquals(new AxialPos(0, -1), AxialPos.direction(2));
    }

    @Test
    void testHexNeighbor() {
        assertEquals(new AxialPos(1, -3), new AxialPos(1, -2).neighbor(2));
    }

    @Test
    void testHexDiagonal() {
        assertEquals(new AxialPos(-1, -1), new AxialPos(1, -2).diagonalNeighbor(3));
    }

    @Test
    void testHexDistance() {
        assertEquals(7, new AxialPos(3, -7).distanceTo(new AxialPos(0, 0)));
    }

    @Test
    void testHexRotateRight() {
        assertEquals(new AxialPos(1, -3).rotateRight(), new AxialPos(3, -2));
    }

    @Test
    void testHexRotateLeft() {
        assertEquals(new AxialPos(1, -3).rotateLeft(), new AxialPos(-2, -1));
    }

    @Test
    void testHexRound() {
        FractionalHex a = new FractionalHex(0.0, 0.0);
        FractionalHex b = new FractionalHex(1.0, -1.0);
        FractionalHex c = new FractionalHex(0.0, -1.0);
        assertEquals(new AxialPos(5, -10), new FractionalHex(0.0, 0.0).hexLerp(new FractionalHex(10.0, -20.0), 0.5).hexRound());
        assertEquals(a.hexRound(), a.hexLerp(b, 0.499).hexRound());
        assertEquals(b.hexRound(), a.hexLerp(b, 0.501).hexRound());
        assertEquals(a.hexRound(), new FractionalHex(a.q * 0.4 + b.q * 0.3 + c.q * 0.3, a.r * 0.4 + b.r * 0.3 + c.r * 0.3).hexRound());
        assertEquals(c.hexRound(), new FractionalHex(a.q * 0.3 + b.q * 0.3 + c.q * 0.4, a.r * 0.3 + b.r * 0.3 + c.r * 0.4).hexRound());
    }

    @Test
    void testHexLinedraw() {
        equalHexArray(new ArrayList<AxialPos>() {
            {
                add(new AxialPos(0, 0));
                add(new AxialPos(0, -1));
                add(new AxialPos(0, -2));
                add(new AxialPos(1, -3));
                add(new AxialPos(1, -4));
                add(new AxialPos(1, -5));
            }
        }, FractionalHex.hexLinedraw(new AxialPos(0, 0), new AxialPos(1, -5)));
    }

    @Test
    void testOffsetFromAxial() {
        assertEquals(new OffsetPos(-2, 2), OffsetPos.offsetFromAxial(new AxialPos(-3, 2)));
        assertEquals(new OffsetPos(1, -1), OffsetPos.offsetFromAxial(new AxialPos(2, -1)));

        assertEquals(new OffsetPos(-2, 2), new AxialPos(-3, 2).toOffset());
        assertEquals(new OffsetPos(1, -1), new AxialPos(2, -1).toOffset());
    }

    @Test
    void testOffsetToAxial() {
        assertEquals(new AxialPos(-3, 2), OffsetPos.offsetToAxial(new OffsetPos(-2, 2)));
        assertEquals(new AxialPos(2, -1), OffsetPos.offsetToAxial(new OffsetPos(1, -1)));

        assertEquals(new AxialPos(-3, 2), new OffsetPos(-2, 2).toAxial());
        assertEquals(new AxialPos(2, -1), new OffsetPos(1, -1).toAxial());
    }
}
