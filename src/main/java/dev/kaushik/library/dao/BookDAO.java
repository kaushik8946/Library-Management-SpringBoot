package dev.kaushik.library.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import dev.kaushik.library.model.Book;

public interface BookDAO {

    int addBook(Book book) throws DataAccessException;

    boolean updateBook(Book book) throws DataAccessException;

    int deleteBooksInBatch(Integer... bookIds) throws DataAccessException;

    List<Book> findBooks(Book criteria) throws DataAccessException;
    
    int updateBookAvailabilityBatch(List<Integer> bookIds) throws DataAccessException;
} 