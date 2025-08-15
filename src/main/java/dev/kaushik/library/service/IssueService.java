package dev.kaushik.library.service;

import java.util.List;

import dev.kaushik.library.exception.LibraryException;
import dev.kaushik.library.model.IssueRecord;
import dev.kaushik.library.model.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface IssueService {

    Integer issueBook(@Positive int bookId, @Positive int memberId, @NotNull String issuedBy) throws LibraryException;

    Integer returnBook(@Positive int bookId, @NotNull String returnedBy) throws LibraryException;

    List<IssueRecord> getAllIssuedRecords() throws LibraryException;

    List<IssueRecord> getOverdueBooks() throws LibraryException;

    List<Member> getMembersWithActiveBooks() throws LibraryException;
}