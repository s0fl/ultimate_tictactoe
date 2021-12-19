import Model.Board;
import org.junit.Assert;
import org.junit.Before;

public class Test {
    Board board;

    @Before
    public void init(){
        board = new Board();
        board.setMove(1, 1, 1, 1);
        board.setMove(2, 4, 0, 1);
        board.setMove(1, 1, 2, 1);
        board.setMove(2, 7, 0, 1);
    }

    @org.junit.Test
    public void setMove() {
        if (board.setMove(1, 7, 0, 1))
            Assert.assertEquals(false, true);
        else
            Assert.assertEquals(true, false);
    }

    @org.junit.Test
    public void isEnd() {
        if(board.isEnd())
            Assert.assertEquals(false, true);
        else
            Assert.assertEquals(false, false);
    }

    @org.junit.Test
    public void testIsEnd() {
        board.setMove(1, 1, 0,1);
        if(board.isEnd(1))
            Assert.assertEquals(true, true);
        else
            Assert.assertEquals(true, false);
    }
}
