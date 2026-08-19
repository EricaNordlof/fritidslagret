package se.chasacademy.fritidslagret.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public final class ScannerConsoleIO implements ConsoleIO {
    private final Scanner scanner;
    private final PrintStream output;

    public ScannerConsoleIO(InputStream input, PrintStream output) {
        this.scanner = new Scanner(Objects.requireNonNull(input));
        this.output = Objects.requireNonNull(output);
    }

    @Override
    public void println(String text) {
        output.println(text);
    }

    @Override
    public String readNonBlank(String prompt) {
        while (true) {
            output.print(prompt);
            if (!scanner.hasNextLine()) {
                throw new InputClosedException();
            }
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            output.println("[FEL] Värdet får inte vara tomt.");
        }
    }

    @Override
    public int readChoice(String prompt, int min, int max) {
        while (true) {
            String value = readNonBlank(prompt);
            try {
                int choice = Integer.parseInt(value);
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
                // Kontrollerat fel: användaren får försöka igen nedan.
            }
            output.printf("[FEL] Ange ett heltal mellan %d och %d.%n", min, max);
        }
    }

    @Override
    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readNonBlank(prompt).toLowerCase(Locale.ROOT);
            if (value.equals("j") || value.equals("ja")) {
                return true;
            }
            if (value.equals("n") || value.equals("nej")) {
                return false;
            }
            output.println("[FEL] Svara j eller n.");
        }
    }
}
