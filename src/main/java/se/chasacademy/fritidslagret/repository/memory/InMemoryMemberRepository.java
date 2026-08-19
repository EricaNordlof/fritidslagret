package se.chasacademy.fritidslagret.repository.memory;

import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.repository.MemberRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryMemberRepository implements MemberRepository {
    private final Map<String, Member> memberById = new LinkedHashMap<>();

    @Override
    public void save(Member member) {
        memberById.put(member.getId(), member);
    }

    @Override
    public Optional<Member> findById(String id) {
        return Optional.ofNullable(memberById.get(id));
    }

    @Override
    public List<Member> findAll() {
        return List.copyOf(new ArrayList<>(memberById.values()));
    }

    @Override
    public boolean existsById(String id) {
        return memberById.containsKey(id);
    }
}
