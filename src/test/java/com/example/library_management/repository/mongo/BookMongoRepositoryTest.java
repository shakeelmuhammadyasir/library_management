package com.example.library_management.repository.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.library_management.model.Book;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class BookMongoRepositoryTest {

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

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockClient.getDatabase(anyString())).thenReturn(mockDatabase);
        when(mockDatabase.getCollection(anyString())).thenReturn(mockCollection);
        repository = new BookMongoRepository(mockClient, "testDatabase", "testCollection");
    }

    @Test
    public void testFindAllBooks() {
        // Given
        when(mockCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.spliterator()).thenReturn(Collections.<Document>emptyList().spliterator());

        // When
        List<Book> books = repository.findAll();

        // Then
        assertNotNull("The list of books should not be null", books);
        assertTrue("The list of books should be empty", books.isEmpty());
    }
    
    
    @Test
    public void testFindAllBooks_WithData() {
        // Given
        Document doc1 = new Document("id", 1)
                .append("serialNumber", "SN001")
                .append("name", "Java Programming")
                .append("authorName", "Author A")
                .append("genre", "Programming")
                .append("isAvailable", true);
        
        Document doc2 = new Document("id", 2)
                .append("serialNumber", "SN002")
                .append("name", "Data Structures")
                .append("authorName", "Author B")
                .append("genre", "Computer Science")
                .append("isAvailable", false);

        when(mockCollection.find()).thenReturn(mockFindIterable);
        when(mockFindIterable.spliterator()).thenReturn(Arrays.asList(doc1, doc2).spliterator());

        // When
        List<Book> books = repository.findAll();

        // Then
        assertNotNull("The list of books should not be null", books);
        assertEquals("The list of books should contain 2 books", 2, books.size());

        Book book1 = books.get(0);
        assertEquals("The serial number should be 'SN001'", "SN001", book1.getSerialNumber());
        assertEquals("The book name should be 'Java Programming'", "Java Programming", book1.getName());
        assertEquals("The author name should be 'Author A'", "Author A", book1.getAuthorName());
        assertEquals("The genre should be 'Programming'", "Programming", book1.getGenre());
        assertTrue("The book should be available", book1.isAvailable());

        Book book2 = books.get(1);
        assertEquals("The serial number should be 'SN002'", "SN002", book2.getSerialNumber());
        assertEquals("The book name should be 'Data Structures'", "Data Structures", book2.getName());
        assertEquals("The author name should be 'Author B'", "Author B", book2.getAuthorName());
        assertEquals("The genre should be 'Computer Science'", "Computer Science", book2.getGenre());
        assertFalse("The book should not be available", book2.isAvailable());
    }

    @Test
    public void testFindBySerialNumber_Found() {
        // Given
        Document doc = new Document("id", 1)
                .append("serialNumber", "SN001")
                .append("name", "Java Programming")
                .append("authorName", "Author A")
                .append("genre", "Programming")
                .append("isAvailable", true);

        when(mockCollection.find(Filters.eq("serialNumber", "SN001"))).thenReturn(mockFindIterable);
        when(mockFindIterable.spliterator()).thenReturn(Arrays.asList(doc).spliterator());

        // When
        Book book = repository.findBySerialNumber("SN001");

        // Then
        assertNotNull("The book should not be null", book);
        assertEquals("The serial number should be 'SN001'", "SN001", book.getSerialNumber());
        assertEquals("The book name should be 'Java Programming'", "Java Programming", book.getName());
        assertEquals("The author name should be 'Author A'", "Author A", book.getAuthorName());
        assertEquals("The genre should be 'Programming'", "Programming", book.getGenre());
        assertTrue("The book should be available", book.isAvailable());
    }

    @Test
    public void testFindBySerialNumber_NotFound() {
        // Given
        when(mockCollection.find(Filters.eq("serialNumber", "SN002"))).thenReturn(mockFindIterable);
        when(mockFindIterable.spliterator()).thenReturn(Collections.<Document>emptyList().spliterator());

        // When
        Book book = repository.findBySerialNumber("SN002");

        // Then
        assertNull("The book should be null", book);
    }

    @Test
    public void testSaveBook() {
        // Given
        Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);

        // When
        repository.save(book);

        // Then
        verify(mockCollection, times(1)).insertOne(any(Document.class));
    }

    @Test
    public void testDeleteBook() {
        // Given
        String serialNumber = "SN001";

        // When
        repository.delete(serialNumber);

        // Then
        verify(mockCollection, times(1)).deleteOne(Filters.eq("serialNumber", serialNumber));
    }
}
