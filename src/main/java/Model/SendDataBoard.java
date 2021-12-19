package Model;

public class SendDataBoard {
    private String buttonId;
    private boolean smallWin;
    private boolean bigWin;
    private int block;
    private int nextBlock;

    public String getButtonId() {
        return buttonId;
    }

    public boolean isSmallWin() {
        return smallWin;
    }

    public boolean isBigWin() {
        return bigWin;
    }

    public int getBlock() {
        return block;
    }

    public int getNextBlock() {
        return nextBlock;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public void setSmallWin(boolean smallWin) {
        this.smallWin = smallWin;
    }

    public void setBigWin(boolean bigWin) {
        this.bigWin = bigWin;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public void setNextBlock(int nextBlock) {
        this.nextBlock = nextBlock;
    }
}
