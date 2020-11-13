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

        //System.out.println(yBlocksQuantized);
        //System.out.println(uBlocksQuantized);
        //System.out.println(vBlocksQuantized);

        // This was used at the first lab
        //Decoder decoder = new Decoder(yBlocks, uBlocks, vBlocks);
        // For this lab we will use the quantized 8x8 blocks
        Decoder decoder = new Decoder(yBlocksQuantized, uBlocksQuantized, vBlocksQuantized);
        decoder.decode();
        decoder.createDecodedImage(ppm.getFormat(), ppm.getMaxValue(), ppm.getWidth(), ppm.getHeight());
    }
}
