package dev.kaushik.library.dao.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.kaushik.library.dao.BookDAO;
import dev.kaushik.library.model.Book;
import dev.kaushik.library.model.enums.*;

@Repository
public class BookDAOImpl implements BookDAO {
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public BookDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	
	private final RowMapper<Book> bookRowMapper = (rs, rowNum) ->
	    Book.builder()
	        .bookId(rs.getInt("bookId"))
	        .title(rs.getString("title"))
	        .author(rs.getString("author"))
	        .category(BookCategory.fromDisplayName(rs.getString("category")))
	        .status(BookStatus.fromCode(rs.getString("status")))
	        .availability(BookAvailability.fromCode(rs.getString("availability")))
	        .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
	        .createdBy(rs.getString("created_by"))
	        .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
	        .updatedBy(rs.getString("updated_by"))
	        .build();

	@Override
	public int addBook(Book book) throws DataAccessException {
		String sql = "INSERT INTO books (title,author,category,status,availability,created_by) " +
                "VALUES (:title,:author,:category,:status,:availability,:createdBy)";
		MapSqlParameterSource params = new MapSqlParameterSource()
	            .addValue("title", book.getTitle())
	            .addValue("author", book.getAuthor())
	            .addValue("category", book.getCategory().getDisplayName())
	            .addValue("status", book.getStatus().getCode())
	            .addValue("availability", book.getAvailability().getCode())
	            .addValue("createdBy", "ADMIN");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"bookId"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;
	}

	@Override
	@Transactional
	public boolean updateBook(Book book) throws DataAccessException {
		List<Book> existingBooks = findBooks(Book.builder().bookId(book.getBookId()).build());
	    
	    if (existingBooks.isEmpty()) {
	        System.out.println("No book found with ID: " + book.getBookId() + " to update");
	        return false;
	    }
	    Book oldBook = existingBooks.get(0);
	    
		String sql = "UPDATE books SET title=:title, author=:author, category=:category, " +
                "status=:status, availability=:availability, updated_by=:updatedBy, updated_at=CURRENT_TIMESTAMP " +
                "WHERE bookId=:bookId";
		MapSqlParameterSource params = new MapSqlParameterSource()
	            .addValue("title", book.getTitle())
	            .addValue("author", book.getAuthor())
	            .addValue("category", book.getCategory().getDisplayName())
	            .addValue("status", book.getStatus().getCode())
	            .addValue("availability", book.getAvailability().getCode())
	            .addValue("updatedBy", book.getUpdatedBy())
	            .addValue("bookId", book.getBookId());
        int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
        if(rowsAffected>0) {
        	logBookChange(oldBook);
        	return true;
        }
        return false;
	}

	@Override
	public int deleteBooksInBatch(Integer... bookIds) throws DataAccessException {
		if (bookIds == null || bookIds.length == 0) {
			return 0;
		}
		List<Integer> bookIdList = Arrays.asList(bookIds);
		List<Book> booksToLog = findBooks(Book.builder().build());
		booksToLog = booksToLog.stream()
				.filter(book -> bookIdList.contains(book.getBookId()))
				.collect(Collectors.toList());

		String sql = "DELETE FROM books WHERE bookId IN (:bookIds)";
		MapSqlParameterSource params = new MapSqlParameterSource("bookIds", bookIdList);
		int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
		if (rowsAffected > 0) {
			booksToLog.forEach(book -> logBookChange(book));
		}
		return rowsAffected;
	}

	@Override
	public List<Book> findBooks(Book criteria) throws DataAccessException {
	    StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM books WHERE 1=1");
	    MapSqlParameterSource params= new MapSqlParameterSource();
	    if (criteria != null) {
	        if (criteria.getBookId() != null) {
	            sqlBuilder.append(" AND bookId = :bookId");
	            params.addValue("bookId", criteria.getBookId());
	        }
	        if (criteria.getTitle() != null && !criteria.getTitle().trim().isEmpty()) {
	            sqlBuilder.append(" AND title LIKE :title");
	            params.addValue("title", "%" + criteria.getTitle().trim() + "%");
	        }
	        if (criteria.getAuthor() != null && !criteria.getAuthor().trim().isEmpty()) {
	            sqlBuilder.append(" AND author LIKE :author");
	            params.addValue("author", "%" + criteria.getAuthor().trim() + "%");
	        }
	        if (criteria.getCategory() != null) {
	            sqlBuilder.append(" AND category = :category");
	            params.addValue("category", criteria.getCategory().getDisplayName());
	        }
	        if (criteria.getStatus() != null) {
	            sqlBuilder.append(" AND status = :status");
	            params.addValue("status", criteria.getStatus().getCode());
	        }
	        if (criteria.getAvailability() != null) {
	            sqlBuilder.append(" AND availability = :availability");
	            params.addValue("availability", criteria.getAvailability().getCode());
	        }
	    }

	    return namedParameterJdbcTemplate.query(sqlBuilder.toString(), params, bookRowMapper);
	}
	@Override
	public int updateBookAvailabilityBatch(List<Integer> bookIds) throws DataAccessException {
		if (bookIds == null || bookIds.size() == 0) {
            return 0;
        }
		List<Book> booksToLog = findBooks(Book.builder().build());
		booksToLog = booksToLog.stream()
				.filter(book -> bookIds.contains(book.getBookId()))
				.collect(Collectors.toList());
        String sql = "UPDATE books " +
                "SET availability = " +
                "CASE availability " +
                "WHEN '" + BookAvailability.AVAILABLE.getCode() + "' THEN '" + BookAvailability.ISSUED.getCode() + "' " +
                "WHEN '" + BookAvailability.ISSUED.getCode() + "' THEN '" + BookAvailability.AVAILABLE.getCode() + "' " +
                "END " +
                "WHERE bookId IN (:bookIds)";
        MapSqlParameterSource params = new MapSqlParameterSource("bookIds", bookIds);
        int rowsAffected= namedParameterJdbcTemplate.update(sql, params);
        if (rowsAffected > 0) {
			booksToLog.forEach(book -> logBookChange(book));
		}
		return rowsAffected;
	}
	
	private void logBookChange(Book oldBook) throws DataAccessException {
	    String sql = "INSERT INTO books_log (BookId,Title,Author,Category,Status,Availability," +
	                 "original_created_at,original_created_by,original_updated_at,original_updated_by,LogDate) " +
	                 "VALUES (:bookId,:title,:author,:category,:status,:availability, " +
	                 ":createdAt,:createdBy,:updatedAt,:updatedBy,CURRENT_TIMESTAMP)";
	    
	    MapSqlParameterSource params = new MapSqlParameterSource()
	        .addValue("bookId", oldBook.getBookId())
	        .addValue("title", oldBook.getTitle())
	        .addValue("author", oldBook.getAuthor())
	        .addValue("category", oldBook.getCategory().getDisplayName())
	        .addValue("status", oldBook.getStatus().getCode())
	        .addValue("availability", oldBook.getAvailability().getCode())
	        .addValue("createdAt", oldBook.getCreatedAt())
	        .addValue("createdBy", oldBook.getCreatedBy())
	        .addValue("updatedAt", oldBook.getUpdatedAt())
	        .addValue("updatedBy", oldBook.getUpdatedBy());

	    namedParameterJdbcTemplate.update(sql, params);
	}
}
