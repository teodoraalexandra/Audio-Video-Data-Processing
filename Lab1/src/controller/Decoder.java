package controller;

import domain.Block;
import domain.PPM;

import java.io.IOException;
import java.util.List;

public class Decoder {
    private PPM decodedImage;
    private Operations operations = new Operations();
    private List<Block> yBlocks;
    private List<Block> uBlocks;
    private List<Block> vBlocks;
    double[][] y;
    double[][] u;
    double[][] v;

    public Decoder(List<Block> yBlocks, List<Block> uBlocks, List<Block> vBlocks) {
        this.yBlocks = yBlocks;
        this.uBlocks = uBlocks;
        this.vBlocks = vBlocks;
    }

    public void decode() {
        // Before passing to what we've done to the first lab, we should do some additional operations
        // DeQuantization
        List<Block> deQuantizedY = operations.deQuantizationPhase(yBlocks);
        List<Block> deQuantizedU = operations.deQuantizationPhase(uBlocks);
        List<Block> deQuantizedV = operations.deQuantizationPhase(vBlocks);

        // Inverse DCT
        List<Block> yDCT = operations.inverseDCT(deQuantizedY);
        List<Block> uDCT = operations.inverseDCT(deQuantizedU);
        List<Block> vDCT = operations.inverseDCT(deQuantizedV);

        // Add 128
        List<Block> addedY = operations.addValue(yDCT);
        List<Block> addedU = operations.addValue(uDCT);
        List<Block> addedV = operations.addValue(vDCT);

        // u and v must be decompressed -> back to 8x8 :)
        //List<Block> decompressedU = operations.blocksToMatrices(uBlocks);
        //List<Block> decompressedV = operations.blocksToMatrices(vBlocks);
        y = operations.createReconstructedMatrix(addedY);
        u = operations.createReconstructedMatrix(addedU);
        v = operations.createReconstructedMatrix(addedV);
    }

    public void createDecodedImage(String format, int maxValue, int width, int height) throws IOException {
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
}
