package com.meek.obsidian_second_brain;

import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
	PgVectorStoreAutoConfiguration.class,
	DataSourceAutoConfiguration.class
})
public class ObsidianSecondBrainApplication {

	public static void main(String[] args) {
		SpringApplication.run(ObsidianSecondBrainApplication.class, args);
	}

}
