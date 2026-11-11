package com.example.library.service;
import com.example.library.entity.Member;
import com.example.library.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @Transactional
public class MemberService {
    private final MemberRepository members;
    public MemberService(MemberRepository members) { this.members = members; }
    @Transactional(readOnly = true)
    public List<Member> list() { return members.findAll(); }
    @Transactional(readOnly = true)
    public Member get(Long id) { return members.findById(id).orElseThrow(() -> new NoSuchElementException("Member not found")); }
    public Member create(Member member) { member.setId(null); return members.save(member); }
}
