package flashcards;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static flashcards.FlashcardsController.noSpaceAndLowerCase;
import static flashcards.FlashcardsController.transformToFlashcardsMap;

public class TestKnowledge {

    private final FlashcardStorage flashcardStorage;
    private final Logger logger;

    public TestKnowledge(FlashcardStorage flashcardStorage, Logger logger) {
        this.flashcardStorage = flashcardStorage;
        this.logger = logger;
    }

    void start() {

        Set<Flashcard> flashcards = flashcardStorage.getFlashcards();
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

    private int getInputAsNumber(String message) {
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

    private String getTermsForDefinition(Map<String, String> map, String value) {
        Optional<String> key = map.entrySet().stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
        return key.orElse(null);
    }

    private boolean compareAnswer(String definition, String answer) {
        String trimLowerAnswer = noSpaceAndLowerCase(answer);
        String trimLowerDef = noSpaceAndLowerCase(definition);

        return trimLowerAnswer.equals(trimLowerDef);
    }
}
