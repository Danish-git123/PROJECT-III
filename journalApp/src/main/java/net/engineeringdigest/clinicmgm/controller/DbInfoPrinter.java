package net.engineeringdigest.clinicmgm.controller;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DbInfoPrinter {
    private final DataSource dataSource;

    public DbInfoPrinter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void printDb() throws Exception {
        try (Connection con = dataSource.getConnection()) {
            System.out.println("Connected to DB = " + con.getCatalog());
        }
    }


}
