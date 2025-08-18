package dev.kaushik.library.service.impl;

import dev.kaushik.library.dao.MemberDAO;
import dev.kaushik.library.exception.LibraryException;
import dev.kaushik.library.model.Member;
import dev.kaushik.library.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Service
@Validated
public class MemberServiceImpl implements MemberService {
	private final MemberDAO memberDAO;

	@Autowired
	public MemberServiceImpl(MemberDAO memberDAO) {
		this.memberDAO = memberDAO;
	}

	@Override
	public Integer addMember(@Valid @NotNull Member member) throws LibraryException {
		if (member.getMemberID() != null) {
			throw new LibraryException("Member ID can not be specified, it will be auto generated");
		}
		try {
			return memberDAO.addMember(member);
		} catch (DataAccessException e) { 
			throw new LibraryException("Failed to add member: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public boolean updateMember(@Valid @NotNull Member member) throws LibraryException {
		if (member.getMemberID() == null || member.getMemberID() <= 0) {
			throw new LibraryException("Member ID must be provided and be a positive number for update.");
		}
		try {
			return memberDAO.updateMember(member);
		} catch (DataAccessException e) {
			throw new LibraryException("Failed to update member with ID: " + member.getMemberID(), e);
		}
	}

	@Override
	@Transactional
	public boolean deleteMember(@NotNull @Positive Integer memberId) throws LibraryException {
		try {
			return memberDAO.deleteMember(memberId);
		} catch (DataAccessException e) {
			throw new LibraryException("Failed to delete member with ID: " + memberId, e);
		}
	}

	@Override
	@Transactional
	public int deleteMembers(@NotNull List<@NotNull @Positive Integer> memberIds) throws LibraryException {
		try {
			Integer[] memberIdArray = memberIds.toArray(new Integer[0]);
			return memberDAO.deleteMembersInBatch(memberIdArray);
		} catch (DataAccessException e) {
			throw new LibraryException("Failed to delete members in batch", e);
		}
	}

	@Override
	public List<Member> findMembers(Member criteria) throws LibraryException {
		try {
			return memberDAO.findMembers(criteria);
		} catch (DataAccessException e) {
			throw new LibraryException("Failed to find members", e);
		}
	}
}