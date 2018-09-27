import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final String FILE_LOCATION_PATH = "src\\Categories.txt";
    
    public static void main(String[] args) throws IOException {

        // Initial
        Scanner scanner = new Scanner(System.in);
        boolean filePresent = false;
        int triesLeft = 10;
        int score = 0;
        ArrayList Categories = new ArrayList<String>();
        String header = "=======================================\n~~~~~~~~~~~~~~~~Hangman~~~~~~~~~~~~~~~~\n";
        String footer = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n=======================================\n";
        LinkedHashMap Dictionary = new LinkedHashMap<String, ArrayList<String>>();
        int end = 0;

        // Reads the file and puts it into a list.
        fillListFromFile(FILE_LOCATION_PATH, Categories, filePresent, header, footer);

        // Gets the list from the previous function and sorts it in a Map where the key is a category and the value
        // is an ArrayList with all the words related to that category.
        sortCategoryDataIntoDictionary(Categories, Dictionary);
        
        // Checks if it found the file at FILE_LOCATION_PATH and if not, does not initialize the program.
        if (!filePresent){
            end++;
        }

        // Start of application.
        while (end == 0) {

            // Initialize "UI" with score
            System.out.println(header);
            System.out.println("             Type a digit");
            System.out.println();
            System.out.println("1. Play");
            System.out.println("2. How to play         Score:");
            System.out.println("3. About this game         " + score);
            System.out.println("4. Quit");
            System.out.println(footer);

            // Input
            String regex = "[1234]";
            String input = scanner.nextLine();
            // Check if user has given input
            if (input.length() < 1) {
                // No input so it prints a meesage
                printInputErrorNumber(header, footer);
                sleep(2500);
            }
            // Check if input is not from 1 to 4
            else if (!Character.toString(input.charAt(0)).matches(regex)) {
                // No input so it prints a meesage
                printInputErrorNumber(header, footer);
                sleep(2500);
            } else {
                switch (Character.toString(input.charAt(0))) {
                    case "1": //Play
                        score = game(Dictionary, header, footer, scanner, triesLeft, score);
                        break;
                    case "2": //How to play
                        printHowToPlay(header,footer);
                        while(true){
                            String back = scanner.nextLine();
                            if (back.contains("exit")){
                                break;
                            }
                        }
                        break;
                    case "3": // About this game
                        printAboutGame(header,footer);
                        while(true){
                            String back = scanner.nextLine();
                            if (back.contains("exit")){
                                break;
                            }
                        }
                        break;
                    case "4": // Quit
                        end++;
                        printQuitGame(header,footer,score);
                        break;
                    default:
                        printInputErrorNumber(header, footer);
                        sleep(2500);
                        break;

                }
            }
        }
    }









    public static int game(LinkedHashMap<String, ArrayList<String>> Dictionary, String header, String footer, Scanner scanner, int triesLeft, int score) {
        // Check if Dictionary is filled
        if (Dictionary.size() < 1) {
            System.out.println(header);
            System.out.println("The dictionary is empty.");
            System.out.println("Please add words in Categories.txt");
            System.out.println(footer);
            sleep(2500);
        } else {
            int end = 0;
            while (end == 0) {
                triesLeft = 10;
                // Print all categories
                System.out.println(header);
                for (Object key : Dictionary.keySet()) {
                    System.out.println("*" + key);
                }
                System.out.println();
                System.out.println("Please write the category you wish to play.");
                System.out.println("You have to spell the category correctly.");
                System.out.println("Case insensitive. To go back type 'exit'.");
                System.out.println(footer);

                // Get input
                String categoryInput = scanner.nextLine();
                // Check for empty input
                String categoryInputCapitalised = "";
                if (!categoryInput.equals("")){
                    categoryInputCapitalised = categoryInput.substring(0, 1).toUpperCase()
                        + categoryInput.substring(1).toLowerCase();
                }
                String chosenCategory = null;
                ArrayList categoryWords;
                String randomWord = null;
                String guesses = "";
                StringBuilder randomWordBlank = new StringBuilder(50);

                // Check if input is empty
                if (categoryInput.equals("")){ printInputErrorCategory(header, footer);sleep(2500); continue;}
                // Save chosen Category, its Words, get random word from Words and create blank template to show to user.
                else if (Dictionary.containsKey(categoryInputCapitalised)) {
                    chosenCategory = categoryInputCapitalised;
                    categoryWords = (ArrayList) Dictionary.get(categoryInputCapitalised);
                    randomWord = (String) categoryWords.get(new Random().nextInt(categoryWords.size()));

                    // Fill blank word with underscores, if there is a space, replace with "*" which will be deleted
                    // upon visualization.
                    for (int i = 0; i < randomWord.length(); i++) {
                        //randomWordBlank.setCharAt(i, '_');
                        if (randomWord.charAt(i) == ' '){
                            randomWordBlank.append('*');
                        }
                        else if(Character.toString(randomWord.charAt(i)).matches("[!@#$%^&(){}+-]")){
                            randomWordBlank.append(randomWord.charAt(i));
                        }
                        else{
                            randomWordBlank.append('_');
                        }
                    }

                    // Start of word guess screen
                    while(true){
                        System.out.println(header);
                        System.out.println("Please input a single letter or try and \nguess the whole word/s in one go.");
                        System.out.println("Category: "+chosenCategory);
                        System.out.println();
                        System.out.print(randomWordBlank.toString().replace("", " ").trim().replaceAll("[*]","")+"    ");
                        System.out.println("Guesses : " + guesses);
                        System.out.println("\nTries left : " + triesLeft + "    To go back, type 'exit'.");
                        System.out.println(footer);

                        // Check if there are any tries left, then check if player has guessed the word
                        if (triesLeft == 0){
                            printInputErrorGameover(header,footer,score,randomWord);
                            score = 0;
                            sleep(2500);
                            break;
                        }
                        else if (!randomWordBlank.toString().contains("_")){
                            score++;
                            printGameWin(header,footer,randomWord,score);
                            sleep(3000);
                            break;
                        }

                        String input = scanner.nextLine();

                        // Check if input is "exit", then if input is empty, then compare input to every letter from
                        // the word. If they match -> replace underscore with input. If there are no matches,
                        // reduce tries left.
                        if (input.toUpperCase().equals(randomWord)){
                            score++;
                            printGameWin(header,footer,randomWord,score);
                            sleep(3000);
                            break;
                        }
                        else if (input.equals("exit")){
                            if (triesLeft < 10) {
                                printNoExit(header, footer, triesLeft);
                                sleep(2500);
                                continue;
                            } else {
                                break;
                            }
                        }
                        else if(input.equals("")){
                            printInputErrorGuess(header,footer);
                            sleep(2500);
                        }
                        else{
                            int count = 0;
                            guesses+=input.toUpperCase().charAt(0)+", ";
                            for (int i = 0; i < randomWord.length(); i++) {
                                if(randomWord.charAt(i) == input.toUpperCase().charAt(0)){
                                    randomWordBlank.setCharAt(i,randomWord.charAt(i));
                                    count++;
                                }
                            }
                            if (count == 0){
                                triesLeft--;
                            }
                        }
                    }
                }
                // For exiting the category menu.
                else if (categoryInput.equals("exit")){
                    end++;
                }
                // If player has entered an invalid category or has misspelled 'exit'.
                else {
                    printInputErrorCategory(header, footer);
                    sleep(2500);
                }
            }

        }
        return score;
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void printInputErrorNumber(String header, String footer) {
        System.out.println(header);
        System.out.println("You have to enter a number from 1 to 4.");
        System.out.println("Returning...");
        System.out.println(footer);
    }

    public static void printInputErrorCategory(String header, String footer) {
        System.out.println(header);
        System.out.println("Invalid category. Please check spelling.");
        System.out.println("Returning...");
        System.out.println(footer);
    }
    public static void printInputErrorGuess(String header, String footer) {
        System.out.println(header);
        System.out.println("Invalid guess. Please enter at least one character.");
        System.out.println("Returning...");
        System.out.println(footer);
    }
    public static void printInputErrorGameover(String header, String footer, int score, String word) {
        System.out.println(header);
        System.out.println("0 tries left. It's game over. :(");
        System.out.println("Your word was : "+word);
        System.out.println("Final score : "+score);
        System.out.println("Returning...");
        System.out.println(footer);
    }
    public static void printGameWin(String header, String footer, String word, int score){
        System.out.println(header);
        System.out.println("Congratulations! You won! :)");
        System.out.println("The word was '" + word + "'.");
        System.out.println("Score + 1 (Total: "+score+").");
        System.out.println(footer);
    }
    public static void printQuitGame(String header, String footer, int score){
        System.out.println(header);
        System.out.println("Sorry to see you go!");
        System.out.println("Final score: "+score);
        System.out.println();
        System.out.println("Quitting...");
        System.out.println(footer);
    }
    public static void printHowToPlay(String header, String footer){
        System.out.println(header);
        System.out.println("                  How to play");
        System.out.println();
        System.out.println("* First you start the game by typing '1' in the menu.");
        System.out.println("* Then you choose a category by typing it fully. (ex. Geography)");
        System.out.println("* The game will choose a random word from that category and the game will start.");
        System.out.println("* You will have underscores ('_') that represent the whole word,");
        System.out.println("* each underscore being one letter. You can see what letters");
        System.out.println("* you have already given on the right. But be careful!");
        System.out.println("* Repeating letters still take away from your tries left!");
        System.out.println("* For each guessed word, you get 1 point. You keep increasing");
        System.out.println("* your score by guessing words in succession. You lose");
        System.out.println("* when your tries become 0 and you haven't guessed the word.");
        System.out.println();
        System.out.println("** P.S.: To play, make sure to put words in the 'Category.txt'");
        System.out.println("- Every category has to have an underscore in front of it and");
        System.out.println("- every word for that category should be below it with no underscores.");
        System.out.println("- Each word should be no longer than 50 symbols and on sparate lines.");
        System.out.println("- Example:");
        System.out.println("    _Geography");
        System.out.println("    River");
        System.out.println("    Mountain Ridge");
        System.out.println("    _Mathematics");
        System.out.println("    Square");
        System.out.println();
        System.out.println(" Make sure that every word has a category above it or it won't show up in game.");
        System.out.println(" Have fun!");
        System.out.println();
        System.out.println(" TO GO BACK TYPE 'exit'");
        System.out.println(footer);
    }
    public static void printAboutGame(String header, String footer){
        System.out.println(header);
        System.out.println("          About the game");
        System.out.println("* Name: Hangman 1.0");
        System.out.println("* Made by : Svetozar Blazhev");
        System.out.println("* Studied @ Software University");
        System.out.println("* Made for the 'MentorMate' internship program");
        System.out.println("* ");
        System.out.println("* Time it took: About two days on/off coding");
        System.out.println("*");
        System.out.println("* Functionality:");
        System.out.println("*   -Reads from file");
        System.out.println("*   -Sorts data from file into a dictionary");
        System.out.println("*   -Interactive menu system");
        System.out.println("*   -Many failsafe checks");
        System.out.println("*      -Ex. empty inputs, words with no category,");
        System.out.println("*      -Ex. letter capitalization, longer input than needed,");
        System.out.println("*      -Ex. categories with no words, no need to 'guess'");
        System.out.println("*      -Ex. symbols in words such as '&' or '-' and others.");
        System.out.println("*   -Functionality made in functions for easy readability");
        System.out.println("*   -Error messages with timers for specific cases");
        System.out.println("*      -(not with error handling, sadly)");
        System.out.println("*   -Keeps track of score");
        System.out.println("*   -And more ontop of the core hangman functionality.");
        System.out.println("*");
        System.out.println("* TO GO BACK TYPE 'exit'");
        System.out.println(footer);
    }
    public static void printNoExit(String header, String footer, int triesLeft) {
        System.out.println(header);
        System.out.println("You can't go back if you've already tried guessing.");
        System.out.println("Current tries :" + triesLeft);
        System.out.println(footer);
    }

    public static void sortCategoryDataIntoDictionary(ArrayList Categories, LinkedHashMap Dictionary) {
        if (Categories.size() > 0) {

            int counter = 0;
            String key = null;
            ArrayList value = new ArrayList<String>();

            // Iteration of the file's lines
            for (Object category : Categories) {

                // Check if line is empty
                if (category == null || category.equals("")) {
                    continue;
                }

                String placeholder = category.toString().trim();
                String placeholderFirst = Character.toString(placeholder.charAt(0));
                if (Character.toString(placeholder.charAt(0)).equals("")){
                    placeholderFirst = Character.toString(placeholder.charAt(1));
                }

                // Check if line from file (word/s) has a "_" (is a category) and if it is the first one to occur.
                // If yes, save temporarily to "key" variable and increase the counter to notify that the
                // first category has been found.
                if (placeholderFirst.equals("_") && counter == 0) {
                    counter++;
                    key = placeholder.substring(1);
                }
                // Check if line from file (word/s) hsa a "_" (is a category) and if it is not the first one to occur,
                // push anything that has already been saved as "key" and "value" variables to the final Dictionary
                // and then replace the key with the newly found category. Also, creates new ArrayList for the "value"
                // that is empty.
                else if (placeholderFirst.equals("_") && counter != 0) {

                    // Check if there are any words for the previous category.
                    // Skips adding the category if it has no words.
                    if (value.size() != 0) {
                        String capitalizedKey = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
                        Dictionary.put(capitalizedKey, value);
                        key = placeholder.substring(1);
                        value = new ArrayList<String>();
                    } else {
                        key = placeholder.substring(1);
                    }
                }
                // Adds this line from the file to the "values" list as a word for a specific category
                // in the Dictionary.
                else {
                    // Checking if there has been and category found at all. If not, skips.
                    if (counter == 0) {
                        continue;
                    }
                    value.add(placeholder.toUpperCase());
                }
            }

            // Pushes everything left from the "for" iterator to the Dictionary.
            // Checks if a category has been found and any words for the category before adding.
            // Nullifies key and value.
            if (value.size() > 0 && key != null) {
                String capitalizedKey = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
                Dictionary.put(capitalizedKey, value);
                key = null;
                value = null;
            }
        }
    }

    public static void fillListFromFile(String path, ArrayList list, Boolean filePresent, String header, String footer) throws IOException {
        File categories = new File(path);
        BufferedReader br;
        try{
           br  = new BufferedReader(new FileReader(categories));
           filePresent = true;

        }
        catch (FileNotFoundException e){
            System.out.println(header);
            System.out.println("Couldn't find file to read.");
            System.out.println("Please create it @ GameDirectory\\" + FILE_LOCATION_PATH);
            System.out.println();
            System.out.println("Quitting...");
            System.out.println(footer);
            return;
        }
        String st;
        int count = 0;
        while ((st = br.readLine()) != null) {
            list.add(count,st);
            count++;
        }
    }
}
