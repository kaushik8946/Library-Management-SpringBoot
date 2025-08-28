package dev.kaushik.library.dao;

import java.util.List;
import java.util.Optional;

import dev.kaushik.library.model.IssueRecord;

public interface IssueRecordDAO {

	int addIssueRecord(IssueRecord issueRecord);

	boolean markAsReturned(IssueRecord issueRecord);
	
	List<IssueRecord> getIssuedRecords(IssueRecord criteria);

	Optional<IssueRecord> getActiveIssueRecordByBookId(int bookId);

}