package com.example.library_management.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.library_management.controller.LibraryController;
import com.example.library_management.model.Book;
import com.example.library_management.repository.mongo.BookMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class BookSwingViewIT extends AssertJSwingJUnitTestCase {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient mongoClient;
	private FrameFixture window;
	private BookSwingView bookSwingView;
	private LibraryController libraryController;
	private BookMongoRepository bookRepository;

	private static final String LIBRARY_DB_NAME = "library";
	private static final String BOOK_COLLECTION_NAME = "book";

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		bookRepository = new BookMongoRepository(mongoClient, LIBRARY_DB_NAME, BOOK_COLLECTION_NAME);
		for (Book book : bookRepository.findAll()) {
			bookRepository.delete(book.getSerialNumber());
		}

		GuiActionRunner.execute(() -> {
			bookSwingView = new BookSwingView();
			libraryController = new LibraryController(bookSwingView, bookRepository);
			bookSwingView.setLibraryController(libraryController);
			return bookSwingView;
		});
		window = new FrameFixture(robot(), bookSwingView);
		window.show();
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void testAllBooks() {
		// Given
		Book book1 = new Book(1, "SN001", "Java Programming", "Author A", "Programming", true);
		Book book2 = new Book(2, "SN002", "Python Programming", "Author B", "Programming", false);

		GuiActionRunner.execute(() -> {

			libraryController.allBooks();
			bookSwingView.bookAdded(book1);
			bookSwingView.bookAdded(book2);
		});

		// Then
		String[][] actualContents = window.table().contents();
		String[][] expectedContents = { { "1", "SN001", "Java Programming", "Author A", "Programming", "true" },
				{ "2", "SN002", "Python Programming", "Author B", "Programming", "false" } };
		assertThat(actualContents).isEqualTo(expectedContents);

		String[] comboBoxContents = window.comboBox("Serial Number ComboBox").contents();
		List<String> actualComboBoxContents = Arrays.asList(comboBoxContents);
		List<String> expectedComboBoxContents = Arrays.asList("SN001", "SN002");
		assertThat(actualComboBoxContents).isEqualTo(expectedComboBoxContents);
	}

	@Test
	@GUITest
	public void testAddBookSuccess() {
		// Given
		window.textBox("ID").enterText("3");
		window.textBox("Serial Number").enterText("SN003");
		window.textBox("Name").enterText("JavaScript Programming");
		window.textBox("Author Name").enterText("Author C");
		window.textBox("Genre").enterText("Programming");
		window.checkBox("Available").check();

		// When
		window.button(JButtonMatcher.withText("Add Book")).click();

		// Then
		String[][] actualContents = window.table().contents();
		String[][] expectedContents = { { "3", "SN003", "JavaScript Programming", "Author C", "Programming", "true" } };
		assertThat(actualContents).isEqualTo(expectedContents);

		String[] comboBoxContents = window.comboBox("Serial Number ComboBox").contents();
		List<String> actualComboBoxContents = Arrays.asList(comboBoxContents);
		List<String> expectedComboBoxContents = Arrays.asList("SN003");

		// assert
		assertThat(actualComboBoxContents).isEqualTo(expectedComboBoxContents);
	}

	@Test
	@GUITest
	public void testDeleteBookSuccess() {
		// Given
		Book book = new Book(1, "SN001", "Book to Delete", "Author F", "Science", true);
		bookRepository.save(book);
		GuiActionRunner.execute(() -> libraryController.allBooks());
		window.table().selectRows(0);

		// When
		window.button(JButtonMatcher.withText("Delete Selected Book")).click();

		// Then
		assertThat(window.table().contents()).isEmpty();
		assertThat(window.comboBox("Serial Number ComboBox").contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testSearchBookSuccess() {
		// Given
		Book book = new Book(1, "SN001", "Search Book", "Author G", "Fiction", true);
		bookRepository.save(book);
		GuiActionRunner.execute(() -> libraryController.allBooks());
		window.comboBox("Serial Number ComboBox").selectItem("SN001");

		// When
		window.button(JButtonMatcher.withText("Search By Serial number")).click();

		// Then
		String[][] actualContents = window.table().contents();
		String[][] expectedContents = { { "1", "SN001", "Search Book", "Author G", "Fiction", "true" } };
		assertThat(actualContents).isEqualTo(expectedContents);

	}

}
