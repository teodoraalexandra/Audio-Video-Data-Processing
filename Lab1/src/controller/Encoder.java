package controller;

import domain.Block;
import domain.PPM;

import java.io.IOException;
import java.util.List;

public class Encoder {
    private PPM ppm;
    private Operations operations;

    public Encoder(PPM ppm) throws IOException {
        this.ppm = ppm;
        this.operations = new Operations();
        this.encode();
    }

    private void encode() throws IOException {
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
}
