package flashcards;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlashcardsController {

    private FlashcardStorage flashcardStorage;
    private Logger logger;

    public FlashcardsController(FlashcardStorage flashcardStorage, Logger logger) {
        this.flashcardStorage = flashcardStorage;
        this.logger = logger;
    }

    public FlashcardStorage getFlashcardStorage() {
        return flashcardStorage;
    }

    public static Map<String, String> transformToFlashcardsMap(Set<Flashcard> flashcards) {
        Map<String, String> flashcardMap = new HashMap<>();

        flashcards.forEach(fc -> flashcardMap.put(
                noSpaceAndLowerCase(fc.getTerm()),
                noSpaceAndLowerCase(fc.getDefinition())
        ));

        return flashcardMap;
    }

    public static String noSpaceAndLowerCase(String text) {
        return text
                .toLowerCase()
                .replaceAll("\\s+", "");
    }

    public void interactiveImport() {
        logger.println("File name:");
        String fileName = logger.nextLine();
        importCardsFromFile(fileName);
    }

    public void importCardsFromFile(String filename) {
        Set<Flashcard> flashcardsFormFile = readFlashcardsFromFile(filename);

        int importedCardsNumber = flashcardsFormFile.size();
        if (importedCardsNumber == 0) {
            logger.println("No cards has been loaded.");
            return;
        }

        Set<Flashcard> flashcards = flashcardStorage.getFlashcards();
        flashcardsFormFile.addAll(flashcards);
        flashcards.clear();
        flashcards.addAll(flashcardsFormFile);
        logger.printf("%d cards have been loaded.\n", importedCardsNumber);
    }


    @SuppressWarnings("unchecked")
    private Set<Flashcard> readFlashcardsFromFile(String fileName) {

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

    public void interactiveExport() {
        logger.println("File name:");
        String fileName = logger.nextLine().trim();
        exportCardsToFile(fileName);
    }

    void exportCardsToFile(String filename) {

        Set<Flashcard> flashcards = flashcardStorage.getFlashcards();
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

}
