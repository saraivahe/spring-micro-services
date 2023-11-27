package com.saraivahe.orderservice.service;

import com.saraivahe.orderservice.dto.InventoryResponse;
import com.saraivahe.orderservice.dto.OrderLineItemsDto;
import com.saraivahe.orderservice.dto.OrderRequest;
import com.saraivahe.orderservice.model.Order;
import com.saraivahe.orderservice.model.OrderLineItems;
import com.saraivahe.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest
                .getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);

        List<String> skuCodeList = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder ->
                                uriBuilder.queryParam("skuCodeList", skuCodeList).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if (inventoryResponseArray == null) {
            throw new RuntimeException("Failed to get inventory information"); // Replace with ApiException
        }

        Arrays.stream(inventoryResponseArray)
                .filter(inventoryResponse -> !inventoryResponse.isInStock())
                .findAny()
                .ifPresent(inventoryResponse -> {
                    throw new IllegalArgumentException("Out of stock for skuCode: " + inventoryResponse.getSkuCode());
                });

        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
    OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }
}
