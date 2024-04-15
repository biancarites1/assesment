package com.example.demo.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class ResourceControllerDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getStatistics() throws Exception {
        this.mockMvc.perform(get("/recommendation/statistics-for-crypto", "BTC"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcRestDocumentation.document("statistics-for-crypto"));
    }
//
//    @Test
//    public void getAllNormalizedValues() throws Exception {
//        this.mockMvc.perform(get("/recommendation/normalized-range", "btc"))
//                .andExpect(status().isBadRequest())
//                .andDo(MockMvcRestDocumentation.document("normalized-range"));
//    }


    @Test
    public void getHighestNormalizedValue() throws Exception {
        this.mockMvc.perform(get("/recommendation/highest-normalized-range-by-day", new Date()))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcRestDocumentation.document("highest-normalized-range-by-day"));
    }


}
