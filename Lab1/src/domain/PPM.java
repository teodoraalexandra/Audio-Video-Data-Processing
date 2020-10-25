package domain;

import java.io.*;
import java.util.Arrays;


public class PPM {
    private String fileName;
    private String format;
    private Integer maxValue;
    private int height;
    private int width;

    // matrices for RGB
    private int[][] r;
    private int[][] g;
    private int[][] b;

    // matrices for YUV
    private double[][] y;
    private double[][] u;
    private double[][] v;

    public PPM(String fileName){
        this.fileName = fileName;
    }

    public void readPPM() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        this.format = reader.readLine();
        reader.readLine(); // Comment line
        String dimensions = reader.readLine();
        String[] split = dimensions.split(" ");
        this.width = Integer.parseInt(split[0]);
        this.height = Integer.parseInt(split[1]);
        this.maxValue = Integer.parseInt(reader.readLine());

        reader.close();
    }

    public void generateRGBMatrices() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        // First lines have been read
        reader.readLine();
        reader.readLine();
        reader.readLine();
        reader.readLine();

        this.r = new int[this.height][this.width];
        this.g = new int[this.height][this.width];
        this.b = new int[this.height][this.width];

        int line = 0;
        int column = 0;

        while (true) {
            if(column == this.width) {
                column = 0;
                line++;
            }
            if(line == this.height) break;
            r[line][column] = Integer.parseInt(reader.readLine());
            g[line][column] = Integer.parseInt(reader.readLine());
            b[line][column] = Integer.parseInt(reader.readLine());
            column++;
        }

        reader.close();
    }

    public void convertRGBtoYUV() {
        this.y = new double[height][width];
        this.u = new double[height][width];
        this.v = new double[height][width];

        for (int line = 0; line < this.height; line++) {
            for (int column = 0; column < this.width; column++) {
                y[line][column] = 0.299 * r[line][column] + 0.587 * g[line][column] + 0.114 * b[line][column];
                u[line][column] = 128 - 0.168736 * r[line][column] - 0.331264 * g[line][column] + 0.5 * b[line][column];
                v[line][column] = 128 + 0.5 * r[line][column] - 0.418688 * g[line][column] - 0.081312 * b[line][column];
            }
        }
    }

    // Getters and setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int[][] getR() {
        return r;
    }

    public void setR(int[][] r) {
        this.r = r;
    }

    public int[][] getG() {
        return g;
    }

    public void setG(int[][] g) {
        this.g = g;
    }

    public int[][] getB() {
        return b;
    }

    public void setB(int[][] b) {
        this.b = b;
    }

    public double[][] getY() {
        return y;
    }

    public void setY(double[][] y) {
        this.y = y;
    }

    public double[][] getU() {
        return u;
    }

    public void setU(double[][] u) {
        this.u = u;
    }

    public double[][] getV() {
        return v;
    }

    public void setV(double[][] v) {
        this.v = v;
    }
}
