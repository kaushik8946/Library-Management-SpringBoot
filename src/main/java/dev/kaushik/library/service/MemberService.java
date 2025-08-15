package dev.kaushik.library.service;

import java.util.List;

import dev.kaushik.library.exception.LibraryException;
import dev.kaushik.library.model.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface MemberService {

    Integer addMember(@Valid @NotNull Member member) throws LibraryException;

    boolean updateMember(@Valid @NotNull Member member) throws LibraryException;

    boolean deleteMember(@NotNull @Positive Integer memberId) throws LibraryException;

    int deleteMembers(@NotNull List<@NotNull @Positive Integer> memberIds) throws LibraryException;

    List<Member> findMembers(Member criteria) throws LibraryException;
}