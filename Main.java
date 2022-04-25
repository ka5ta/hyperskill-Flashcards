package flashcards;

import java.util.*;
import java.util.stream.Collectors;

import static flashcards.FlashcardsController.noSpaceAndLowerCase;
import static flashcards.FlashcardsController.transformToFlashcardsMap;


public class Main {

    static Logger logger = new Logger();
    static String exportFile = "";

    public static void main(String[] args) {
        FlashcardsController flashcardsController = new FlashcardsController(new FlashcardStorage(logger), logger);
        importOrExportFromFileCommandLineArgs(args, flashcardsController);
        startFlashcardsApp(flashcardsController);
    }

    private static void startFlashcardsApp(FlashcardsController controller) {

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

            FlashcardStorage flashcards = controller.getFlashcardStorage();
            switch (enumAction) {
                case ADD:
                    flashcards.createFlashCards();
                    break;
                case REMOVE:
                    flashcards.removeCard();
                    break;
                case IMPORT:
                    controller.interactiveImport();
                    break;
                case EXPORT:
                    controller.interactiveExport();
                    break;
                case ASK:
                    new TestKnowledge(flashcards, logger).start();
                    break;
                case EXIT:
                    logger.println("Bye bye!");
                    controller.exportCardsToFile(exportFile);
                    return;
                case CARDS:
                    flashcards.getFlashcards().forEach(System.out::println);
                    break;
                case LOG:
                    logger.saveLogsToFile();
                    break;
                case HARDEST_CARD:
                    flashcards.printHardestCards();
                    break;
                case RESET_STATS:
                    flashcards.resetStats();
                    break;
                default:
                    break;
            }
        }
    }

    private static void importOrExportFromFileCommandLineArgs(String[] args, FlashcardsController controller) {
        for (int i = 1; i < args.length; i += 2) {
            switch (args[i - 1]) {
                case "-import":
                    controller.importCardsFromFile(args[i]);
                    break;
                case "-export":
                    exportFile = args[i];
                    controller.exportCardsToFile(exportFile);
                default:
                    break;
            }
        }
    }




}






