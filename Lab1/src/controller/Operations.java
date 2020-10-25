package controller;

import domain.Block;
import domain.PPM;

import java.util.ArrayList;
import java.util.List;

public class Operations {
    private double[][] matrix8x8(int x, int y, double[][] YMatrix) {
        double[][] result = new double[8][8];
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                result[i][j] = YMatrix[x + i][y + j];
            }
        }
        return result;
    }

    private double[][] matrix4x4(int x, int y, double[][] UVMatrix){
        // 4:2:0 sub-sampling a 8x8 matrix
        double[][] subMatrix = this.matrix8x8(x, y, UVMatrix);
        double[][] result = new double[4][4];
        int line = 0;
        int column = 0;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                result[i][j] = (subMatrix[line][column] +
                        subMatrix[line + 1][column] +
                        subMatrix[line][column + 1] +
                        subMatrix[line + 1][column + 1]) / 4.0;
                column += 2;
            }
            line += 2;
            column = 0;
        }
        return result;
    }

    // Transform the Y matrix in blocks of 8x8
    // Transform the U and V matrices in blocks 4x4
    public List<Block> matricesToBlocks(PPM image, double[][] matrix, String type){
        List<Block> blocks = new ArrayList<>();
        // Go through PPM image
        // (i, j) is the position of the block in the image
        for(int i = 0; i < image.getHeight(); i = i + 8) {
            for (int j = 0; j < image.getWidth(); j = j + 8) {
                double[][] elements;
                int size;
                if (type.equals("Y")) {
                    size = 8;
                    elements = matrix8x8(i, j, matrix);
                } else {
                    // For U and V
                    size = 4;
                    elements = matrix4x4(i, j, matrix);
                }

                // Store the blocks in a list
                Block block = new Block(size, type);
                block.setInitialHeight(image.getHeight());
                block.setInitialWidth(image.getWidth());
                block.setBlock(elements);
                block.setXCoordinate(i);
                block.setYCoordinate(j);
                blocks.add(block);
            }
        }
        return blocks;
    }
}
