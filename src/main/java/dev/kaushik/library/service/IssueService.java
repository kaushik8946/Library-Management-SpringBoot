package dev.kaushik.library.service;

import java.util.List;

import dev.kaushik.library.model.IssueRecord;
import dev.kaushik.library.model.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface IssueService {

	Integer issueBook(@Positive int bookId, @Positive int memberId, @NotNull String issuedBy);

	Integer returnBook(@Positive int bookId, @NotNull String returnedBy);

	List<IssueRecord> getAllIssuedRecords();

	List<IssueRecord> getOverdueBooks();

	List<Member> getMembersWithActiveBooks();
}