package se.chasacademy.fritidslagret.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class SequentialLoanIdGenerator implements LoanIdGenerator {
    private final AtomicInteger sequence = new AtomicInteger();

    @Override
    public String nextId() {
        return "LAN-%04d".formatted(sequence.incrementAndGet());
    }
}
