package dev.kaushik.library.controller;

import dev.kaushik.library.model.Book;
import dev.kaushik.library.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/addBook")
    public ResponseEntity<Integer> addBook(@Valid @RequestBody Book book) {
        Integer bookId = bookService.addBook(book);
        return new ResponseEntity<>(bookId, HttpStatus.CREATED);
    }

    @PutMapping("/updateBook")
    public ResponseEntity<Boolean> updateBook(@Valid @RequestBody Book book) {
        boolean updated = bookService.updateBook(book);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/deleteBook/{bookId}")
    public ResponseEntity<Boolean> deleteBook(@PathVariable @NotNull @Positive Integer bookId) {
        boolean deleted = bookService.deleteBook(bookId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @PostMapping("/batchDeleteBooks")
    public ResponseEntity<Integer> deleteBooksBatch(@RequestBody @NotNull List<@NotNull @Positive Integer> bookIds) {
        Integer deletedCount = bookService.deleteBooksBatch(bookIds);
        return new ResponseEntity<>(deletedCount, HttpStatus.OK);
    }

    @PostMapping("/updateBookAvailabilityBatch")
    public ResponseEntity<Integer> updateBookAvailabilityBatch(@RequestBody @NotNull List<@NotNull @Positive Integer> bookIds) {
        Integer updatedCount = bookService.updateBookAvailabilityBatch(bookIds);
        return new ResponseEntity<>(updatedCount, HttpStatus.OK);
    }

    @GetMapping("/viewBooks")
    public ResponseEntity<List<Book>> viewBooks(@RequestBody(required = false) Book criteria) {
        List<Book> books = bookService.findBooks(criteria);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}