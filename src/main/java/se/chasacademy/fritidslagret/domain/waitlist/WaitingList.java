package se.chasacademy.fritidslagret.domain.waitlist;

import se.chasacademy.fritidslagret.exception.AlreadyQueuedException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** FIFO-kö för en specifik utrustning. */
public final class WaitingList {
    private final String equipmentId;
    private final Deque<String> memberQueue = new ArrayDeque<>();
    private final Set<String> queuedMemberIds = new HashSet<>();

    public WaitingList(String equipmentId) {
        this.equipmentId = Objects.requireNonNull(equipmentId, "Utrustnings-ID får inte vara null.").trim();
        if (this.equipmentId.isEmpty()) {
            throw new IllegalArgumentException("Utrustnings-ID får inte vara tomt.");
        }
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public int enqueue(String memberId) {
        String normalizedId = Objects.requireNonNull(memberId, "Medlems-ID får inte vara null.").trim();
        if (normalizedId.isEmpty()) {
            throw new IllegalArgumentException("Medlems-ID får inte vara tomt.");
        }
        if (!queuedMemberIds.add(normalizedId)) {
            throw new AlreadyQueuedException("Medlemmen " + normalizedId + " står redan i kön.");
        }
        memberQueue.offer(normalizedId);
        return memberQueue.size();
    }

    public Optional<String> poll() {
        String memberId = memberQueue.poll();
        if (memberId != null) {
            queuedMemberIds.remove(memberId);
        }
        return Optional.ofNullable(memberId);
    }

    public boolean remove(String memberId) {
        if (!queuedMemberIds.remove(memberId)) {
            return false;
        }
        memberQueue.remove(memberId);
        return true;
    }

    public boolean contains(String memberId) {
        return queuedMemberIds.contains(memberId);
    }

    public boolean isEmpty() {
        return memberQueue.isEmpty();
    }

    public int size() {
        return memberQueue.size();
    }

    public List<String> snapshot() {
        return List.copyOf(new ArrayList<>(memberQueue));
    }
}
