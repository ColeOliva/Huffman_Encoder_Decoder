import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BitOutputStream implements AutoCloseable {
    private PrintStream output;
    private List<Integer> buffer;
    private int currentByte; // a buffer used to build up the next set of digits
    private int numBits; // how many digits are currently in the buffer
    private boolean debug; // set to true to write ASCII 0s and 1s rather than bits

    private static final int BYTE_SIZE = 8; // digits per byte

    // Creates a BitOutputStream sending output to the given stream. If debug
    // is set to true, bits are printed as ASCII 0s and 1s.
    public BitOutputStream(PrintStream output, boolean debug) {
        this.buffer = new ArrayList<>();
        this.output = output;
        this.debug = debug;
    }

    // Writes the given bit to output
    public void write(int bit) {
        if (this.debug) {
            System.out.print(bit);
        }
        if (bit < 0 || bit > 1) {
            throw new IllegalArgumentException("Illegal bit: " + bit);
        }
        this.currentByte += bit << this.numBits;
        this.numBits++;
        if (this.numBits == BYTE_SIZE) {
            this.buffer.add(this.currentByte);
            this.numBits = 0;
            this.currentByte = 0;
        }
    }

    // post: flushes remaining bits and closes the output
    @Override
    public void close() {
        try {
            int remaining = BYTE_SIZE - this.numBits;

            if (remaining == BYTE_SIZE) {
                remaining = 0;
            }

            // Flush the last byte (if there is one)
            if (remaining > 0) {
                this.buffer.add(this.currentByte);
            }

            // Prepend with the number of missing bits from the end
            this.output.write(remaining);
            for (int b : this.buffer) {
                this.output.write(b);
            }
        } finally {
            this.output.close();
        }
    }
}
