import controller.Decoder;
import controller.Encoder;
import domain.PPM;
import domain.Block;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        PPM ppm = new PPM("images/nt-P3.ppm");
        Encoder encoder = new Encoder(ppm);
        encoder.encodeLab1();

        List<Block> yBlocks = encoder.getYBlocks();
        List<Block> uBlocks = encoder.getUBlocks();
        List<Block> vBlocks = encoder.getVBlocks();

        encoder.encodeLab2(yBlocks, uBlocks, vBlocks);

        List<Block> yBlocksQuantized = encoder.getQuantizationMatrixY();
        List<Block> uBlocksQuantized = encoder.getQuantizationMatrixU();
        List<Block> vBlocksQuantized = encoder.getQuantizationMatrixV();

        // Encoder for lab 3 will take as input the 8x8 blocks returned from lab 2 encoder
        List<Integer> entropy = encoder.encodeLab3(yBlocksQuantized, uBlocksQuantized, vBlocksQuantized);

        // Decoder lab 1
        //Decoder decoder = new Decoder(yBlocks, uBlocks, vBlocks);

        // Decoder lab 2
        //Decoder decoder = new Decoder(yBlocksQuantized, uBlocksQuantized, vBlocksQuantized);

        // Decoder lab 3
        Decoder decoder = new Decoder(entropy, ppm.getWidth(), ppm.getHeight());
        decoder.decode();
        decoder.createDecodedImage(ppm.getFormat(), ppm.getMaxValue());
    }
}
