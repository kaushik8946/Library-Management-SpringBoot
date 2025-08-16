package dev.kaushik.library.controller;

import dev.kaushik.library.model.Member;
import dev.kaushik.library.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/addMember")
    public ResponseEntity<Integer> addMember(@Valid @RequestBody Member member) {
        Integer memberId = memberService.addMember(member);
        return new ResponseEntity<>(memberId, HttpStatus.CREATED);
    }

    @PutMapping("/updateMember")
    public ResponseEntity<Boolean> updateMember(@Valid @RequestBody Member member) {
        boolean updated = memberService.updateMember(member);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/deleteMember/{memberId}")
    public ResponseEntity<Boolean> deleteMember(@PathVariable @NotNull @Positive Integer memberId) {
        boolean deleted = memberService.deleteMember(memberId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @PostMapping("/batchDeleteMembers")
    public ResponseEntity<Integer> deleteMembersBatch(@RequestBody @NotNull List<@NotNull @Positive Integer> memberIds) {
        Integer deletedCount = memberService.deleteMembers(memberIds);
        return new ResponseEntity<>(deletedCount, HttpStatus.OK);
    }

    @GetMapping("/viewMembers")
    public ResponseEntity<List<Member>> viewMembers(@RequestBody(required = false) Member criteria) {
        List<Member> members = memberService.findMembers(criteria);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }
}