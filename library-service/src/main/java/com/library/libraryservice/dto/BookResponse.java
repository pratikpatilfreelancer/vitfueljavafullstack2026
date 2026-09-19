package com.library.libraryservice.dto;

import com.library.libraryservice.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookResponse {

    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int totalCopies;
    private int availableCopies;

    public static BookResponse fromEntity(Book book) {
        return new BookResponse(book.getBookId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getCategory(), book.getTotalCopies(), book.getAvailableCopies());
    }
}
