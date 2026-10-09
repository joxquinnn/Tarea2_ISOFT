package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service 
public class CsvReaderService {
    public void leerCsvBasico() {
        try {
            // Carga el archivo desde src/main/resources
            ClassPathResource resource = new ClassPathResource("compras.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            String linea;
            List<String[]> registros = new ArrayList<>();
            
            // Lee línea por línea
            while ((linea = reader.readLine()) != null) {
                String[] valores = linea.split(","); // Separa por comas
                registros.add(valores);
                
                // Imprimir para verificar
                System.out.println(valores[0] + "," + valores[1] + "," + valores[2] + "," + valores[3] + "," + valores[4]); 
            }
            reader.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}