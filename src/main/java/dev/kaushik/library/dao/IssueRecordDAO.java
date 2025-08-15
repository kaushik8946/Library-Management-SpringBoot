package dev.kaushik.library.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;

import dev.kaushik.library.model.IssueRecord;

public interface IssueRecordDAO {

	int addIssueRecord(IssueRecord issueRecord) throws DataAccessException;

	boolean markAsReturned(IssueRecord issueRecord) throws DataAccessException;
	
	List<IssueRecord> getIssuedRecords(IssueRecord criteria) throws DataAccessException;

	Optional<IssueRecord> getActiveIssueRecordByBookId(int bookId) throws DataAccessException;

}