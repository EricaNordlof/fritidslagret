package se.chasacademy.fritidslagret.report;

import java.io.PrintStream;
import java.util.Objects;

public final class ConsoleReportWriter implements ReportWriter {
    private final PrintStream output;

    public ConsoleReportWriter(PrintStream output) {
        this.output = Objects.requireNonNull(output);
    }

    @Override
    public void write(String report) {
        output.println(report);
    }
}
