package se.chasacademy.fritidslagret.util;

@FunctionalInterface
public interface LoanIdGenerator {
    String nextId();
}
