package com.tibbelin.tajmtrackr;

import java.time.ZoneId;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TajmtrackrApplication {

	public static void main(String[] args) {
		
        Set<String> timezones = ZoneId.getAvailableZoneIds();
		System.out.println(timezones);
		SpringApplication.run(TajmtrackrApplication.class, args);
	}

}
