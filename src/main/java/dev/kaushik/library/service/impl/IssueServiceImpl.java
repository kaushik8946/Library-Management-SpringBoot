package dev.kaushik.library.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.kaushik.library.dao.BookDAO;
import dev.kaushik.library.dao.IssueRecordDAO;
import dev.kaushik.library.dao.MemberDAO;
import dev.kaushik.library.exception.LibraryException;
import dev.kaushik.library.model.Book;
import dev.kaushik.library.model.IssueRecord;
import dev.kaushik.library.model.Member;
import dev.kaushik.library.model.enums.BookAvailability;
import dev.kaushik.library.model.enums.BookStatus;
import dev.kaushik.library.model.enums.IssueStatus;
import dev.kaushik.library.service.IssueService;
import dev.kaushik.library.validator.IssueValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Service
public class IssueServiceImpl implements IssueService {
	private final BookDAO bookDAO;
	private final MemberDAO memberDAO;
	private final IssueRecordDAO issueRecordDAO;

	@Autowired
	public IssueServiceImpl(BookDAO bookDAO, MemberDAO memberDAO, IssueRecordDAO issueRecordDAO) {
		this.bookDAO = bookDAO;
		this.memberDAO = memberDAO;
		this.issueRecordDAO = issueRecordDAO;
	}

	@Override
	public Integer issueBook(@Positive int bookId, @Positive int memberId, @NotBlank String issuedBy) {
		List<Book> books = bookDAO.findBooks(Book.builder().bookId(bookId).build());
		if (books.isEmpty() || books.get(0).getAvailability() == BookAvailability.ISSUED) {
			throw new LibraryException("Book with ID " + bookId + " is not available for issue");
		}
		if (books.get(0).getStatus() == BookStatus.INACTIVE) {
			throw new LibraryException("Book with ID " + bookId + " is Inactive");
		}
		List<Member> members = memberDAO.findMembers(Member.builder().memberID(memberId).build());
		if (members.isEmpty()) {
			throw new LibraryException("Member with ID " + memberId + " not found.");
		}
        
        if (issueRecordDAO.getActiveIssueRecordByBookId(bookId).isPresent()) {
            throw new LibraryException("Book with ID " + bookId + " is already issued.");
        }

        IssueRecord issueRecord = new IssueRecord();
		issueRecord.setBookId(bookId);
		issueRecord.setMemberId(memberId);
		issueRecord.setStatus(IssueStatus.ISSUED);
		issueRecord.setIssueDate(LocalDateTime.now());
		issueRecord.setIssuedBy(issuedBy);

        IssueValidator.validate(issueRecord);
        Integer issueId = issueRecordDAO.addIssueRecord(issueRecord);

        bookDAO.updateBookAvailabilityBatch(Collections.singletonList(bookId));
        
        return issueId;
    }

	@Override
	public Integer returnBook(@Positive int bookId, @NotBlank String returnedBy) {
		Optional<IssueRecord> optionalRecord = issueRecordDAO.getActiveIssueRecordByBookId(bookId);
		if (optionalRecord.isEmpty()) {
			throw new LibraryException("No active issue record found for Book ID: " + bookId);
		}

		IssueRecord issueRecordToUpdate = optionalRecord.get();
		issueRecordToUpdate.setReturnDate(LocalDateTime.now());
		issueRecordToUpdate.setReturnedBy(returnedBy);
		issueRecordToUpdate.setStatus(IssueStatus.RETURNED);
		IssueValidator.validate(issueRecordToUpdate);
		issueRecordDAO.markAsReturned(issueRecordToUpdate);
		return bookDAO.updateBookAvailabilityBatch(List.of(bookId));
	}

	@Override
	public List<IssueRecord> getAllIssuedRecords() throws LibraryException {
		return issueRecordDAO.getIssuedRecords(null);
	}

	@Override
	public List<IssueRecord> getOverdueBooks() throws LibraryException {
		int dueDays = 14;
		List<IssueRecord> allIssuedRecords = issueRecordDAO.getIssuedRecords(null);

		LocalDateTime now = LocalDateTime.now();

		return allIssuedRecords.stream()
				.filter(record -> record.getStatus() == IssueStatus.ISSUED && record.getReturnDate() == null)
				.filter(record -> record.getIssueDate().plusDays(dueDays).isBefore(now))
				.collect(Collectors.toList());
	}

	@Override
	public List<Member> getMembersWithActiveBooks() throws LibraryException {
        IssueRecord criteria = IssueRecord.builder().status(IssueStatus.ISSUED).build();
        List<IssueRecord> issuedRecords = issueRecordDAO.getIssuedRecords(criteria);

        List<Integer> memberIds = issuedRecords.stream()
                .map(IssueRecord::getMemberId)
                .distinct()
                .collect(Collectors.toList());

        return memberDAO.findMembersByIds(memberIds);
        
	}

}
