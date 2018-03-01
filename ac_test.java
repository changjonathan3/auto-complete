/**
 * @author Jonathan Chang
 * ac_test
 * CS 1501 Spring 18
 * Garrison
 */

import java.io.*;
import java.util.*;
import java.util.ArrayList;

public class ac_test  {

    public static void main(String[] args) throws IOException {
        dlb d = new dlb();
        //put dictionary test file into dlb trie
        BufferedReader readerOne = new BufferedReader(new FileReader("dictionary.txt"));
        String line;
        while (( line = readerOne.readLine()) != null) {
            d.insert(line);
        }
        readerOne.close();

        ArrayList<String> h = new ArrayList(); // store all user saved words
        //this uploads stored words in past user history to the updating history list of the program
        try{
            BufferedReader readerTwo = new BufferedReader(new FileReader("user_history.txt"));
            String lines;
            while ((lines = readerTwo.readLine()) != null) {
                h.add(lines); //updating list in program
            }
            readerTwo.close();
        }
        catch(FileNotFoundException e){}


        //begin reading user input characters
        Scanner s = new Scanner(System.in);
        StringBuilder user = new StringBuilder(); //this is the main tracker of what the user enters
        FileWriter writer = new FileWriter("user_history.txt", true);
        ArrayList<String> words = new ArrayList(); //save predicted words depending on user search each round

        double totalTime = 0; //use these two to calculate average search time at end of program
        int trials = 0;

        System.out.println("Enter your first character: ");
        char in = s.next().charAt(0); //in is always the character the user inputs next

        while(in != '!'){ //exit if desired

            if(in == '$'){ //just store word if desired
                System.out.println("Word Saved! " + user.toString());
                writer.write(user.toString() + "\n");
                h.add(user.toString());
                words.clear();
                user.setLength(0);
                System.out.println("Enter first character of next word: ");
                in = s.next().charAt(0);
            }

            else{ //otherwise, we need to search for the word
                user.append(in); //update our main search string
                int valid = d.search(user.toString());

                if(valid != 0){ //this means there is a potential word found
                    Node sentinel = d.base; //this will be the "root" or first char in each possible word
                    System.out.println("Predicting: ");
                    long start = System.nanoTime();
                    StringBuilder output = new StringBuilder(predictor (d, sentinel, user, h)); // predicting done
                    System.out.println(output.toString());
                    long time = System.nanoTime() - start;
                    double seconds = (double)time / 1000000000.0; //timing this search round
                    System.out.println("(" + seconds + " s)");

                    totalTime += seconds; //updates aggregate time data
                    trials++;

                    //split main output line into substrings of words (at most 5)
                    //either take substring up to a space, or end of the entire output line (if last word)
                    boolean done = false;
                    int f = 0; //index of last char in substring
                    output.setLength(output.length()-1);
                    for(int i = 0; i<5 && done == false; i++){
                        String temp = output.substring(0);
                        if(!temp.contains(" ")){
                            done = true;
                            f=output.length();
                        }
                        if(!done){
                            f = temp.indexOf(" ");
                        }
                        temp = output.substring(2, f); //avoids adding the number, only the word itself
                        words.add(temp);
                        if(!done){ //only continue if we didn't reach the end of output line yet
                            temp = output.substring(f+1);
                            output = new StringBuilder(temp);
                        }
                    }

                    //searching for next char
                    System.out.println("Enter the next character: ");
                    in = s.next().charAt(0);
                    if(in != '!' && in !='$'){ //exit values
                        int a=Character.getNumericValue(in);
                        if(a == 1 || a==2 || a==3 || a==4 || a==5){ //if user selects a prediction, store it
                            System.out.println("Word Complete! " + words.get(a-1));
                            writer.write(words.get(a-1) +"\n");
                            h.add(words.get(a-1));
                            words.clear();
                            user.setLength(0);

                            //begin next search
                            System.out.println("Enter first character of next word: ");
                            in = s.next().charAt(0);
                        }
                        else{ // no predictions selected, remove them
                            words.clear();
                        }
                    }
                }
                else{ //search was not found
                    System.out.println("No predictions! Enter next character: ");
                    in = s.next().charAt(0);
                }
            }
        }// end while searching loop

        double avg = (double) totalTime / trials; //gets our total average time and exits program
        System.out.println("Average Time: " + avg);
        System.out.println("Bye!");
        writer.close();
    }

    public static String predictor (dlb d, Node sent, StringBuilder sb, ArrayList h) {

        StringBuilder printer = new StringBuilder(); //will print word predictions generated
        int save = sb.length(); //saves the original searching length so we can save it in main class
        ArrayList<Node> pre = new ArrayList(); //constantly updating with potential nodes to branch words from
        ArrayList<String> oldies = new ArrayList(); //update with multiple old words selected in the past

        boolean old = false; //whether or not we can offer an old word as a prediction
        boolean match = false; //whether match is found
        int countOld = 1;

        for(int i = 0; i< h.size(); i++){ //iterate through our history list
            StringBuilder temp = new StringBuilder(); //current word in history
            StringBuilder comp = new StringBuilder(); // current word being searched

            String t = String.valueOf(h.get(i)); //convert both into strings
            String c = sb.toString();

            temp.append(t);
            comp.append(c);

            while(!match && temp.charAt(0) == comp.charAt(0)){ //check for word
                temp.deleteCharAt(0);
                comp.deleteCharAt(0);
                if(comp.length() == 0 || temp.length() ==0)
                    match = true;
            }

            if(match){ //this means that words matched, we can add this as prediction
                old = true;
                match=false; //reset
                oldies.add(t);
                printer.append(countOld + ")" + t + " "); //word printed!
                countOld++;
            }
        }

        int check = 0; //gives dlb search class values (0,1,2,3)
        boolean skip = false; //skip over a column of nodes if needed

        for(int i=1;i<6;i++){ //repeat for maximum of 5 potential predictions

            if(sent.child.letter == '$'){ //one letter word
                if(i != 1){
                    sb.append(sent.letter);
                }
                check = d.search(sb.toString()); //searches the dlb trie
                if(check ==3 || check ==1){ //has branching so is prefix, save it
                    pre.add(0, sent);
                }
                sent = sent.child; // move sentinel down to '$'
            }

            else{ //need to scan
                //remove extra letter for first prediction
                if(i==1){
                    sb.setLength(sb.length()-1);
                }
                boolean invalid = false; //in case we need to break (no word)
                while(sent.letter != '$'){ //scan down until word found
                    sb.append(sent.letter);
                    check = d.search(sb.toString()); //search each time for word

                    if(check == 0){
                        invalid = true;
                        break; //end predictions, no word
                    }

                    if(check ==3 || check ==1){ //has branching so is prefix, save it
                        pre.add(0, sent);
                    }
                    sent = sent.child; //continues down chain
                }
                if(invalid) break;
            }

            if(i==1 && old){
                i = i + countOld -1; //bypass rounds, however many old words we used
            }

            boolean repeat = false; //check if old word matches a prediction
            while(!oldies.isEmpty())
            {
                if(oldies.get(0).equals(sb.toString())){ //if match, don't count this prediction
                repeat = true;
                i--;
                }
                oldies.remove(0);
            }

            if(!repeat){
                printer.append(i + ")" + sb.toString() + " "); //word printed!
            }

            if(check != 2){ //we can just keep going forwards
                sent = sent.sibling; //moves over to next column
            }

            if(check == 2 && i != 5){ //at the end of word, backtrack for a prefix
                if(pre.isEmpty()){ //no more predictions left, so end
                    break;
                }
                sent = pre.get(0); //use an old potential node to branch from
                sb.setLength(sb.length()-1); //delete last letter since it can't be used anymore
                if(sent.child.sibling != null && !skip){
                    sent = sent.child.sibling; //pass into next column since we have another possible word
                }

                else{ //used up the current branch possibilities
                    pre.remove(0); //pop off most recent possible node
                    sb.setLength(sb.length()-1); //remove unusable letter

                    if(pre.isEmpty()){ //no more predictions left, so end
                        break;
                    }
                    if(sent.sibling != null && pre.get(0).child.sibling.sibling != null){ //backup by just one
                        sent = pre.get(0).child.sibling.sibling;
                    }
                    else{ //backup more than just one possible prefix
                        pre.remove(0); //pop off node
                        sb.setLength(sb.length()-1); //remove unusable letter

                        if(pre.isEmpty()){ //no more predictions left, so end
                            break;
                        }
                        if(skip){ //avoid reusing a node we already fully used up
                            pre.remove(0); //pop again
                            sb.setLength(sb.length()-1); //remove unusable letter

                            if(pre.isEmpty()){ //no more predictions left, so end
                                break;
                            }
                        }
                        if(sb.charAt(sb.length()-1) == pre.get(0).letter)
                            sb.setLength(sb.length()-1); //remove unusable letter

                        while(pre.get(0).sibling == null && !pre.isEmpty()){ //cycle backwards to old node
                            pre.remove(0);
                            sb.setLength(sb.length()-1);
                        }

                        if(pre.isEmpty() || pre.get(0).sibling == null){ //no more words
                            break;
                        }
                        sent = pre.get(0).sibling; //this is the next node that we can traverse down on
                    }
                    skip = true; //remember to bypass on next round
                }
            }
        }
        sb.setLength(save); //reset to original user search string
        return printer.toString(); //final output printed
    }
}