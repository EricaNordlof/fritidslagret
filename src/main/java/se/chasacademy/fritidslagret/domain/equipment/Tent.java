package se.chasacademy.fritidslagret.domain.equipment;

public final class Tent extends Equipment {
    public static final int MAX_LOAN_DAYS = 5;

    public Tent(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxLoanDays() {
        return MAX_LOAN_DAYS;
    }

    @Override
    public String getTypeName() {
        return "Tält";
    }
}
