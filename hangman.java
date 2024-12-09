import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class HangmanGame {

    public static void main(String[] args) {
        Game game = new Game("words.txt");
        game.start();
    }
}

class Game {
    private final WordSelector wordSelector;
    private final Ui ui;
    private final String wordToGuess;
    private final int maxAttempts = 6;
    private int attemptsLeft;
    private StringBuilder guessedWord;
    private Set<Character> incorrectGuesses;

    public Game(String wordsFilePath) {
        this.wordSelector = new WordSelector(wordsFilePath);
        this.ui = new Ui();
        this.wordToGuess = wordSelector.getRandomWord().orElseThrow(() -> new RuntimeException("Файл с словами пуст."));
        this.attemptsLeft = maxAttempts;
        this.guessedWord = new StringBuilder(MASK.repeat(wordToGuess.length()));
        this.incorrectGuesses = new HashSet<>();
    }

    public void start() {
        ui.printWelcomeMessage();
        while (isInGame()) {
            ui.printCurrentState(guessedWord.toString(), attemptsLeft, maxAttempts, incorrectGuesses);
            char guess = ui.readPlayerGuess();
            processGuess(guess);
        }
        if (isWin()) {
            ui.printWin(wordToGuess);
        } else {
            ui.printLose(wordToGuess);
        }
    }

    private void processGuess(char guess) {
        guess = Character.toLowerCase(guess);
        if (!Character.isLetter(guess)) {
            ui.printInvalidGuessMessage();
            return;
        }
        if (wordToGuess.indexOf(guess) != -1) {
            updateGuessedWord(guess);
            ui.printCorrectGuessMessage();
        } else if (!incorrectGuesses.contains(guess)) {
            incorrectGuesses.add(guess);
            attemptsLeft--;
            ui.printIncorrectGuessMessage();
        } else {
            ui.printAlreadyGuessedMessage();
        }
    }

    private void updateGuessedWord(char guess) {
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                guessedWord.setCharAt(i, guess);
            }
        }
    }

    private boolean isInGame() {
        return attemptsLeft > 0 && !wordToGuess.equals(guessedWord.toString());
    }

    private boolean isWin() {
        return wordToGuess.equals(guessedWord.toString());
    }

    private static final char MASK = '_';
}

class WordSelector {
    private final List<String> words;

    public WordSelector(String filePath) {
        this.words = readWordsFromFile(filePath);
    }

    public Optional<String> getRandomWord() {
        if (words.isEmpty()) {
            return Optional.empty();
        }
        Random random = new Random();
        return Optional.of(words.get(random.nextInt(words.size())));
    }

    private List<String> readWordsFromFile(String filePath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return words;
    }
}

class Ui {
    private final Scanner scanner = new Scanner(System.in);
    private final String[] hangmanStages = {
        """
            +---+
            |   |
                |
                |
                |
                |
            =======
            """,
        """
            +---+
            |   |
            O   |
                |
                |
                |
            =======
            """,
        """
            +---+
            |   |
            O   |
            |   |
                |
                |
            =======
            """,
        """
            +---+
            |   |
            O   |
           /|   |
                |
                |
            =======
            """,
        """
            +---+
            |   |
            O   |
           /|\\  |
                |
                |
            =======
            """,
        """
            +---+
            |   |
            O   |
           /|\\  |
           /    |
                |
            =======
            """,
        """
            +---+
            |   |
            O   |
           /|\\  |
           / \\  |
                |
            =======
            """
    };

    public void printWelcomeMessage() {
        System.out.println("Добро пожаловать в игру Виселица!");
    }

    public void printCurrentState(String guessedWord, int attemptsLeft, int maxAttempts, Set<Character> incorrectGuesses) {
        System.out.println(hangmanStages[maxAttempts - attemptsLeft]);
        System.out.println("Текущее состояние слова: " + guessedWord);
        System.out.println("Осталось попыток: " + attemptsLeft);
        System.out.println("Неправильные буквы: " + incorrectGuesses);
    }

    public char readPlayerGuess() {
        while (true) {
            System.out.print("Введите букву: ");
            String input = scanner.nextLine();
            if (input.length() == 1) {
                return input.charAt(0);
            }
            System.out.println("Пожалуйста, введите только одну букву.");
        }
    }

    public void printCorrectGuessMessage() {
        System.out.println("Правильная буква!");
    }

    public void printIncorrectGuessMessage() {
        System.out.println("Неправильная буква!");
    }

    public void printInvalidGuessMessage() {
        System.out.println("Пожалуйста, введите букву русского алфавита.");
    }

    public void printAlreadyGuessedMessage() {
        System.out.println("Вы уже вводили эту букву.");
    }

    public void printWin(String wordToGuess) {
        System.out.println("Поздравляем! Вы угадали слово: " + wordToGuess);
    }

    public void printLose(String wordToGuess) {
        System.out.println("Вы проиграли. Загаданное слово было: " + wordToGuess);
    }
}
