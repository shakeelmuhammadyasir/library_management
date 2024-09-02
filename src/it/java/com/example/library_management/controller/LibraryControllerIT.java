package com.example.library_management.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testcontainers.containers.MongoDBContainer;

import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.mongo.BookMongoRepository;
import com.example.library_management.view.BookView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

@RunWith(MockitoJUnitRunner.class)
public class LibraryControllerIT {

    @Mock
    private BookView bookView;

    private BookRepository bookRepository;

    private LibraryController libraryController;

    private MongoClient client;

    private static final String LIBRARY_DB_NAME = "library";
    private static final String BOOK_COLLECTION_NAME = "book";
    
    @ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    @Before
    public void setup() {
        client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(27017)));
        bookRepository = new BookMongoRepository(client, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME);
        MongoDatabase database = client.getDatabase(LIBRARY_DB_NAME);
        database.drop();
        libraryController = new LibraryController(bookView, bookRepository);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testAllBooks() {
        // Given
        Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
        bookRepository.save(book);
        
        // When
        libraryController.allBooks();
        
        // Then
        verify(bookView).showAllBooks(asList(book));
    }

    @Test
    public void testNewBook() {
        // Given
        Book book = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
        
        // When
        libraryController.newBook(book);
        
        // Then
        verify(bookView).bookAdded(book);
    }

    @Test
    public void testDeleteBook() {
        // Given
        Book bookToDelete = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
        bookRepository.save(bookToDelete);
        
        // When
        libraryController.deleteBook(bookToDelete);
        
        // Then
        verify(bookView).bookRemoved(bookToDelete);
    }

    @Test
    public void testSearchBook() {
        // Given
        Book bookToSearch = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
        bookRepository.save(bookToSearch);
        
        // When
        libraryController.searchBook("SN001");
        
        // Then
        verify(bookView).showSearchedBooks(bookToSearch);
    }
}
