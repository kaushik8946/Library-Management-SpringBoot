package dev.kaushik.library.service;

import java.util.List;

import dev.kaushik.library.model.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface MemberService {

	Integer addMember(@Valid @NotNull Member member);

	boolean updateMember(@Valid @NotNull Member member);

	boolean deleteMember(@NotNull @Positive Integer memberId);

	int deleteMembers(@NotNull List<@NotNull @Positive Integer> memberIds);

	List<Member> findMembers(Member criteria);
}