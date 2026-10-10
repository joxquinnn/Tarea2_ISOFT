package com.example.demo.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CsvRunner implements CommandLineRunner {

    private final CsvReaderService csvReaderService;

    public CsvRunner(CsvReaderService csvReaderService){
        this.csvReaderService = csvReaderService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- INICIANDO LECTURA DEL CSV ---");
        
        csvReaderService.leerCsvBasico();
        
        System.out.println("--- FIN DE LA LECTURA ---");
    }
}