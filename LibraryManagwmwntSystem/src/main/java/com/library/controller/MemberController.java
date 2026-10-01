package com.library.controller;

import com.library.model.Member;
import com.library.repository.MemberRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @PostMapping
    public Member addMember(@RequestBody Member member) {
        return memberRepository.save(member);
    }

    @DeleteMapping("/{id}")
    public String deleteMember(@PathVariable int id) {

        if (!memberRepository.existsById(id)) {
            return "Member not found";
        }

        memberRepository.deleteById(id);

        return "Member deleted successfully";
    }
}