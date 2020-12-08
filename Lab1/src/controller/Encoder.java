package controller;

import domain.Block;
import domain.PPM;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Encoder {
    private PPM ppm;
    private Operations operations;
    private List<Block> quantizationMatrixY;
    private List<Block> quantizationMatrixU;
    private List<Block> quantizationMatrixV;

    public Encoder(PPM ppm) throws IOException {
        this.ppm = ppm;
        this.operations = new Operations();
    }

    public void encodeLab1() throws IOException {
        // Read the PPM image
        this.ppm.readPPM();

        // Form 3 matrices: one for R components, one for G components and one for G components
        this.ppm.generateRGBMatrices();

        // Convert each pixel value from RGB to YUV
        this.ppm.convertRGBtoYUV();
    }

    public List<Block> getYBlocks() {
        return operations.matricesToBlocks(this.ppm, this.ppm.getY(), "Y");
    }

    public List<Block> getUBlocks() {
        return operations.matricesToBlocks(this.ppm, this.ppm.getU(), "U");
    }

    public List<Block> getVBlocks() {
        return operations.matricesToBlocks(this.ppm, this.ppm.getV(), "V");
    }

    // Continuation for Lab2 ---> Discrete Cosine Transformation and Quantization
    public void encodeLab2(List<Block> yBlocks, List<Block> uBlocks, List<Block> vBlocks) {
        // U and V are 4x4, so we must get them back to 8x8
        // Perform same operation as decoder did in Lab1
        List<Block> decompressedU = operations.blocksToMatrices(uBlocks);
        List<Block> decompressedV = operations.blocksToMatrices(vBlocks);

        // Substract 128
        List<Block> substractedY = operations.substractValue(yBlocks);
        List<Block> substractedU = operations.substractValue(decompressedU);
        List<Block> substractedV = operations.substractValue(decompressedV);

        // Transforms these blocks intro another 8x8 DCT coefficient blocks
        List<Block> yDCT = operations.forwardDCT(substractedY);
        List<Block> uDCT = operations.forwardDCT(substractedU);
        List<Block> vDCT = operations.forwardDCT(substractedV);

        // Perform quantization
        this.quantizationMatrixY = operations.quantizationPhase(yDCT);
        this.quantizationMatrixU = operations.quantizationPhase(uDCT);
        this.quantizationMatrixV = operations.quantizationPhase(vDCT);
    }

    // Continuation for Lab 3 ---> Entropy encoding (ZigZag parsing and run-length encoding)
    public List<Integer> encodeLab3(List<Block> yBlocks, List<Block> uBlocks, List<Block> vBlocks) {
        for (int i = 0; i < yBlocks.size(); i++) {
            operations.constructEntropy(yBlocks.get(i).getIntegersBlock());
            operations.constructEntropy(uBlocks.get(i).getIntegersBlock());
            operations.constructEntropy(vBlocks.get(i).getIntegersBlock());
        }

        return operations.getEntropy();
    }

    public List<Block> getQuantizationMatrixY() {
        return quantizationMatrixY;
    }

    public List<Block> getQuantizationMatrixU() {
        return quantizationMatrixU;
    }

    public List<Block> getQuantizationMatrixV() {
        return quantizationMatrixV;
    }
}
