package se.chasacademy.fritidslagret.report;

/** Utgångsport som kan ersättas med en mock eller test-dubbel. */
@FunctionalInterface
public interface ReportWriter {
    void write(String report);
}
