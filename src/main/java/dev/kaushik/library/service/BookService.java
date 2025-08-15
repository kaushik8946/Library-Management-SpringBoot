package dev.kaushik.library.service;

import java.util.List;

import dev.kaushik.library.exception.LibraryException;
import dev.kaushik.library.model.Book;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface BookService {

    int addBook(@Valid @NotNull Book book) throws LibraryException;

    List<Book> findBooks(Book criteria) throws LibraryException;

    boolean updateBook(@Valid @NotNull Book book) throws LibraryException;

    boolean deleteBook(@NotNull @Positive Integer bookId) throws LibraryException;

    int deleteBooksBatch(@NotNull List<@NotNull @Positive Integer> bookIds) throws LibraryException;

    int updateBookAvailabilityBatch(@NotNull List<@NotNull @Positive Integer> bookIds) throws LibraryException;
}