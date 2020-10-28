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

        List<Block> yBlocks = encoder.getYBlocks();
        List<Block> uBlocks = encoder.getUBlocks();
        List<Block> vBlocks = encoder.getVBlocks();

        //System.out.println(yBlocks);
        //System.out.println(uBlocks);
        //System.out.println(vBlocks);

        Decoder decoder = new Decoder(yBlocks, uBlocks, vBlocks);
        decoder.decode();
        decoder.createDecodedImage(ppm.getFormat(), ppm.getMaxValue(), ppm.getWidth(), ppm.getHeight());
    }
}
