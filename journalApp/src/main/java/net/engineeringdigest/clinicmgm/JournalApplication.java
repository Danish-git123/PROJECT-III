package net.engineeringdigest.clinicmgm;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class JournalApplication {



    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(JournalApplication.class, args);

    }




}