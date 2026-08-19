package se.chasacademy.fritidslagret.repository.memory;

import se.chasacademy.fritidslagret.domain.waitlist.WaitingList;
import se.chasacademy.fritidslagret.repository.WaitingListRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryWaitingListRepository implements WaitingListRepository {
    private final Map<String, WaitingList> waitingListByEquipmentId = new LinkedHashMap<>();

    @Override
    public WaitingList getOrCreate(String equipmentId) {
        return waitingListByEquipmentId.computeIfAbsent(equipmentId, WaitingList::new);
    }

    @Override
    public Optional<WaitingList> findByEquipmentId(String equipmentId) {
        return Optional.ofNullable(waitingListByEquipmentId.get(equipmentId));
    }

    @Override
    public List<WaitingList> findAll() {
        return List.copyOf(new ArrayList<>(waitingListByEquipmentId.values()));
    }
}
