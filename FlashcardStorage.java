package flashcards;

import java.util.*;
import java.util.stream.Collectors;

class FlashcardStorage {

    Set<Flashcard> flashcards;
    Logger logger;

    public FlashcardStorage(Logger logger) {
        this.flashcards = new HashSet<>();
        this.logger = logger;
    }

    public Set<Flashcard> getFlashcards() {
        return flashcards;
    }

    Set<Flashcard> createFlashCards() {
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
        return this.flashcards;
    }

    public Flashcard createUniqueFlashcard(int cardNumber) {

        String term;
        String definition;
        int countOfMistakes = 0;
        Map<String, String> flashcardMap = FlashcardsController.transformToFlashcardsMap(this.flashcards);

        logger.println("The card:");
        term = logger.nextLine();
        if (flashcardMap.containsKey(FlashcardsController.noSpaceAndLowerCase(term))) {
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
        if (flashcardMap.containsValue(FlashcardsController.noSpaceAndLowerCase(definition))) {
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

    void removeCard() {
        logger.println("Which card?");
        String termToRemove = logger.nextLine();
        String termToRemoveLowercase = FlashcardsController.noSpaceAndLowerCase(termToRemove);

        for (Flashcard flashcard : flashcards) {
            String currentFlashcard = FlashcardsController.noSpaceAndLowerCase(flashcard.getTerm());
            if (currentFlashcard.equals(termToRemoveLowercase)) {
                flashcards.remove(flashcard);
                logger.println("The card has been removed.");
                return;
            }
        }
        logger.printf("Can't remove \"%s\": there is no such card.\n", termToRemove);
    }



    void resetStats() {
        flashcards.forEach(i -> i.setCountOfMistakes(0));
        logger.println("Card statistics have been reset.");
    }

    void printHardestCards() {
        String message = "There are no cards with errors.";
        Set<Flashcard> flashcards = this.getFlashcards();

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

}