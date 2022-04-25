package flashcards;

import java.util.HashMap;
import java.util.Map;

public enum Action {
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
