/**
 * this class represents a Huffman coding algorithm implementation.
 * It provides functionality to encode and decode messages using Huffman codes.
 * The class constructs a Huffman tree based on given frequencies or a previously 
 * constructed code file, and can save the Huffman code to an output stream and decode 
 * a compressed message using the Huffman code.
 */

 import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
 
 public class HuffmanCode {
 
     // initializing fields
     private HuffmanNode codeTree;
     
      /**
      * Constructs a HuffmanCode object based on the given frequencies.
      * constructs the Huffman tree by combining nodes with the lowest frequencies.
      *
      * @param frequencies - an array of frequencies for characters
      */
     public HuffmanCode(int[] frequencies) {
         
         Queue<HuffmanNode> prq = new PriorityQueue<>();
 
         for (int i = 0; i < frequencies.length; i++) {
             if (frequencies[i] > 0) {
                 char ch = (char) i;
                 prq.add(new HuffmanNode(ch, frequencies[i]));
             }
         }
 
         while (prq.size() > 1) {
             HuffmanNode first = prq.remove();
             HuffmanNode second = prq.remove();
             HuffmanNode both = new HuffmanNode('\u0000', first.frequency + second.frequency,
                                                first, second);
             prq.add(both);
         }
         this.codeTree = prq.remove();
     }
 
     /**
     * Constructs a HuffmanCode object by reading a previously constructed code from a .code file.
     * Reads the ASCII values and Huffman codes from the input and constructs the Huffman 
     * tree accordingly.
     *
     * @param input - a Scanner object representing the input .code file
     */
     public HuffmanCode(Scanner input) {
         this.codeTree = new HuffmanNode();
 
         while (input.hasNextLine()) {
             int asciiValue = Integer.parseInt(input.nextLine());
             String huffmanCode = input.nextLine();
             char character = (char) asciiValue;
 
             // x = change(x) :)
             this.codeTree = HuffmanCodeHelper(this.codeTree, character, huffmanCode);
         }
     }
 
     /**
     * Helper method to construct the Huffman tree from ASCII values and Huffman codes.
     * Traverses the Huffman code bit by bit and constructs the corresponding Huffman tree.
     *
     * @param currentNode - the current node in the Huffman tree
     * @param character - the character corresponding to the Huffman code
     * @param huffmanCode - the Huffman code for the character
     * @return - the updated current node after traversing the Huffman code
     */
     private HuffmanNode HuffmanCodeHelper(HuffmanNode currentNode, char character, String huffmanCode) {
         HuffmanNode node = currentNode;
 
         // go through the huff code bit by bit
         for (int i = 0; i < huffmanCode.length(); i++) {
             char bit = huffmanCode.charAt(i);
 
             if (bit == '0') {
                 if (node.left == null) {
                     node.left = new HuffmanNode();
                 }
                 node = node.left;
             } else if (bit == '1') {
                 if (node.right == null) {
                     node.right = new HuffmanNode();
                 }
                 node = node.right;
             }
         }
         node.ch = character;
         return currentNode;
     }
 
     /**
     * Saves the Huffman code to the provided PrintStream.
     *
     * @param output - the PrintStream to write the Huffman code to
     */
     public void save(PrintStream output) {
         // x = change(x) again lets goooo
         this.codeTree = saveHelper(output, this.codeTree, "");
     }
 
     /**
     * Helper method to recursively traverse the Huffman tree and save the ASCII values
     * and Huffman codes to the output stream.
     * Performs a pre-order traversal of the Huffman tree.
     * For each leaf node encountered, it writes the ASCII value and corresponding
     * Huffman code to the output stream.
     *
     * @param output - the PrintStream to write the ASCII values and Huffman codes to
     * @param root - the current node being traversed
     * @param huffCode - the Huffman code accumulated so far for the current node
     * @return - the updated root node after recursive calls
     */
     private HuffmanNode saveHelper(PrintStream output, HuffmanNode root, String huffCode) {
         if (root != null) { //pre-order traversal
             if (root.left == null && root.right == null) {
                 char ch = root.ch;
                 int asciiNum = (int) ch;
                 output.println(asciiNum);
                 output.println(huffCode);
             }
             root.left = saveHelper(output, root.left, huffCode + "0");
             root.right = saveHelper(output, root.right, huffCode + "1");
         }
         return root;
     }
 
      /**
      * Decodes a compressed message using the Huffman code by
      * reading sequences of bits that represent encoded characters
      * and writing out the original decompressed message.
      *
      * @param input - a BitInputStream representing the compressed message
      * @param output - the PrintStream to write the decompressed message to
      */
     public void translate(BitInputStream input, PrintStream output) {
         HuffmanNode currentNode = this.codeTree;
 
         while (input.hasNextBit()) {
             int bit = input.nextBit();
 
             if (bit == 0) {
                 currentNode = currentNode.left;
             } else if (bit == 1) {
                 currentNode = currentNode.right;
             }
 
             if (currentNode.left == null && currentNode.right == null) {
                 // Reached a leaf node, write the character code to the output
                 output.write(currentNode.ch);
                 currentNode = this.codeTree; // Go back to the top of the tree
             }
         }
     }
 
     /**
      * Represents a node in a Huffman tree.
      */
     private static class HuffmanNode implements Comparable<HuffmanNode> {
 
         public HuffmanNode root;
         public char ch;
         public int frequency;
         public HuffmanNode left;
         public HuffmanNode right;
 
         /**
         * Constructs a HuffmanNode with the specified character and frequency.
         * Initializes the left and right child nodes to null.
         *
         * @param ch - the character stored in this node
         * @param frequency - the frequency of the character
         */
         public HuffmanNode(char ch, int frequency) {
             this(ch, frequency, null, null);
         }
 
         /**
         * Constructs a node with no inputs
         */
         public HuffmanNode() {
             this('\u0000', 0, null, null);
         }
 
         /**
         * Constructs a HuffmanNode with the specified character, frequency, and child nodes.
         *
         * @param ch - the character stored in this node
         * @param frequency - the frequency of the character
         * @param right - the right child node
         * @param left - the left child node
         */
         public HuffmanNode(char ch, int frequency, HuffmanNode left, HuffmanNode right) {
             this.ch = ch;
             this.frequency = frequency;
             this.left = left;
             this.right = right;
         }
 
         /**
         * Compares this HuffmanNode with another HuffmanNode based on their frequencies.
         *
         * @param root- the HuffmanNode to compare with
         * @return - a negative integer if this node has a lower frequency,
         *         zero if both nodes have the same frequency,
         *         a positive integer if this node has a higher frequency
         */
         @Override
         public int compareTo(HuffmanNode root) {
             if (this.frequency < root.frequency) {
                 return -1;
             } else if (this.frequency == root.frequency) {
                 return 0;
             } else {
                 return 1;
             }
         }
     }
 }