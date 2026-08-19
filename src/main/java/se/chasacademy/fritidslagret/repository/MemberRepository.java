package se.chasacademy.fritidslagret.repository;

import se.chasacademy.fritidslagret.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    void save(Member member);

    Optional<Member> findById(String id);

    List<Member> findAll();

    boolean existsById(String id);
}
