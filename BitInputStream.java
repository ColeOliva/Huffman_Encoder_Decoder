import java.io.*;
import java.util.*;

public class BitInputStream implements AutoCloseable {
    private FileInputStream input;
    private int currentByte; // current set of bits (buffer)
    private int nextByte; // next set of bits (buffer)
    private int numBits; // how many bits from buffer have been used
    private int remainingAtEnd; // how many bits will be remaining at the end
                                // after we're done

    private static final int BYTE_SIZE = 8; // bits per byte

    // pre : given file name is legal
    // post: creates a BitInputStream reading input from the file
    public BitInputStream(String file) {
        try {
            this.input = new FileInputStream(file);

            // Read in the number of remaining bits at the end
            this.remainingAtEnd = this.input.read();

            // Set up the nextByte field.
            this.nextByte = this.input.read();
        } catch (IOException ex) {
            throw new RuntimeException(ex.toString());
        }

        this.nextByte();
    }

    public boolean hasNextBit() {
        boolean atEnd = this.currentByte == -1;
        boolean onlyRemaining = this.nextByte == -1
                && BYTE_SIZE - this.numBits == this.remainingAtEnd;
        return !atEnd && !onlyRemaining;
    }

    // post: reads next bit from input (-1 if at end of file)
    // throws NoSuchElementException if there is no bit to return
    public int nextBit() {
        // if at eof, throw exception
        if (!this.hasNextBit()) {
            throw new NoSuchElementException();
        }
        int result = this.currentByte % 2;
        this.currentByte /= 2;
        this.numBits++;
        if (this.numBits == BYTE_SIZE) {
            this.nextByte();
        }
        return result;
    }

    // post: refreshes the internal buffer with the next BYTE_SIZE bits
    private void nextByte() {
        this.currentByte = this.nextByte;
        if (this.currentByte != -1) {
            try {
                this.nextByte = this.input.read();
            } catch (IOException e) {
                throw new RuntimeException(e.toString());
            }
        }

        this.numBits = 0;
    }

    // post: input is closed
    @Override
    public void close() {
        try {
            if (this.input != null) {
                this.input.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }
}
