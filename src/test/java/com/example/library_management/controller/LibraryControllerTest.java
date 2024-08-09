package com.example.library_management.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.view.BookView;

class LibraryControllerTest {

	@Mock
	private BookView mockBookView;

	@Mock
	private BookRepository mockBookRepository;

	private LibraryController controller;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		controller = new LibraryController(mockBookView, mockBookRepository);
	}

	@Test
	void testAllBooks() {
		// Given
		List<Book> books = Collections
				.singletonList(new Book(1, "SN001", "Java Programming", "Author A", "Programming", true));
		when(mockBookRepository.findAll()).thenReturn(books);

		// When
		controller.allBooks();

		// Then
		verify(mockBookView).showAllBooks(books);
	}

	@Test
	void testNewBook_BookAlreadyExists() {
		// Given
		Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
		when(mockBookRepository.findBySerialNumber("SN001")).thenReturn(book);

		// When
		controller.newBook(book);

		// Then
		verify(mockBookView).showError("Already existing book with serial number SN001", book);
		verify(mockBookRepository, never()).save(book);
	}

	@Test
	void testNewBook_Success() {
		// Given
		Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
		when(mockBookRepository.findBySerialNumber("SN001")).thenReturn(null);

		// When
		controller.newBook(book);

		// Then
		verify(mockBookRepository).save(book);
		verify(mockBookView).bookAdded(book);
	}

	@Test
	void testDeleteBook_BookNotFound() {
		// Given
		Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
		when(mockBookRepository.findBySerialNumber("SN001")).thenReturn(null);

		// When
		controller.deleteBook(book);

		// Then
		verify(mockBookView).showErrorBookNotFound("No existing book with serial number SN001", book);
		verify(mockBookRepository, never()).delete("SN001");
	}

	@Test
	void testDeleteBook_Success() {
		// Given
		Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
		when(mockBookRepository.findBySerialNumber("SN001")).thenReturn(book);

		// When
		controller.deleteBook(book);

		// Then
		verify(mockBookRepository).delete("SN001");
		verify(mockBookView).bookRemoved(book);
	}

	@Test
	void testSearchBook_Found() {
		// Given
		Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
		when(mockBookRepository.findBySerialNumber("SN001")).thenReturn(book);

		// When
		controller.searchBook("SN001");

		// Then
		verify(mockBookView).showSearchedBooks(book);
	}

	@Test
	void testSearchBook_NotFound() {
		// Given
		when(mockBookRepository.findBySerialNumber("SN001")).thenReturn(null);

		// When
		controller.searchBook("SN001");

		// Then
		verify(mockBookView).showErrorBookNotFound("No existing book with serial number SN001");
	}
}
