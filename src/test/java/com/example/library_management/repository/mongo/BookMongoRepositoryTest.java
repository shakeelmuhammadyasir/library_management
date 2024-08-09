package com.example.library_management.repository.mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.library_management.model.Book;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

class BookMongoRepositoryTest {

	@Mock
	private MongoClient mockClient;

	@Mock
	private MongoDatabase mockDatabase;

	@Mock
	private MongoCollection<Document> mockCollection;

	@Mock
	private FindIterable<Document> mockFindIterable;

	@Mock
	private MongoCursor<Document> mockCursor;

	private BookMongoRepository repository;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		when(mockClient.getDatabase(anyString())).thenReturn(mockDatabase);
		when(mockDatabase.getCollection(anyString())).thenReturn(mockCollection);
		repository = new BookMongoRepository(mockClient, "testDatabase", "testCollection");
	}

	@Test
	void testFindAllBooks() {
		// Given
		when(mockCollection.find()).thenReturn(mockFindIterable);
		when(mockFindIterable.spliterator()).thenReturn(Collections.<Document>emptyList().spliterator());

		// When
		List<Book> books = repository.findAll();

		// Then
		assertNotNull(books, "The list of books should not be null");
		assertTrue(books.isEmpty(), "The list of books should be empty");
	}

	@Test
	void testFindByName() {
		// Given
		Document doc = new Document("id", 1).append("serialNumber", "SN001").append("name", "Java Programming")
				.append("authorName", "Author A").append("genre", "Programming").append("isAvailable", true);

		when(mockCollection.find()).thenReturn(mockFindIterable);
		when(mockFindIterable.spliterator()).thenReturn(Arrays.asList(doc).spliterator());

		// When
		List<Book> books = repository.findByName("Java");

		// Then
		assertNotNull(books, "The list of books should not be null");
		assertFalse(books.isEmpty(), "The list of books should not be empty");
		assertEquals(1, books.size(), "There should be one book in the list");
		assertEquals("Java Programming", books.get(0).getName(), "The book name should be 'Java Programming'");
	}

	@Test
	void testFindBySerialNumber_Found() {
		// Given
		Document doc = new Document("id", 1).append("serialNumber", "SN001").append("name", "Java Programming")
				.append("authorName", "Author A").append("genre", "Programming").append("isAvailable", true);

		when(mockCollection.find(Filters.eq("serialNumber", "SN001"))).thenReturn(mockFindIterable);
		when(mockFindIterable.spliterator()).thenReturn(Arrays.asList(doc).spliterator());

		// When
		Book book = repository.findBySerialNumber("SN001");

		// Then
		assertNotNull(book, "The book should not be null");
		assertEquals("SN001", book.getSerialNumber(), "The serial number should be 'SN001'");
		assertEquals("Java Programming", book.getName(), "The book name should be 'Java Programming'");
		assertEquals("Author A", book.getAuthorName(), "The author name should be 'Author A'");
		assertEquals("Programming", book.getGenre(), "The genre should be 'Programming'");
		assertTrue(book.isAvailable(), "The book should be available");
	}

	@Test
	void testFindBySerialNumber_NotFound() {
		// Given
		when(mockCollection.find(Filters.eq("serialNumber", "SN002"))).thenReturn(mockFindIterable);
		when(mockFindIterable.spliterator()).thenReturn(Collections.<Document>emptyList().spliterator());

		// When
		Book book = repository.findBySerialNumber("SN002");

		// Then
		assertNull(book, "The book should be null");
	}

	@Test
	void testSaveBook() {
		// Given
		Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);

		// When
		repository.save(book);

		// Then
		verify(mockCollection, times(1)).insertOne(any(Document.class));
	}

	@Test
	void testDeleteBook() {
		// Given
		String serialNumber = "SN001";

		// When
		repository.delete(serialNumber);

		// Then
		verify(mockCollection, times(1)).deleteOne(eq(Filters.eq("serialNumber", serialNumber)));
	}
}
