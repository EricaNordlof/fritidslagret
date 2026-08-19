package se.chasacademy.fritidslagret.service;

import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.domain.member.MemberStatus;
import se.chasacademy.fritidslagret.exception.DuplicateIdException;
import se.chasacademy.fritidslagret.exception.EntityNotFoundException;
import se.chasacademy.fritidslagret.repository.MemberRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = Objects.requireNonNull(memberRepository);
    }

    public Member register(Member member) {
        Objects.requireNonNull(member, "Medlem får inte vara null.");
        if (memberRepository.existsById(member.getId())) {
            throw new DuplicateIdException("Medlems-ID " + member.getId() + " används redan.");
        }
        memberRepository.save(member);
        return member;
    }

    public Member changeStatus(String memberId, MemberStatus status) {
        Member member = getRequired(memberId);
        member.changeStatus(status);
        return member;
    }

    public Member getRequired(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingen medlem hittades med ID " + id + "."));
    }

    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    public List<Member> getAll() {
        return memberRepository.findAll();
    }
}
