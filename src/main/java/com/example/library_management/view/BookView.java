package com.example.library_management.view;

import java.util.List;

import com.example.library_management.model.Book;

public interface BookView {

	public void showAllBooks(List<Book> books);

    public void bookAdded(Book book);

    public void bookRemoved(Book book);
    
    public void showSearchedBooks(Book books);

    public void showError(String message, Book book);

    public void showErrorBookNotFound(String message);

    public void showErrorBookNotFound(String message, Book book);

}
