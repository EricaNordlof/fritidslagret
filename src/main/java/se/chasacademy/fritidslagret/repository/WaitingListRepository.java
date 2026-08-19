package se.chasacademy.fritidslagret.repository;

import se.chasacademy.fritidslagret.domain.waitlist.WaitingList;

import java.util.List;
import java.util.Optional;

public interface WaitingListRepository {
    WaitingList getOrCreate(String equipmentId);

    Optional<WaitingList> findByEquipmentId(String equipmentId);

    List<WaitingList> findAll();
}
