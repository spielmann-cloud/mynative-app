package com.bookmagazin.myapp;

import com.bookmagazin.myapp.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests. webClient sends request to running server. Spring Boot Test caches application context
 */
//Loads a full spring web application context and servlet container listening on random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyappApplicationTests {
	@Autowired
	private WebTestClient webTestClient; //Utility to perform rest calls for testing
										//Attribute based injection is not preferred but ok for tests

	@Test
	void whenGetRequestWithIdThenBookReturned() {
		var bookIsbn = "1231231230";
		var bookToCreate = new Book(bookIsbn, "Title", "Author", 9.90);
		Book expectedBook = webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate) // Adds the book in request body
				.exchange() //Sends the request
				.expectStatus().isCreated() //Verifies that http response status has 201 created
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull()) //Verify that http response has non-null body
				.returnResult().getResponseBody(); //return result, body

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = new Book("1231231231", "Title", "Author", 9.90);

		webTestClient
				.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPutRequestThenBookUpdated() {
		var bookIsbn = "1231231232";
		var bookToCreate = new Book(bookIsbn, "Title", "Author", 9.90);
		Book createdBook = webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();
		var bookToUpdate = new Book(createdBook.isbn(), createdBook.title(), createdBook.author(), 7.95);

		webTestClient
				.put()
				.uri("/books/" + bookIsbn)
				.bodyValue(bookToUpdate)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
				});
	}

	@Test
	void whenDeleteRequestThenBookDeleted() {
		var bookIsbn = "1231231233";
		var bookToCreate = new Book(bookIsbn, "Title", "Author", 9.90);
		webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient
				.delete()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNoContent();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class).value(errorMessage ->
						assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
				);
	}
}
