package flashcards;

import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    static Logger logger = new Logger();
    static final Set<Flashcard> flashcards = new HashSet<>();
    static String exportFile = "";

    public static void main(String[] args) {
        importOrExportFromFileCommandLineArgs(args);
        startFlashcardsApp();
    }

    private static void startFlashcardsApp() {

        while (true) {
            logger.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String action = logger.nextLine().trim();
            Action enumAction;
            try {
                enumAction = Action.getAction(action);
            } catch (IllegalArgumentException e) {
                logger.println("There is no such action, try again.");
                continue;
            }

            switch (enumAction) {
                case ADD:
                    createFlashCards();
                    break;
                case REMOVE:
                    removeCard();
                    break;
                case IMPORT:
                    interactiveImport();
                    break;
                case EXPORT:
                    interactiveExport();
                    break;
                case ASK:
                    testKnowledge();
                    break;
                case EXIT:
                    logger.println("Bye bye!");
                    exportCardsToFile(exportFile);
                    return;
                case CARDS:
                    flashcards.forEach(System.out::println);
                    break;
                case LOG:
                    saveLogsToFile(logger.getLogs());
                    break;
                case HARDEST_CARD:
                    printHardestCards();
                    break;
                case RESET_STATS:
                    resetStats();
                    break;
            }

        }
    }

    private static void importOrExportFromFileCommandLineArgs(String[] args) {
        for (int i = 1; i < args.length; i += 2) {
            switch (args[i - 1]) {
                case "-import":
                    importCardsFromFile(args[i]);
                    break;
                case "-export":
                    exportFile = args[i];
                    exportCardsToFile(exportFile);
            }
        }
    }


    private static int getInputAsNumber(String message) {
        int numberInput;
        while (true) {
            logger.println(message);
            try {
                numberInput = Integer.parseInt(logger.nextLine());
                break;
            } catch (NumberFormatException e) {
                logger.println("Your input is not a number. Enter valid number.");
            }
        }
        return numberInput;
    }

    private static Set<Flashcard> createFlashCards() {
        //int numberOfCards = getInputAsNumber("Input the number of cards:");
        int numberOfCards = 1;
        for (int i = 0; i < numberOfCards; i++) {
            try {
                Flashcard card = createUniqueFlashcard(i + 1);
                flashcards.add(card);
            } catch (RuntimeException e) {
                // todo some functionality
            }
        }
        return flashcards;
    }

    public static Flashcard createUniqueFlashcard(int cardNumber) {

        String term;
        String definition;
        int countOfMistakes = 0;
        Map<String, String> flashcardMap = transformToFlashcardsMap(flashcards);

        logger.println("The card:");
        term = logger.nextLine();
        if (flashcardMap.containsKey(noSpaceAndLowerCase(term))) {
            logger.printf("The card \"%s\" already exists.\n", term);
            throw new RuntimeException();
        }
        //System.out.printf("Card #%d:\n", cardNumber);
/*        while (true) {
            term = scanner.nextLine();
            if (flashcardMap.containsKey(noSpaceAndLowerCase(term))) {
                System.out.printf("The term \"%s\" already exists. Try again:\n", term);
                continue;
            }
            break;
        }*/

        logger.println("The definition of the card:");
        definition = logger.nextLine();
        if (flashcardMap.containsValue(noSpaceAndLowerCase(definition))) {
            logger.printf("The definition \"%s\" already exists.\n", definition);
            throw new RuntimeException();
        }
        //System.out.printf("The definition of the card #%d: \n", cardNumber);
/*        while (true) {
            definition = scanner.nextLine();
            if (flashcardMap.containsValue(noSpaceAndLowerCase(definition))) {
                System.out.printf("The definition \"%s\" already exists. Try again:\n", definition);
                continue;
            }
            break;
        }*/

        logger.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
        return new Flashcard(term, definition, countOfMistakes);
    }

    private static void removeCard() {
        logger.println("Which card?");
        String termToRemove = logger.nextLine();
        String termToRemoveLowercase = noSpaceAndLowerCase(termToRemove);

        for (Flashcard flashcard : flashcards) {
            String currentFlashcard = noSpaceAndLowerCase(flashcard.getTerm());
            if (currentFlashcard.equals(termToRemoveLowercase)) {
                flashcards.remove(flashcard);
                logger.println("The card has been removed.");
                return;
            }
        }
        logger.printf("Can't remove \"%s\": there is no such card.\n", termToRemove);
    }

    private static void interactiveImport() {
        logger.println("File name:");
        String fileName = logger.nextLine();
        importCardsFromFile(fileName);
    }

    private static void importCardsFromFile(String filename) {
        Set<Flashcard> flashcardsFormFile = readFlashcardsFromFile(filename);

        int importedCardsNumber = flashcardsFormFile.size();
        if (importedCardsNumber == 0) {
            logger.println("No cards has been loaded.");
            return;
        }

        flashcardsFormFile.addAll(flashcards);
        flashcards.clear();
        flashcards.addAll(flashcardsFormFile);
        logger.printf("%d cards have been loaded.\n", importedCardsNumber);
    }


    @SuppressWarnings("unchecked")
    private static Set<Flashcard> readFlashcardsFromFile(String fileName) {

        try (FileInputStream fileInput = new FileInputStream(fileName);
             ObjectInputStream objIn = new ObjectInputStream(fileInput)) {
            return new HashSet<>((HashSet<Flashcard>) objIn.readObject());
        } catch (FileNotFoundException e) {
            logger.println("File not found.");
            return new HashSet<>();
        } catch (ClassNotFoundException | IOException e) {
            return new HashSet<>();
        }
    }

    private static void interactiveExport() {
        logger.println("File name:");
        String fileName = logger.nextLine().trim();
        exportCardsToFile(fileName);
    }

    private static void exportCardsToFile(String filename) {

        int cardsNumber = flashcards.size();
        if(cardsNumber == 0){
            logger.println("No cards has been saved.");
            return;
        }

        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {
            Set<Flashcard> flashcardsFromFile = readFlashcardsFromFile(filename);
                flashcardsFromFile.addAll(flashcards);
                flashcards.clear();
                flashcards.addAll(flashcardsFromFile);
                objOut.writeObject(flashcards);
                logger.printf("%d cards have been saved.\n", cardsNumber);

        } catch (FileNotFoundException e) {
            logger.println("File not found.");
        } catch (IOException e) {
            logger.println(e.getClass().getName());
        }
    }

    public static Map<String, String> transformToFlashcardsMap(Set<Flashcard> flashcards) {
        Map<String, String> flashcardMap = new HashMap<>();

        flashcards.forEach(fc -> flashcardMap.put(
                noSpaceAndLowerCase(fc.getTerm()),
                noSpaceAndLowerCase(fc.getDefinition())
        ));

        return flashcardMap;
    }

    private static String noSpaceAndLowerCase(String text) {
        return text
                .toLowerCase()
                .replaceAll("\\s+", "");
    }


    private static void testKnowledge() {

        int cardsToGuessNumber = getInputAsNumber("How many times to ask?");

        int showedCardsCount = 0;
        for (Flashcard flashcard : flashcards) {
            if (showedCardsCount == cardsToGuessNumber) {
                return;
            }
            String term = flashcard.getTerm();
            String definition = flashcard.getDefinition();

            logger.printf("Print the definition of \"%s\":\n", term);
            String userDefinitionAnswer = logger.nextLine();

            if (compareAnswer(definition, userDefinitionAnswer)) {
                logger.println("Correct!");
            } else {
                Map<String, String> flashcardMap = transformToFlashcardsMap(flashcards);
                String userDefinitionNoSpaces = noSpaceAndLowerCase(userDefinitionAnswer);
                if (flashcardMap.containsValue(userDefinitionNoSpaces)) {
                    String matchingTerm = getTermsForDefinition(flashcardMap, userDefinitionNoSpaces);
                    logger.printf("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".\n", definition, matchingTerm);
                } else {
                    logger.printf("Wrong. The right answer is \"%s\".\n", definition);
                }
                int mistakesNumber = flashcard.getCountOfMistakes();
                flashcard.setCountOfMistakes(++mistakesNumber);
            }
            showedCardsCount++;
        }
    }

    private static String getTermsForDefinition(Map<String, String> map, String value) {
        Optional<String> key = map.entrySet().stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
        return key.orElse(null);
    }

    private static boolean compareAnswer(String definition, String answer) {
        String trimLowerAnswer = noSpaceAndLowerCase(answer);
        String trimLowerDef = noSpaceAndLowerCase(definition);

        return trimLowerAnswer.equals(trimLowerDef);
    }

    private static void printHardestCards() {
        String message = "There are no cards with errors.";

        if (flashcards.size() == 0) {
            logger.println(message);
            return;
        }

        Optional<Flashcard> hardestFlashcardOptional = flashcards.stream()
                .filter(i -> i.getCountOfMistakes() > 0)
                .max(Comparator.comparing(Flashcard::getCountOfMistakes));

        if (hardestFlashcardOptional.isEmpty()) {
            logger.println(message);
            return;
        }

        Flashcard hardestFlashcard = hardestFlashcardOptional.get();

        Set<Flashcard> hardestFlashcards = flashcards.stream()
                .filter(i -> i.getCountOfMistakes() == hardestFlashcard.getCountOfMistakes())
                .collect(Collectors.toSet());

        if (hardestFlashcards.size() == 1) {
            logger.printf("The hardest card is \"%s\". You have %d errors answering it.\n", hardestFlashcard.getTerm(), hardestFlashcard.getCountOfMistakes());
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("The hardest cards are: ");
        int itemIndex = 0;
        for (Flashcard f : hardestFlashcards) {
            if (itemIndex == hardestFlashcards.size() - 1) {
                sb.append(String.format("\"%s\". ", f.getTerm()));
            } else {
                sb.append(String.format("\"%s\", ", f.getTerm()));
                itemIndex++;
            }
        }
        sb.append(String.format("You have %d errors answering them", hardestFlashcards.size()));
        logger.println(sb.toString());
    }

    private static void resetStats() {
        flashcards.forEach(i -> i.setCountOfMistakes(0));
        logger.println("Card statistics have been reset.");
    }


    private static void saveLogsToFile(List<String> currentLogs) {
        logger.println("File name:");
        String fileName = logger.nextLine().trim();
        File file = new File(fileName);

        try (FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fileWriter)) {
            for (String logLine : currentLogs) {
                bw.write(logLine);
            }
            String message = "The log has been saved.";
            logger.println(message);
            bw.write(String.format("%s - %s", Logger.getTime(), message));
        } catch (IOException e) {
            e.printStackTrace();
            String error = "Exception, Logs were not saved.";
            logger.println(error);
        }

/*        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {
            objOut.writeObject(currentLogs);
            logger.println("The log has been saved.");
        } catch (FileNotFoundException e) {
            logger.println("File not found.");
        } catch (IOException e) {
            logger.println(e.getClass().getName());
        }*/
    }
}

class Flashcard implements Serializable {
    private final String term;
    private String definition;
    private int countOfMistakes;

    public Flashcard(String term, String definition, int countOfMistakes) {
        this.term = term;
        this.definition = definition;
        this.countOfMistakes = countOfMistakes;
    }

    public String getTerm() {
        return this.term;
    }

    public String getDefinition() {
        return this.definition;
    }

    public int getCountOfMistakes() {
        return this.countOfMistakes;
    }

    public void setCountOfMistakes(int countOfMistakes) {
        this.countOfMistakes = countOfMistakes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flashcard flashcard = (Flashcard) o;
        return term.equals(flashcard.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term);
    }

    @Override
    public String toString() {
        return "Flashcard{" +
                "term='" + term + '\'' +
                ", definition='" + definition + '\'' +
                ", countOfMistakes=" + countOfMistakes +
                '}';
    }
}

enum Action {
    ADD("add"),
    REMOVE("remove"),
    IMPORT("import"),
    EXPORT("export"),
    ASK("ask"),
    EXIT("exit"),
    CARDS("cards"),
    LOG("log"),
    HARDEST_CARD("hardest card"),
    RESET_STATS("reset stats");


    private final String label;
    private static final Map<String, Action> LABEL_ACTION = new HashMap<>();

    static {
        for (Action a : Action.values()) {
            LABEL_ACTION.put(a.label, a);
        }
    }

    Action(String label) {
        this.label = label;
    }

    public static Action getAction(String label) {
        if (!LABEL_ACTION.containsKey(label)) {
            throw new IllegalArgumentException();
        }

        return LABEL_ACTION.get(label);
    }
}

class Logger {

    private final List<String> logs = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    protected List<String> getLogs() {
        return logs;
    }

    protected void println(String message) {
        String logMessage = String.format("%s - %s\n", getTime(), message);
        this.logs.add(logMessage);
        System.out.println(message);
    }

    protected void printf(String format, Object... parameters) {
        String logMessage = String.format("%s - %s\n", getTime(), format);
        logs.add(logMessage);
        System.out.printf(format, parameters);
    }

    protected static String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant instant = Instant.now();
        return formatter.format(instant);
    }

    protected String nextLine() {
        String input = scanner.nextLine();
        logs.add(String.format("%s - %s\n", getTime(), input));
        return input;
    }


}
