package se.chasacademy.fritidslagret.repository.memory;

import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryEquipmentRepository implements EquipmentRepository {
    private final Map<String, Equipment> equipmentById = new LinkedHashMap<>();

    @Override
    public void save(Equipment equipment) {
        equipmentById.put(equipment.getId(), equipment);
    }

    @Override
    public Optional<Equipment> findById(String id) {
        return Optional.ofNullable(equipmentById.get(id));
    }

    @Override
    public List<Equipment> findAll() {
        return List.copyOf(new ArrayList<>(equipmentById.values()));
    }

    @Override
    public boolean existsById(String id) {
        return equipmentById.containsKey(id);
    }
}
