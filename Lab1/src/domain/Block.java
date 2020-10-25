package domain;

import java.util.Arrays;

public class Block {
    private String blockType;
    private int size;
    private int xCoordinate;
    private int yCoordinate;
    private int initialWidth;
    private int initialHeight;
    private double[][] block;

    public Block(int size, String blockType){
        this.size = size;
        this.blockType = blockType;
        this.block = new double[size][size];
    }

    @Override
    public String toString() {
        return "Block{" +
                "blockType='" + blockType + '\'' +
                ", xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                ", block=" + Arrays.deepToString(block) +
                '}';
    }

    // Getters and setters

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public int getInitialWidth() {
        return initialWidth;
    }

    public void setInitialWidth(int initialWidth) {
        this.initialWidth = initialWidth;
    }

    public int getInitialHeight() {
        return initialHeight;
    }

    public void setInitialHeight(int initialHeight) {
        this.initialHeight = initialHeight;
    }

    public double[][] getBlock() {
        return block;
    }

    public void setBlock(double[][] block) {
        this.block = block;
    }
}
