package se.chasacademy.fritidslagret.domain.equipment;

public final class Bicycle extends Equipment {
    public static final int MAX_LOAN_DAYS = 7;

    public Bicycle(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxLoanDays() {
        return MAX_LOAN_DAYS;
    }

    @Override
    public String getTypeName() {
        return "Cykel";
    }
}
