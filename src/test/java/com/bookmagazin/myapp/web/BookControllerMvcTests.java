package com.bookmagazin.myapp.web;


import com.bookmagazin.myapp.domain.BookNotFoundException;
import com.bookmagazin.myapp.domain.BookService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Tests. Spring mvc controller loads spring application context in mock web env.
 * means no running server, configures spring mvc infrastructure and includes only beans used by
 * mvc layer like restcontoller and restcontrolleradvice.
 */
@WebMvcTest(BookController.class) //Identify test class that focuses on spring mvc components
                                  //explicitly targeting BookController

class BookControllerMvcTests {

    @Autowired
    private MockMvc mockMvc; //Utility class to test web layer in mock env.

    @MockBean
    private BookService bookService;  //Adds a mock of BookService to Spring application context

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "73737313940";
        given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotFoundException.class); //defines expected behavior. If not defined then
                                                             //caca laca für unteres Code
        mockMvc                                         //used to perform http get request and verify results
                .perform(get("/books/" + isbn))
                .andExpect(status().isNotFound()); //expects response 404
    }

}