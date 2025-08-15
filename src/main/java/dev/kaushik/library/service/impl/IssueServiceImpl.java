package dev.kaushik.library.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import dev.kaushik.library.dao.*;
import dev.kaushik.library.exception.LibraryException;
import dev.kaushik.library.model.*;
import dev.kaushik.library.model.enums.*;
import dev.kaushik.library.service.IssueService;

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
	public Integer issueBook(int bookId, int memberId, String issuedBy) throws LibraryException {

		List<Book> books = bookDAO.findBooks(Book.builder().bookId(bookId).build());
		if (books.isEmpty() || books.get(0).getAvailability() == BookAvailability.ISSUED) {
			throw new LibraryException("Book with ID " + bookId + " is not available for issue");
		}

		List<Member> members = memberDAO.findMembers(Member.builder().memberID(memberId).build());
		if (members.isEmpty()) {
			throw new LibraryException("Member with ID " + memberId + " not found.");
		}

		if (issueRecordDAO.getActiveIssueRecordByBookId(bookId).isPresent()) {
			throw new LibraryException("Book with ID " + bookId + " is already actively issued.");
		}

		try {
			IssueRecord issueRecord = IssueRecord.builder().bookId(bookId).memberId(memberId).status(IssueStatus.ISSUED)
					.issueDate(LocalDateTime.now()).issuedBy(issuedBy).build();
			Integer issueId = issueRecordDAO.addIssueRecord(issueRecord);
			bookDAO.updateBookAvailabilityBatch(List.of(bookId));
			return issueId;
		} catch (DataAccessException e) {
			throw new LibraryException("Failed to issue book: " + e.getMessage(), e);
		}
	}

	@Override
	public Integer returnBook(int bookId, String returnedBy) throws LibraryException {
		Optional<IssueRecord> optionalRecord = issueRecordDAO.getActiveIssueRecordByBookId(bookId);
		if (optionalRecord.isEmpty()) {
			throw new LibraryException("No active issue record found for Book ID: " + bookId);
		}

		try {
			IssueRecord issueRecordToUpdate = optionalRecord.get();
			issueRecordToUpdate.setReturnDate(LocalDateTime.now());
			issueRecordToUpdate.setReturnedBy(returnedBy);
			issueRecordToUpdate.setStatus(IssueStatus.RETURNED);
			issueRecordDAO.markAsReturned(issueRecordToUpdate);

			return bookDAO.updateBookAvailabilityBatch(List.of(bookId));
		} catch (DataAccessException e) {
			throw new LibraryException("Failed to return book: " + e.getMessage(), e);
		}
	}

	@Override
	public List<IssueRecord> getAllIssuedRecords() throws LibraryException {
		try {
			return issueRecordDAO.getIssuedRecords(null);
		} catch (DataAccessException e) {
			throw new LibraryException("Failed to get all issued records: " + e.getMessage(), e);
		}
	}

	@Override
	public List<IssueRecord> getOverdueBooks() throws LibraryException {
		try {
			int dueDays = 14;
			List<IssueRecord> allIssuedRecords = issueRecordDAO.getIssuedRecords(null);

			LocalDateTime now = LocalDateTime.now();

			return allIssuedRecords.stream()
					.filter(record -> record.getStatus() == IssueStatus.ISSUED && record.getReturnDate() == null)
					.filter(record -> record.getIssueDate().plusDays(dueDays).isBefore(now))
					.collect(Collectors.toList());

		} catch (DataAccessException e) {
			throw new LibraryException("Failed to retrieve overdue books: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Member> getMembersWithActiveBooks() throws LibraryException {
		try {
			IssueRecord criteria = IssueRecord.builder().status(IssueStatus.ISSUED).build();
			List<IssueRecord> issuedRecords = issueRecordDAO.getIssuedRecords(criteria);

			List<Integer> memberIds = issuedRecords.stream().map(IssueRecord::getMemberId).distinct()
					.collect(Collectors.toList());

			List<Member> allMembers = memberDAO.findMembers(null);

			return allMembers.stream().filter(member -> memberIds.contains(member.getMemberID()))
					.collect(Collectors.toList());
		} catch (DataAccessException e) {
			throw new LibraryException("Failed to retrieve members with active books: " + e.getMessage(), e);
		}
	}

}
