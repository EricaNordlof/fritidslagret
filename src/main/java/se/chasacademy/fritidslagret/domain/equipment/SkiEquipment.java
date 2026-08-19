package se.chasacademy.fritidslagret.domain.equipment;

public final class SkiEquipment extends Equipment {
    public static final int MAX_LOAN_DAYS = 3;

    public SkiEquipment(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxLoanDays() {
        return MAX_LOAN_DAYS;
    }

    @Override
    public String getTypeName() {
        return "Skidutrustning";
    }
}
