package se.chasacademy.fritidslagret.ui;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScannerConsoleIOTest {
    @Test
    void invalidTextAndOutOfRangeChoiceAreHandledBeforeValidChoice() {
        ByteArrayInputStream input = new ByteArrayInputStream("apa\n9\n2\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        ScannerConsoleIO io = new ScannerConsoleIO(input, new PrintStream(outputBytes, true, StandardCharsets.UTF_8));

        int choice = io.readChoice("Välj: ", 0, 6);

        assertEquals(2, choice);
        String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Ange ett heltal mellan 0 och 6"));
    }

    @Test
    void closedInputRaisesControlledInputException() {
        ScannerConsoleIO io = new ScannerConsoleIO(
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream()));

        assertThrows(InputClosedException.class, () -> io.readNonBlank("Välj: "));
    }
}
