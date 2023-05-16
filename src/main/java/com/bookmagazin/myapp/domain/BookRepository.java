package com.bookmagazin.myapp.domain;

import java.util.Optional;

public interface BookRepository {
    public Iterable<Book> findAll();
    public Optional<Book> findByIsbn(String isbn);
    public boolean existsByIsbn(String isbn);
    public Book save(Book book);
    public void deleteByIsbn(String isbn);

}