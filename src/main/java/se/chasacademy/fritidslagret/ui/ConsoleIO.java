package se.chasacademy.fritidslagret.ui;

public interface ConsoleIO {
    void println(String text);

    String readNonBlank(String prompt);

    int readChoice(String prompt, int min, int max);

    boolean readYesNo(String prompt);
}
