package com.saraivahe.inventoryservice;

import com.saraivahe.inventoryservice.model.Inventory;
import com.saraivahe.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}


	/* @Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return (args) -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("SKU-123");
			inventory.setQuantity(100);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("SKU-456");
			inventory1.setQuantity(0);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);
		};
	}*/
}
