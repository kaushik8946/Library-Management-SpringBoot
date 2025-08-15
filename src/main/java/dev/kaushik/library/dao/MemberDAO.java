package dev.kaushik.library.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import dev.kaushik.library.model.Member;

public interface MemberDAO {
    int addMember(Member member) throws DataAccessException;

    boolean updateMember(Member member) throws DataAccessException;

    boolean deleteMember(int memberId) throws DataAccessException;

    int deleteMembersInBatch(Integer... memberIds) throws DataAccessException;

    List<Member> findMembers(Member criteria) throws DataAccessException;
}