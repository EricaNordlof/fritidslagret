package se.chasacademy.fritidslagret.repository;

import se.chasacademy.fritidslagret.domain.equipment.Equipment;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {
    void save(Equipment equipment);

    Optional<Equipment> findById(String id);

    List<Equipment> findAll();

    boolean existsById(String id);
}
