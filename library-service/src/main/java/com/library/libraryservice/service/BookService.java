package com.library.libraryservice.service;

import com.library.libraryservice.dto.BookRequest;
import com.library.libraryservice.dto.BookResponse;
import com.library.libraryservice.entity.Book;
import com.library.libraryservice.exception.BookNotFoundException;
import com.library.libraryservice.exception.DuplicateIsbnException;
import com.library.libraryservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for the book catalog: CRUD, search, and copy-count management.
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateIsbnException("A book with ISBN " + request.getIsbn() + " already exists");
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        book.setTotalCopies(request.getTotalCopies());
        // A freshly added book starts with all of its copies available.
        book.setAvailableCopies(request.getTotalCopies());

        return BookResponse.fromEntity(bookRepository.save(book));
    }

    public BookResponse getBookById(Long bookId) {
        return BookResponse.fromEntity(findBookOrThrow(bookId));
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(BookResponse::fromEntity).toList();
    }

    public List<BookResponse> searchBooks(String keyword) {
        return bookRepository.search(keyword).stream().map(BookResponse::fromEntity).toList();
    }

    @Transactional
    public BookResponse updateBook(Long bookId, BookRequest request) {
        Book book = findBookOrThrow(bookId);

        if (bookRepository.existsByIsbnAndBookIdNot(request.getIsbn(), bookId)) {
            throw new DuplicateIsbnException("A book with ISBN " + request.getIsbn() + " already exists");
        }

        // Keep the number of currently-reserved/issued copies constant while the total changes,
        // e.g. if 2 of 5 copies are out and the librarian raises totalCopies to 7, available becomes 4.
        int copiesInUse = book.getTotalCopies() - book.getAvailableCopies();
        int newAvailable = Math.max(0, request.getTotalCopies() - copiesInUse);

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(newAvailable);

        return BookResponse.fromEntity(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = findBookOrThrow(bookId);
        bookRepository.delete(book);
    }

    private Book findBookOrThrow(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));
    }
}
