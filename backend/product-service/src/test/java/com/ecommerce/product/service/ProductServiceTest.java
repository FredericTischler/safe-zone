package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductEvent;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
        ReflectionTestUtils.setField(productService, "productRepository", productRepository);
        ReflectionTestUtils.setField(productService, "kafkaTemplate", kafkaTemplate);
        ReflectionTestUtils.setField(productService, "productEventsTopic", "product-events");
    }

    @Test
    void createProduct_shouldPersistEntityAndPublishEvent() {
        ProductRequest request = new ProductRequest(
                "Phone",
                "Flagship phone",
                999.99,
                "Tech",
                5
        );

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId("product-id");
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            return product;
        });

        ProductResponse response = productService.createProduct(request, "seller-1", "Alice");

        assertThat(response.getId()).isEqualTo("product-id");
        assertThat(response.getSellerId()).isEqualTo("seller-1");
        verify(productRepository).save(any(Product.class));

        ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(kafkaTemplate).send(eq("product-events"), eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo("CREATED");
        assertThat(eventCaptor.getValue().getProductId()).isEqualTo("product-id");
    }

    @Test
    void updateProduct_shouldThrowWhenCallerIsNotOwner() {
        Product existing = new Product();
        existing.setId("product-1");
        existing.setSellerId("owner-1");

        when(productRepository.findById("product-1")).thenReturn(Optional.of(existing));

        ProductRequest request = new ProductRequest("name", "desc", 10.0, "cat", 2);

        assertThatThrownBy(() -> productService.updateProduct("product-1", request, "other-user"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not authorized");

        verify(productRepository, never()).save(any(Product.class));
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void deleteProduct_shouldRemoveEntityAndSendDeletedEvent() {
        Product existing = new Product();
        existing.setId("product-1");
        existing.setSellerId("owner-1");
        existing.setName("Old name");

        when(productRepository.findById("product-1")).thenReturn(Optional.of(existing));

        productService.deleteProduct("product-1", "owner-1");

        verify(productRepository).delete(existing);
        ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(kafkaTemplate).send(eq("product-events"), eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo("DELETED");
        assertThat(eventCaptor.getValue().getProductId()).isEqualTo("product-1");
    }
}
