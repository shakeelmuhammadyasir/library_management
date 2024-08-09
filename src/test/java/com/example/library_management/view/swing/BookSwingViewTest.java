package com.example.library_management.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;

import org.assertj.core.api.Assertions;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JCheckBoxFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.library_management.controller.LibraryController;
import com.example.library_management.model.Book;

class BookSwingViewTest {
	private FrameFixture window;
	private BookSwingView bookSwingView;
	private LibraryController libraryControllerMock;

	@BeforeEach
	void setUp() {
		libraryControllerMock = Mockito.mock(LibraryController.class);
		bookSwingView = new BookSwingView();
		bookSwingView.setLibraryController(libraryControllerMock);
		window = new FrameFixture(bookSwingView);
		window.show();
	}

	@AfterEach
	void tearDown() {
		window.cleanUp();
	}

	@Test
	void testAddBookButtonWithValidData() {
		// Given
		JTextComponentFixture idTextField = window.textBox("ID");
		JTextComponentFixture serialNumberTextField = window.textBox("Serial Number");
		JTextComponentFixture nameTextField = window.textBox("Name");
		JTextComponentFixture authorNameTextField = window.textBox("Author Name");
		JTextComponentFixture genreTextField = window.textBox("Genre");
		JButtonFixture addButton = window.button("Add Book");
		JCheckBoxFixture availableCheckBox = window.checkBox("Available");

		// When
		idTextField.enterText("1");
		serialNumberTextField.enterText("SN123");
		nameTextField.enterText("Book Name");
		authorNameTextField.enterText("Author");
		genreTextField.enterText("Genre");
		availableCheckBox.check();
		addButton.click();

		// Then
		Mockito.verify(libraryControllerMock)
				.newBook(Mockito.argThat(book -> book.getId() == 1 && "SN123".equals(book.getSerialNumber())
						&& "Book Name".equals(book.getName()) && "Author".equals(book.getAuthorName())
						&& "Genre".equals(book.getGenre()) && book.isAvailable()));
	}

	@Test
	void testAddBookButtonWithInvalidID() {
		// Given
		JTextComponentFixture idTextField = window.textBox("ID");
		JTextComponentFixture serialNumberTextField = window.textBox("Serial Number");
		JTextComponentFixture nameTextField = window.textBox("Name");
		JTextComponentFixture authorNameTextField = window.textBox("Author Name");
		JTextComponentFixture genreTextField = window.textBox("Genre");
		JButtonFixture addButton = window.button("Add Book");
		JCheckBoxFixture availableCheckBox = window.checkBox("Available");

		// When
		idTextField.enterText("invalidID");
		serialNumberTextField.enterText("SN123");
		nameTextField.enterText("Book Name");
		authorNameTextField.enterText("Author");
		genreTextField.enterText("Genre");
		availableCheckBox.check();
		addButton.click();

		// Then
		JLabel errorLabel = bookSwingView.getErrorLabel();
		assertThat(errorLabel.getText()).isEqualTo("ID must be an integer");
		Mockito.verify(libraryControllerMock, Mockito.never()).newBook(Mockito.any(Book.class));
	}

	@Test
	void testDeleteBookButtonWithBookSelected() {
		// Given
		Book book = new Book(1, "SN123", "Book Name", "Author", "Genre", true);
		bookSwingView.bookAdded(book);
		JTableFixture table = window.table();
		table.selectRows(0); // Select the first row

		// When
		window.button("Delete Selected Book").click();

		// Then
		Mockito.verify(libraryControllerMock).deleteBook(Mockito.eq(book));
	}

	@Test
	void testDeleteBookButtonWithNoBookSelected() {
		// Given
		JTableFixture table = window.table();
		table.target().clearSelection();

		// When
		window.button("Delete Selected Book").click();

		// Then
		JLabel errorLabel = bookSwingView.getErrorLabel();
		assertThat(errorLabel.getText()).isEqualTo("Please select a book to delete");
		Mockito.verify(libraryControllerMock, Mockito.never()).deleteBook(Mockito.any(Book.class));
	}

	@Test
	void testSearchBookButton() {
		// Given
		Book book = new Book(1, "SN123", "Book Name", "Author", "Genre", true);
		bookSwingView.bookAdded(book);
		JComboBoxFixture serialNumberComboBox = window.comboBox("Serial Number ComboBox");

		// When
		String[] comboBoxContents = serialNumberComboBox.contents();
		List<String> comboBoxContentsList = Arrays.asList(comboBoxContents);
		assertTrue(comboBoxContentsList.contains("SN123"), "Item SN123 should be present in JComboBox");
		serialNumberComboBox.selectItem("SN123");
		window.button("Search").click();

		// Then
		Mockito.verify(libraryControllerMock).searchBook(Mockito.eq("SN123"));
	}

	@Test
	void testSearchBookButtonWithNoSelection() {
		// Given
		JComboBoxFixture serialNumberComboBox = window.comboBox("Serial Number ComboBox");
		serialNumberComboBox.clearSelection();

		// When
		window.button("Search").click();

		// Then
		JLabel errorLabel = bookSwingView.getErrorLabel();
		assertThat(errorLabel.getText()).isEqualTo("Please select a serial number from the drop-down");
		Mockito.verify(libraryControllerMock, Mockito.never()).searchBook(Mockito.anyString());
	}

	@Test
	void testClearButton() {
		// Given
		JTextComponentFixture idTextField = window.textBox("ID");
		JTextComponentFixture serialNumberTextField = window.textBox("Serial Number");
		JTextComponentFixture nameTextField = window.textBox("Name");
		JTextComponentFixture authorNameTextField = window.textBox("Author Name");
		JTextComponentFixture genreTextField = window.textBox("Genre");
		JButtonFixture clearButton = window.button("Clear Fields");
		JCheckBoxFixture availableCheckBox = window.checkBox("Available");

		idTextField.enterText("1");
		serialNumberTextField.enterText("SN123");
		nameTextField.enterText("Book Name");
		authorNameTextField.enterText("Author");
		genreTextField.enterText("Genre");
		availableCheckBox.check();

		// When
		clearButton.click();

		// Then
		Assertions.assertThat(idTextField.text()).isEmpty();
		Assertions.assertThat(serialNumberTextField.text()).isEmpty();
		Assertions.assertThat(nameTextField.text()).isEmpty();
		Assertions.assertThat(authorNameTextField.text()).isEmpty();
		Assertions.assertThat(genreTextField.text()).isEmpty();
		availableCheckBox.requireNotSelected();
	}

	@Test
	void testShowAllBooks() {
		// Given
		List<Book> books = Arrays.asList(new Book(1, "SN123", "Book 1", "Author 1", "Genre 1", true),
				new Book(2, "SN456", "Book 2", "Author 2", "Genre 2", false));

		// When
		bookSwingView.showAllBooks(books);

		// Then
		JTableFixture table = window.table();
		assertThat(table.valueAt(TableCell.row(0).column(0))).isEqualTo("1");
		assertThat(table.valueAt(TableCell.row(0).column(1))).isEqualTo("SN123");
		assertThat(table.valueAt(TableCell.row(0).column(2))).isEqualTo("Book 1");
		assertThat(table.valueAt(TableCell.row(0).column(3))).isEqualTo("Author 1");
		assertThat(table.valueAt(TableCell.row(0).column(4))).isEqualTo("Genre 1");
		assertThat(table.valueAt(TableCell.row(0).column(5))).isEqualTo("true");
		assertThat(table.valueAt(TableCell.row(1).column(0))).isEqualTo("2");
		assertThat(table.valueAt(TableCell.row(1).column(1))).isEqualTo("SN456");
		assertThat(table.valueAt(TableCell.row(1).column(2))).isEqualTo("Book 2");
		assertThat(table.valueAt(TableCell.row(1).column(3))).isEqualTo("Author 2");
		assertThat(table.valueAt(TableCell.row(1).column(4))).isEqualTo("Genre 2");
		assertThat(table.valueAt(TableCell.row(1).column(5))).isEqualTo("false");
	}

	@Test
	void testShowErrorWithBook() {
		// Given
		String errorMessage = "Error message";
		Book book = new Book(1, "SN123", "Book Name", "Author", "Genre", true);

		// When
		bookSwingView.showError(errorMessage, book);

		// Then
		JLabel errorLabel = bookSwingView.getErrorLabel();
		assertThat(errorLabel.getText()).isEqualTo(errorMessage);
	}

	@Test
	void testShowErrorBookNotFound() {
		// Given
		String errorMessage = "Book not found error";

		// When
		bookSwingView.showErrorBookNotFound(errorMessage);

		// Then
		JLabel errorLabel = bookSwingView.getErrorLabel();
		assertThat(errorLabel.getText()).isEqualTo(errorMessage);
	}

	@Test
	void testShowErrorBookNotFoundWithBook() {
		// Given
		String errorMessage = "Book not found with details";
		Book book = new Book(1, "SN123", "Book Name", "Author", "Genre", true);

		// When
		bookSwingView.showErrorBookNotFound(errorMessage, book);

		// Then
		JLabel errorLabel = bookSwingView.getErrorLabel();
		assertThat(errorLabel.getText()).isEqualTo(errorMessage);
	}

	@Test
	void testPrivateShowError() throws Exception {
		// Given
		String errorMessage = "Private method error message";
		java.lang.reflect.Method method = BookSwingView.class.getDeclaredMethod("showError", String.class);
		method.setAccessible(true);

		// When
		method.invoke(bookSwingView, errorMessage);

		// Then
		JLabel errorLabel = bookSwingView.getErrorLabel();
		assertThat(errorLabel.getText()).isEqualTo(errorMessage);
	}

	@Test
	void testBookRemoved() {
		// Given
		Book book = new Book(1, "SN123", "Book Name", "Author", "Genre", true);
		bookSwingView.bookAdded(book);
		JTableFixture table = window.table();
		assertThat(table.rowCount()).isEqualTo(1);
		JComboBoxFixture comboBox = window.comboBox("Serial Number ComboBox");
		assertThat(comboBox.contents()).contains("SN123");

		// When
		bookSwingView.bookRemoved(book);

		// Then
		assertThat(table.rowCount()).isZero();
		assertThat(comboBox.contents()).doesNotContain("SN123");
	}

	@Test
	void testBookRemovedWhenNotPresent() {
		// Given
		Book book = new Book(1, "SN123", "Book Name", "Author", "Genre", true);
		bookSwingView.bookAdded(book);
		JTableFixture table = window.table();
		assertThat(table.rowCount()).isEqualTo(1);
		JComboBoxFixture comboBox = window.comboBox("Serial Number ComboBox");
		assertThat(comboBox.contents()).contains("SN123");

		// When
		Book nonExistentBook = new Book(2, "SN999", "Non-existent Book", "Unknown", "Unknown", false);
		bookSwingView.bookRemoved(nonExistentBook);

		// Then
		assertThat(table.rowCount()).isEqualTo(1);
		assertThat(comboBox.contents()).contains("SN123");
	}

	@Test
	void testShowSearchedBooks() {
		// Given
		Book book = new Book(1, "SN123", "Book Name", "Author", "Genre", true);

		// When
		bookSwingView.showSearchedBooks(book);

		// Then
		JTableFixture table = window.table();
		assertThat(table.rowCount()).isEqualTo(1);
		assertThat(table.valueAt(TableCell.row(0).column(0))).isEqualTo("1");
		assertThat(table.valueAt(TableCell.row(0).column(1))).isEqualTo("SN123");
		assertThat(table.valueAt(TableCell.row(0).column(2))).isEqualTo("Book Name");
		assertThat(table.valueAt(TableCell.row(0).column(3))).isEqualTo("Author");
		assertThat(table.valueAt(TableCell.row(0).column(4))).isEqualTo("Genre");
		assertThat(table.valueAt(TableCell.row(0).column(5))).isEqualTo("true");
	}

	@Test
	void testShowSearchedBooksWithNullBook() {
		// When
		bookSwingView.showSearchedBooks(null);

		// Then
		JTableFixture table = window.table();
		assertThat(table.rowCount()).isZero();
	}

}
