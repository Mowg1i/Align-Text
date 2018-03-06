import java.util.Arrays;

/**
* Practical 1 - AlignText.
* @author Jessica Cooper (jmc31@st-andrews.ac.uk)
* @version 1
* @since 1
*/

public class AlignText {

    /**
    * The maximum valid number of arguments.
    */
    private static final int MAXARGS = 3;

    /**
    * Takes a String array of arguments and checks if they are valid.
    * arg[0] must be a string of length greater than 0
    * arg[1] must be an integer greater than 0
    * arg[2] is optional, but if it exists it must be a char, either 'L', 'C' or 'J'.
    * @param args String array of command line arguments provided by user
    * @return true if the args are valid, false otherwise
    */
    private static boolean checkArgs(String[] args) {

       if (args.length < 2 || args.length > MAXARGS) {
          return false;
       }

       // try/catch is to handle if parseInt fails if arg[1] is not a number. (And
       // therefore also not a valid argument.
       try {
          if (Integer.parseInt(args[1]) < 0) {
             return false;
          }
       } catch (NumberFormatException e) {
          return false;
       }

       if (args.length == MAXARGS) {
          char option = args[2].charAt(0);
          if (option != 'L' && option != 'C' && option != 'J') {
             return false;
          }
       }
       return true;
    }


    /**
    * Takes a string array of words and a character limit, and builds an array of lines containing
    * the maximum possible words per line within the character limit without splitting words.
    * @param words An array of words
    * @param lineLength The character limit per line
    * @return A String array of lines of length lineLength or less
    */
    private static String[] wordsToLines(String[] words, int lineLength) {


       /** 
       * String array lines is of length of wordcount, because the max possible number
       * of lines is if there's one word per line.
       */
       String[] lines = new String[words.length];

       // initialising all lines to empty string, otherwise we get null problems
       for (int i = 0; i < lines.length; i++) {
          lines[i] = "";
       }

       /** 
       * A counter for the word being added to the line
       */
       int w = 0;

       /** 
       * A counter for the line being built
       */
       int l = 0;


       while (w < words.length) {

          /** 
          * The length that the line will be if the current word is added to it.
          */
          int potentialLineLength = lines[l].length() + words[w].length();

          // check if joining word to line is allowed within char limit, or if line is empty.
          // if either, add word. If not, go to next line.
          if (lines[l].length() == 0) {
             lines[l] = words[w];
          } else if (potentialLineLength < lineLength) {
             lines[l] += " " + words[w];
          } else {
             l++;
             lines[l] = words[w];
          }
          w++;
       }

       // trim array now we know how long it needs to be
       return Arrays.copyOf(lines, l + 1);
    }

    /**
    * Takes an array of lines, the goal line length and an alignment option, returns an aligned
    * string by adding spaces to each line and then concatenating them.
    * @param lines An array of lines of length less than or equal to lineLength
    * @param lineLength The character limit per line
    * @param option The alignment option - a char that could be:
    * 'L' for left align
    * 'C' for center
    * 'J' for justify
    * 'R' the default, which is right align
    * @return A string aligned as per option provided
    */
    private static String aligner(String[] lines, int lineLength, char option) {


       /** 
       * Empty string to hold aligned text
       */
       String paragraphOut = "";

       // for each line
       for (int l = 0; l < lines.length; l++) {

            /** 
            * For readability, currentLine is, unsuprisingly, the current line in loop.
           */
          String currentLine = lines[l];

          /** 
            * Spaces are added to this string until the number of spaces plus the length
            * of the current line equals the desired line length
           */
          String spaces = "";
          while (currentLine.length() + spaces.length() < lineLength) {
             spaces += " ";
          }

          // Pretty self-explanatory below, the spaces saved in the variable above are
          // added to the line in various positions depending on the option provided

          if (option == 'R') {
             paragraphOut += spaces + currentLine + "\n";
          } else if (option == 'L') {
             paragraphOut += currentLine + spaces + "\n";
          } else if (option == 'C') {
             String lead = spaces.substring(0, spaces.length() / 2);

             // if the number of spaces needed is not even, the extra space is added to the
             // start of the line
             if (spaces.length() % 2 != 0) {
                lead += " ";
             }
             paragraphOut += lead + currentLine + spaces.substring(spaces.length() / 2) + "\n";

          } else if (option == 'J') {

             // if we're at the very last line, then left align it.
             if (l == lines.length - 1) {
                currentLine = currentLine + spaces;
             }

             // If the currentline does not contain any spaces and it's less than the desired
             // line length, just add one space to the end
             if (!currentLine.contains(" ") && currentLine.length() < lineLength) {
                currentLine += " ";
             }

             // Working backwards from the end of the string, looking for spaces. If one is found,
             // another space is added beside it. This loop continues until the length of the
             // string equals  the desired line length

             for (int j = currentLine.length(); currentLine.length() < lineLength; j--) {
                if (j == 0) {
                    j = currentLine.length();
                }
                if (currentLine.substring(j - 1, j).equals(" ")) {
                    currentLine = currentLine.substring(0, j) + " " + currentLine.substring(j);
                    j--;
                }
             }
             // the correctly aligned line is added to the paragraph
             paragraphOut += currentLine + "\n";
          }
       }

       // and we're done :D
       return paragraphOut;
    }

    /**
    * Takes a String array of paragraphs, wraps the text to the line length given and either
    * justifies, centers, left or right aligns it.
    * @param args String array of command line arguments provided by user
    * arg[0] must be a string of length greater than 0
    * arg[1] must be an integer greater than 0
    * arg[2] is optional, but if it exists it must be a char, either 'L', 'C' or 'J', for
    * left aligned, centered or justified. If left blank the text will be right aligned.
    * @Return A paragraph word wrapped at the given line length and aligned as specified.
    */
    public static void main(String[] args) {

       if (!checkArgs(args)) {
          System.out.println("usage: java AlignText file_name line_length <align_mode>");
          return;
       }

       /** 
       * The array of paragraphs, obtained using FileUtil
       * https://studres.cs.st-andrews.ac.uk/CS5001/Practicals/p1-aligntext/Resources/FileUtil.java
       */
       String[] paragraphsIn = FileUtil.readFile(args[0]);

       /** 
       * The maxiumum line length / character limit for each line
       */
       int lineLength = Integer.parseInt(args[1]);

       /** 
       * The default alignment option is 'R', for right align
       */
       char option = 'R';
       if (args.length == MAXARGS) {
          option = args[2].charAt(0);
       }

       // for each paragraph
       for (int p = 0; p < paragraphsIn.length; p++) {

          // split the paragraphs into lines of given length
          String[] lines = wordsToLines(paragraphsIn[p].split(" "), lineLength);

          // align the lines by adding spaces depending on the option given, and print.
          System.out.print(aligner(lines, lineLength, option));

       }
    }
}
