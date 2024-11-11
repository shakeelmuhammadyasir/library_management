package com.example.library_management.controller;

import java.io.Serializable;

import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.view.BookView;

public class LibraryController implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient BookView bookView;
	private transient BookRepository bookRepository;

	public LibraryController(BookView bookView, BookRepository bookRepository) {
		this.bookView = bookView;
		this.bookRepository = bookRepository;
	}

	public void allBooks() {
		bookView.showAllBooks(bookRepository.findAll());
	}

	public void newBook(Book book) {
		Book existingBook = bookRepository.findBySerialNumber(book.getSerialNumber());
		if (existingBook != null) {
			bookView.showError("Already existing book with serial number " + book.getSerialNumber(), existingBook);
			return;
		}

		bookRepository.save(book);
		bookView.bookAdded(book);
	}

	public void deleteBook(Book book) {
		Book existingBook = bookRepository.findBySerialNumber(book.getSerialNumber());
		if (existingBook == null) {
			bookView.showErrorBookNotFound("No existing book with serial number " + book.getSerialNumber(), book);
			return;
		}

		bookRepository.delete(book.getSerialNumber());
		bookView.bookRemoved(book);
	}

	public void searchBook(String serialNumber) {
		Book book = bookRepository.findBySerialNumber(serialNumber);
		if (book == null) {
			bookView.showErrorBookNotFound("No existing book with serial number " + serialNumber);
			return;
		}

		bookView.showSearchedBooks(book);
	}
}
