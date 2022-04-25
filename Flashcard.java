package flashcards;

import java.io.Serializable;
import java.util.Objects;

public class Flashcard implements Serializable {
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
