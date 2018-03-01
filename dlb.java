/**
 * @author Jonathan Chang
 * dlb Class
 * CS 1501 Spring 18
 * Garrison
 */

public class dlb  {

    private final char last = '$'; //set to mark end of word
    private Node root; //top node
    public Node base; //base of user entered word

    //Constructor
    public dlb () {
        root = new Node();
    }

    /**
     *  INSERT METHOD
     *
     * 1) Make sure key is not already in the trie.
     *
     * 2) Add nodes as needed, only after prefix of word
     */

    public boolean insert (String word) {

        // For successful adds
        boolean check = false;

        // Tack on '$' to end of word
        word += last;

        Node curr = root;

        // Add char by char to new nodes by order in word
        for (int i = 0; i < word.length(); i++) {

            char letter = word.charAt(i);
            AddNode set = setChild(curr, letter);
            curr = set.node;
            check = set.checkAdd; //only add if node didn't exist before
        }

        return check;
    }

    /**
     * setSibling (used in INSERT)
     * Adds a char in a new node on the same level
     * (to the right of curr node), only if needed
     */

    private AddNode setSibling(Node sibling, char letter) {

        if (sibling == null) {

            sibling = new Node(letter);
            return new AddNode(sibling, true);
        }

        else {

            Node next = sibling;

            // Keep checking for letter desired
            while (next.sibling != null) {

                if (next.letter == letter) {
                    break;
                }
                next = next.sibling;
            }

            if (next.letter == letter) {
                // No need to add new node
                return new AddNode(next, false);
            }

            else {
                // Need to make new node with the char value, add to end of row
                next.sibling = new Node(letter);
                return new AddNode(next.sibling, true);
            }
        }
    }

    /**
     * setChild (used in INSERT)
     * Adds a char in new node on a new level
     * (down relative to curr node), only if needed
     */

    private AddNode setChild(Node parent, char letter) {

        //Look for any existing child nodes
        if (parent.child == null) {

            // Add a child if no child nodes
            parent.child = new Node(letter);
            return new AddNode (parent.child, true);
        }

        else {
            // Or, Add amongst existing child nodes as a sibling
            return setSibling(parent.child, letter);
        }
    }

    //used in INSERT
    //only add new node if needed (prevents duplicates)
    private class AddNode {

        Node node;
        boolean checkAdd;

        //Constructor

        public AddNode(Node node, boolean checkAdd) {
            this.node = node;
            this.checkAdd = checkAdd;
        }
    }

    /**
     *  SEARCH METHOD
     *
     * 1) At each level, follow right among siblings until char matches, then go to next level.
     *      If null is reached, it is not found.
     *
     *  2) Proceed until the end of string char '$' is reached.
     */

    public int search(String word) {

        Node curr = root;

        for (int i = 0; i < word.length(); i++) {

            char letter = word.charAt(i);
            curr = getChild(curr, letter);

            if (curr == null) {
                return 0;
            }
            //Proceed checking
        }

        //Curr node is @ last char in word, did not reach '$' yet
        Node end = getChild(curr, last);
        base=curr;
        if (end == null)
        {
            //is strictly a prefix
            return 1;
        }
        else if (end.sibling == null)
        {
            //is strictly a word
            return 2;
        }
        else
        {
            // both prefix and a word
            return 3;
        }
    }

    /**
     * getSibling (used in SEARCH)
     * Looking for desired char value amongst sibling nodes
     * (to the right) of curr node
     *
     */

    public Node getSibling(Node sibling, char letter) {

        Node next = sibling;

        while (next != null) {
            if (next.letter == letter) {
                break;
            }
            next = next.sibling;
        }

        return next;
    }

    /**
     * getChild (used in SEARCH)
     * Looking for desired char amongst child nodes
     * (down relative to curr node)
     */

    public Node getChild (Node parent, char letter) {

        return getSibling(parent.child, letter);
    }

}
