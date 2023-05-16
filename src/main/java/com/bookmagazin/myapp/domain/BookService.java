package com.bookmagazin.myapp.domain;


import org.springframework.stereotype.Service;

/**
 * Service classes thrown exception if needed and Repository classes do not
 */
@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Iterable<Book> viewBookList(){
        return bookRepository.findAll();
    }

    public Book viewBookDetails(String isbn) {
        return this.bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public Book addBookToCatalog(Book book) {
        if(this.bookRepository.existsByIsbn(book.isbn())) {
            throw new BookAlreadyExistException(book.isbn());
        }
        return this.bookRepository.save(book);
    }

    public void removeBookFromCatalog(String isbn) {
        this.bookRepository.deleteByIsbn(isbn);
    }

    public Book editBookDetails(String isbn, Book book) {
        return this.bookRepository.findByIsbn(isbn).map(existingBook -> {
            var newBook = new Book(existingBook.isbn(),
                    book.title(),
                    book.author(),
                    book.price()
            );
            return this.bookRepository.save(newBook);
        }).orElseGet(()-> addBookToCatalog(book));
    }
}

