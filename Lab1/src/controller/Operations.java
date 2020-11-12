package controller;

import domain.Block;
import domain.PPM;

import java.util.ArrayList;
import java.util.List;

class Operations {
    // Quantization matrix
    private double[][] Q = {
            {6, 4, 4, 6, 10, 16, 20, 24},
            {5, 5, 6, 8, 10, 23, 24, 22},
            {6, 5, 6, 10, 16, 23, 28, 22},
            {6, 7, 9, 12, 20, 35, 32, 25},
            {7, 9, 15, 22, 27, 44, 41, 31},
            {10, 14, 22, 26, 32, 42, 45, 37},
            {20, 26, 31, 35, 41, 48, 48, 40},
            {29, 37, 38, 39, 45, 40, 41, 40}
    };

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
    List<Block> matricesToBlocks(PPM image, double[][] matrix, String type){
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

    List<Block> blocksToMatrices(List<Block> blocks) {
        List<Block> blocksToMatrices = new ArrayList<>();
        // Block is a compressed matrix on 4x4
        blocks.forEach(block -> blocksToMatrices.add(matrix8x8(block)));
        return blocksToMatrices;
    }

    // Create the decompressed version of 8x8
    private Block matrix8x8(Block initialBlock){
        Block newBlock = new Block(8, initialBlock.getBlockType());
        newBlock.setInitialWidth(initialBlock.getInitialWidth());
        newBlock.setInitialHeight(initialBlock.getInitialHeight());
        newBlock.setXCoordinate(initialBlock.getXCoordinate());
        newBlock.setYCoordinate(initialBlock.getYCoordinate());
        newBlock.setXCoordinate(initialBlock.getXCoordinate());
        newBlock.setYCoordinate(initialBlock.getYCoordinate());
        double[][] elements = new double[8][8];
        // We want to keep the old version too
        double[][] olderVersion = initialBlock.getBlock();
        int line = 0;
        int column = 0;

        // Each 2x2 square has the value provided by one element from the compressed 4x4 matrix
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                elements[line][column] = olderVersion[i][j];
                elements[line + 1][column] = olderVersion[i][j];
                elements[line][column + 1] = olderVersion[i][j];
                elements[line + 1][column + 1] = olderVersion[i][j];
                column += 2;
            }
            column = 0;
            line += 2;
        }
        newBlock.setBlock(elements);
        return newBlock;
    }

    double[][] createReconstructedMatrix(List<Block> blocks) {
        // Must be double[600][800]
        double[][] elements = new double[blocks.get(0).getInitialHeight()][blocks.get(0).getInitialWidth()];
        for(Block block: blocks){
            int line = 0;
            int column = 0;
            // Matrices are 8x8 now
            for(int i = block.getXCoordinate(); i < block.getXCoordinate() + 8; i++) {
                for(int j = block.getYCoordinate(); j < block.getYCoordinate() + 8; j++) {
                    elements[i][j] = block.getBlock()[line][column];
                    column++;
                }
                column=0;
                line++;
            }
        }
        return elements;
    }

    // Before DCT, we have to substract 128 from every value of 8x8 Block
    List<Block> substractValue(List<Block> encoded) {
        for (Block block: encoded) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    block.getBlock()[i][j] -= 128.0;
                }
            }
        }
        return encoded;
    }

    // DCT phase
    List<Block> forwardDCT(List<Block> encoded) {
        for (Block block: encoded)
            block.setIntegersBlock(DCT(block.getBlock()));
        return encoded;
    }

    private int[][] DCT(double[][] matrix) {
        int[][] G = new int[8][8];
        double constant = (double) 1 / 4;

        for (int u = 0; u < 8; u++)
            for (int v = 0; v < 8; v++)
            {
                G[u][v] = (int) (constant * alpha(u) * alpha(v) * outerSum(matrix, u, v));
            }

        return G;
    }

    private double alpha(int value) {
        return value > 0 ? 1 : (1 / Math.sqrt(2.0));
    }

    private double outerSum(double[][] matrix, int u, int v) {
        double sum = 0.0;
        for (int x = 0; x < 8; x++)
            sum += innerSum(matrix, u, v, x);
        return sum;
    }

    private double innerSum(double[][] matrix, int u, int v, int x) {
        double sum = 0.0;
        for (int y = 0; y < 8; y++)
            sum += product(matrix[x][y], x, y, u, v);
        return sum;
    }

    private double product(double matrixValue, int x, int y, int u, int v) {
        double cosU = Math.cos(
                ((2 * x + 1) * u * Math.PI) / 16
        );

        double cosV = Math.cos(
                ((2 * y + 1) * v * Math.PI) / 16
        );

        return matrixValue * cosU * cosV;
    }

    // Quantization phase
    List<Block> quantizationPhase(List<Block> encoded) {
        for (Block block: encoded)
            block.setIntegersBlock(MatrixOperations.divideMatrices(block.getIntegersBlock(), Q));
        return encoded;
    }
}
