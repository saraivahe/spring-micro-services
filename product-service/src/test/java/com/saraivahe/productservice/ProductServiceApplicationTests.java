package com.saraivahe.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saraivahe.productservice.dto.ProductRequest;
import com.saraivahe.productservice.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    static {
        mongoDBContainer.start(); // Prob not needed
    }

    @Test
    @Order(1)
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @Order(2)
    void shouldGetAllProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/product")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @Order(3)
    void shouldGetAllProductsAndReturnCorrectProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/product")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals("Coke", productRepository.findAll().get(0).getName());
        Assertions.assertEquals("Cold Beverage", productRepository.findAll().get(0).getDescription());
        Assertions.assertEquals(BigDecimal.valueOf(1.75), productRepository.findAll().get(0).getPrice());
    }

    private ProductRequest getProductRequest() {
        return ProductRequest
                .builder()
                .name("Coke")
                .description("Cold Beverage")
                .price(BigDecimal.valueOf(1.75))
                .build();
    }

}
