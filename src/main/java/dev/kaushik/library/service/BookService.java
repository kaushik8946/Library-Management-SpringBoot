package dev.kaushik.library.service;

import java.util.List;

import dev.kaushik.library.model.Book;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface BookService {

    int addBook(@Valid @NotNull Book book);

    List<Book> findBooks(Book criteria);

    boolean updateBook(@Valid @NotNull Book book);

    boolean deleteBook(@NotNull @Positive Integer bookId);

    int deleteBooksBatch(@NotNull List<@NotNull @Positive Integer> bookIds);

    int updateBookAvailabilityBatch(@NotNull List<@NotNull @Positive Integer> bookIds);
}