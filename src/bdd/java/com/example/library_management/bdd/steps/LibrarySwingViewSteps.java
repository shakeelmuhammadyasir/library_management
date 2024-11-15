package com.example.library_management.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import com.example.library_management.bdd.LibrarySwingAppBDD;
import static com.example.library_management.bdd.steps.DatabaseSteps.DB_NAME;
import static com.example.library_management.bdd.steps.DatabaseSteps.COLLECTION_NAME;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LibrarySwingViewSteps {

	private FrameFixture window;

	@After
	public void tearDown() {
		if (window != null) {
			window.cleanUp();
		}
	}

	@When("The Library Management View is shown")
	public void the_Library_Management_View_is_shown() {
		application("com.example.library_management.app.swing.LibrarySwingApp")
				.withArgs("--mongo-port=" + LibrarySwingAppBDD.mongoPort, "--db-name=" + DB_NAME,
						"--db-collection=" + COLLECTION_NAME)
				.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Library Management System".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@Given("The user provides book data in the text fields")
	public void the_user_provides_book_data_in_the_text_fields() {
		window.textBox("ID").enterText("10");
		window.textBox("Serial Number").enterText("SN354");
		window.textBox("Name").enterText("New Book");
		window.textBox("Author Name").enterText("Author Name");
		window.textBox("Genre").enterText("Genre");
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonText) {
		window.button(JButtonMatcher.withText(buttonText)).click();
	}

	@Then("The list contains the new book")
	public void the_list_contains_the_new_book() {
		String[][] tableContents = window.table().contents();
		assertThat(tableContents).isNotEmpty();
		assertThat(Arrays.asList(tableContents))
				.contains(new String[] { "10", "SN354", "New Book", "Author Name", "Genre", "false" });
	}

	@Given("The user provides book data in the text fields, specifying an existing id")
	public void the_user_provides_book_data_in_the_text_fields_specifying_an_existing_id() {
		window.textBox("ID").enterText(DatabaseSteps.BOOK_1_ID);
		window.textBox("Serial Number").enterText(DatabaseSteps.BOOK_1_SERIAL_NUMBER);
		window.textBox("Name").enterText(DatabaseSteps.BOOK_1_NAME);
		window.textBox("Author Name").enterText(DatabaseSteps.BOOK_1_AUTHOR_NAME);
		window.textBox("Genre").enterText(DatabaseSteps.BOOK_1_GENRE);
	}

	@Then("An error is shown containing the name of the existing book")
	public void an_error_is_shown_containing_the_name_of_the_existing_book() {
		window.label(JLabelMatcher
				.withText("Already existing book with serial number " + DatabaseSteps.BOOK_1_SERIAL_NUMBER))
				.requireVisible();
	}

	@Given("The user selects a book from the list")
	public void the_user_selects_a_book_from_the_list() {
		String[][] tableContents = window.table().contents();
		assertThat(tableContents).isNotEmpty();
		window.table().selectRows(0);
	}

	@Then("The book is removed from both lists")
	public void the_book_is_removed_from_both_lists() {
		String[][] tableContents = window.table().contents();
		List<String[]> tableContentList = Arrays.asList(tableContents);
		assertThat(tableContentList).isNotEmpty();

		assertThat(tableContentList).doesNotContain(
				new String[] { DatabaseSteps.BOOK_1_ID, DatabaseSteps.BOOK_1_SERIAL_NUMBER, DatabaseSteps.BOOK_1_NAME,
						DatabaseSteps.BOOK_1_AUTHOR_NAME, DatabaseSteps.BOOK_1_GENRE, "true" });

		String[] comboBoxContents = window.comboBox("Serial Number ComboBox").contents();
		List<String> comboBoxContentList = Arrays.asList(comboBoxContents);
		assertThat(comboBoxContentList).isNotEmpty();

		assertThat(comboBoxContentList).doesNotContain(DatabaseSteps.BOOK_1_SERIAL_NUMBER);
	}

}
