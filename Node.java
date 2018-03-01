/**
 * @author Jonathan Chang
 * Node class
 * CS 1501 Spring 18
 * Garrison
 */
public class Node {

        Node sibling;
        Node child;
        char letter;

        //Constructors

        public Node() {}

        public Node(char letter) {
            this(letter, null, null);
        }

        public Node(char letter, Node sibling, Node child) {
            this.letter = letter;
            this.sibling = sibling;
            this.child = child;
        }
    }