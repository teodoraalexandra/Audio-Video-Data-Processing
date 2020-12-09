package controller;

import domain.Block;
import domain.PPM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Decoder {
    private PPM decodedImage;
    private Operations operations = new Operations();
    private List<Block> yBlocks = new ArrayList<>();
    private List<Block> uBlocks = new ArrayList<>();
    private List<Block> vBlocks = new ArrayList<>();
    private List<Integer> entropy;
    private List<Block> deQuantizedY;
    private List<Block> deQuantizedU;
    private List<Block> deQuantizedV;
    private int position = 0;
    private int width = 0;
    private int height = 0;
    private double[][] y = new double[600][800];
    private double[][] u = new double[600][800];
    private double[][] v = new double[600][800];

    public Decoder(List<Block> yBlocks, List<Block> uBlocks, List<Block> vBlocks) {
        this.yBlocks = yBlocks;
        this.uBlocks = uBlocks;
        this.vBlocks = vBlocks;
    }

    public Decoder(List<Integer> entropy, int width, int height) {
        this.entropy = entropy;
        this.width = width;
        this.height = height;
        entropyDecoding();
    }

    private void entropyDecoding() {
        position = 0;
        while (position < entropy.size())
        {
            Block blockY = new Block(8, "Y");
            blockY.setInitialHeight(height);
            blockY.setInitialWidth(width);
            blockY.setIntegersBlock(decoderZigZagParsing());
            this.yBlocks.add(blockY);

            Block blockU = new Block(8, "U");
            blockU.setInitialHeight(height);
            blockU.setInitialWidth(width);
            blockU.setIntegersBlock(decoderZigZagParsing());
            this.uBlocks.add(blockU);

            Block blockV = new Block(8, "V");
            blockV.setInitialHeight(height);
            blockV.setInitialWidth(width);
            blockV.setIntegersBlock(decoderZigZagParsing());
            this.vBlocks.add(blockV);
        }
    }

    public void decode() {
        // Before passing to what we've done to the first lab, we should do some additional operations
        // DeQuantization
        deQuantizedY = operations.deQuantizationPhase(this.yBlocks);
        deQuantizedU = operations.deQuantizationPhase(this.uBlocks);
        deQuantizedV = operations.deQuantizationPhase(this.vBlocks);

        // Inverse DCT
        List<Block> yDCT = operations.inverseDCT(deQuantizedY);
        List<Block> uDCT = operations.inverseDCT(deQuantizedU);
        List<Block> vDCT = operations.inverseDCT(deQuantizedV);

        // Add 128
        List<Block> addedY = operations.addValue(yDCT);
        List<Block> addedU = operations.addValue(uDCT);
        List<Block> addedV = operations.addValue(vDCT);

        y = operations.createReconstructedMatrix(addedY);
        u = operations.createReconstructedMatrix(addedU);
        v = operations.createReconstructedMatrix(addedV);
    }

    public void createDecodedImage(String format, int maxValue) throws IOException {
        PPM decodedImage = new PPM();
        decodedImage.setFileName("images/decoded-nt-P3.ppm");
        decodedImage.setFormat(format);
        decodedImage.setMaxValue(maxValue);
        decodedImage.setWidth(width);
        decodedImage.setHeight(height);
        decodedImage.setY(y);
        decodedImage.setU(u);
        decodedImage.setV(v);
        decodedImage = decodedImage.convertYUVtoRGB();
        decodedImage.writePPM();
    }

    // Decoder lab 3 operations
    private int[][] decoderZigZagParsing() {
        int[][] matrix = new int[8][8];

        position++;
        matrix[0][0] = entropy.get(position++);

        // If we find 2 consecutive zeros, return the matrix
        if (entropy.get(position) == 0 && entropy.get(position + 1) == 0) {
            position += 2;
            return matrix;
        }

        int column = 0;
        int row = 0;

        do {
            column++;
            if (setMatrix(row, column, matrix)) return matrix;

            do {
                row++;
                column--;
                if (setMatrix(row, column, matrix)) return matrix;
            } while (column != 0);

            if (row == 7)
                break;
            row++;
            if (setMatrix(row, column, matrix)) return matrix;
            do {
                row--;
                column++;
                if (setMatrix(row, column, matrix)) return matrix;
            } while (row != 0);
        } while (true);

        do {
            column++;
            if (setMatrix(row, column, matrix)) return matrix;
            if (column == 7)
                break;
            do {
                row--;
                column++;
                if (setMatrix(row, column, matrix)) return matrix;
            } while (column != 7);
            row++;
            if (setMatrix(row, column, matrix)) return matrix;
            do {
                row++;
                column--;
                if (setMatrix(row, column, matrix)) return matrix;
            } while (row != 7);
        } while (true);

        return matrix;
    }


    private boolean setMatrix(int row, int column, int[][] matrix) {
        // If 2 consecutive zeros are found, we should return 0
        if (entropy.get(position) == 0 && entropy.get(position + 1) == 0) {
            position += 2;
            return true;
        }

        if (entropy.get(position) == 0) {
            // Return the corresponding amplitude
            matrix[row][column] = entropy.get(position + 2);
            // Go to the next tuple of bytes
            position += 3;
        } else {
            // This was not encoded -> add 0
            matrix[row][column] = 0;
            // There are of consecutive zeroes that occur in front
            entropy.set(position, entropy.get(position) - 1);
        }

        return false;
    }
}


