package com.pi4.dungeonFinderBackend;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableScheduling
@SpringBootApplication
public class DungeonFinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DungeonFinderApplication.class, args);
	}

}
