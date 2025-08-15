package dev.kaushik.library.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
	            .addValue("createdBy", book.getCreatedBy());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"bookId"});
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;
	}

	@Override
	public boolean updateBook(Book book) throws DataAccessException {
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
        return rowsAffected > 0;

	}

	@Override
	public int deleteBooksInBatch(Integer... bookIds) throws DataAccessException {
		if (bookIds == null || bookIds.length == 0) {
            return 0;
        }
        List<Integer> bookIdList = Arrays.asList(bookIds);
        String sql = "DELETE FROM books WHERE bookId IN (:bookIds)";
        MapSqlParameterSource params = new MapSqlParameterSource("bookIds", bookIdList);
        return namedParameterJdbcTemplate.update(sql, params);
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

	    String sql = sqlBuilder.toString();
	    return namedParameterJdbcTemplate.getJdbcOperations().query(sql, bookRowMapper);
	}

	@Override
	public int updateBookAvailabilityBatch(List<Integer> bookIds) throws DataAccessException {
		if (bookIds == null || bookIds.size() == 0) {
            return 0;
        }

        String sql = "UPDATE books " +
                "SET availability = " +
                "CASE availability " +
                "WHEN '" + BookAvailability.AVAILABLE.getCode() + "' THEN '" + BookAvailability.ISSUED.getCode() + "' " +
                "WHEN '" + BookAvailability.ISSUED.getCode() + "' THEN '" + BookAvailability.AVAILABLE.getCode() + "' " +
                "END " +
                "WHERE bookId IN (:bookIds)";
        MapSqlParameterSource params = new MapSqlParameterSource("bookIds", bookIds);
        return namedParameterJdbcTemplate.update(sql, params);
	}

}
