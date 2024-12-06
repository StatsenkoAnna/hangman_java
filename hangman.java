import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HangmanGame {

    public static void main(String[] args) {
        Game game = new Game("words.txt");
        game.start();
    }
}

class Game {
    private final WordSelector wordSelector;
    private final UserInterface userInterface;
    private final String wordToGuess;
    private final int maxAttempts = 6;
    private int attemptsLeft;
    private StringBuilder guessedWord;

    public Game(String wordsFilePath) {
        this.wordSelector = new WordSelector(wordsFilePath);
        this.userInterface = new UserInterface();
        this.wordToGuess = wordSelector.getRandomWord();
        this.attemptsLeft = maxAttempts;
        this.guessedWord = new StringBuilder("_".repeat(wordToGuess.length()));
    }

    public void start() {
        userInterface.printWelcomeMessage();
        while (attemptsLeft > 0 && !wordToGuess.equals(guessedWord.toString())) {
            userInterface.printCurrentState(guessedWord.toString(), attemptsLeft, maxAttempts);
            char guess = userInterface.readPlayerGuess();
            processGuess(guess);
        }
        userInterface.printFinalResult(wordToGuess, guessedWord.toString());
    }

    private void processGuess(char guess) {
        if (wordToGuess.indexOf(guess) != -1) {
            updateGuessedWord(guess);
            userInterface.printCorrectGuessMessage();
        } else {
            attemptsLeft--;
            userInterface.printIncorrectGuessMessage();
        }
    }

    private void updateGuessedWord(char guess) {
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                guessedWord.setCharAt(i, guess);
            }
        }
    }
}

class WordSelector {
    private final List<String> words;

    public WordSelector(String filePath) {
        this.words = readWordsFromFile(filePath);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            System.err.println("Файл с словами пуст.");
            System.exit(1);
        }
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
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
            System.exit(1);
        }
        return words;
    }
}

class UserInterface {
    private final Scanner scanner = new Scanner(System.in);
    private final String[] hangmanStages = {
        "  +---+\n" +
        "  |   |\n" +
        "      |\n" +
        "      |\n" +
        "      |\n" +
        "      |\n" +
        "=========",
        "  +---+\n" +
        "  |   |\n" +
        "  O   |\n" +
        "      |\n" +
        "      |\n" +
        "      |\n" +
        "=========",
        "  +---+\n" +
        "  |   |\n" +
        "  O   |\n" +
        "  |   |\n" +
        "      |\n" +
        "      |\n" +
        "=========",
        "  +---+\n" +
        "  |   |\n" +
        "  O   |\n" +
        " /|   |\n" +
        "      |\n" +
        "      |\n" +
        "=========",
        "  +---+\n" +
        "  |   |\n" +
        "  O   |\n" +
        " /|\\  |\n" +
        "      |\n" +
        "      |\n" +
        "=========",
        "  +---+\n" +
        "  |   |\n" +
        "  O   |\n" +
        " /|\\  |\n" +
        " /    |\n" +
        "      |\n" +
        "=========",
        "  +---+\n" +
        "  |   |\n" +
        "  O   |\n" +
        " /|\\  |\n" +
        " / \\  |\n" +
        "      |\n" +
        "========="
    };

    public void printWelcomeMessage() {
        System.out.println("Добро пожаловать в игру Виселица!");
    }

    public void printCurrentState(String guessedWord, int attemptsLeft, int maxAttempts) {
        System.out.println(hangmanStages[maxAttempts - attemptsLeft]);
        System.out.println("Текущее состояние слова: " + guessedWord);
        System.out.println("Осталось попыток: " + attemptsLeft);
    }

    public char readPlayerGuess() {
        System.out.print("Введите букву: ");
        String input = scanner.nextLine();
        if (input.length() != 1) {
            System.out.println("Пожалуйста, введите только одну букву.");
            return readPlayerGuess();
        }
        return input.charAt(0);
    }

    public void printCorrectGuessMessage() {
        System.out.println("Правильная буква!");
    }

    public void printIncorrectGuessMessage() {
        System.out.println("Неправильная буква!");
    }

    public void printFinalResult(String wordToGuess, String guessedWord) {
        if (wordToGuess.equals(guessedWord)) {
            System.out.println("Поздравляем! Вы угадали слово: " + wordToGuess);
        } else {
            System.out.println("Вы проиграли. Загаданное слово было: " + wordToGuess);
        }
        scanner.close();
    }
}
