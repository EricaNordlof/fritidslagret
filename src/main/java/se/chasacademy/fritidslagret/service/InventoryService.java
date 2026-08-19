package se.chasacademy.fritidslagret.service;

import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.exception.DuplicateIdException;
import se.chasacademy.fritidslagret.exception.EntityNotFoundException;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;

import java.util.List;
import java.util.Objects;

public final class InventoryService {
    private final EquipmentRepository equipmentRepository;

    public InventoryService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = Objects.requireNonNull(equipmentRepository);
    }

    public Equipment register(Equipment equipment) {
        Objects.requireNonNull(equipment, "Utrustning får inte vara null.");
        if (equipmentRepository.existsById(equipment.getId())) {
            throw new DuplicateIdException("Utrustnings-ID " + equipment.getId() + " används redan.");
        }
        equipmentRepository.save(equipment);
        return equipment;
    }

    public Equipment getRequired(String id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingen utrustning hittades med ID " + id + "."));
    }

    public List<Equipment> getAll() {
        return equipmentRepository.findAll();
    }
}
