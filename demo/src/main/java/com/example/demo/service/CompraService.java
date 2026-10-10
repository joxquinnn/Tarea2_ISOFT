package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CompraDTO;

@Service 
public class CompraService{
    private final double UNIDAD_UF = 41.130;
    private final double UNIDAD_USD = 0.976;
    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);
    private List<CompraDTO> leerCsv() {
        try {
            // Carga el archivo desde src/main/resources
            ClassPathResource resource = new ClassPathResource("compras.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            String linea;
            List<CompraDTO> compras = new ArrayList<>();
            boolean primeraLinea = true; 
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    try {
                        Integer.parseInt(linea.split(",")[0]);
                    } catch (NumberFormatException e) {
                        continue; 
                    }
                }
                CompraDTO compra = new CompraDTO();
                String[] valores = linea.split(","); 
                
                logger.debug("Procesando: " + valores[0] + "," + valores[1] + "," + valores[2] + "," + valores[3] + "," + valores[4]);
                compra.setId(Integer.parseInt(valores[0]));
                compra.setProducto(valores[1]);
                compra.setMonto(Double.parseDouble(valores[2]));
                compra.setUnidad(valores[3]);
                compra.setFecha(LocalDate.parse(valores[4]));
                compras.add(compra);
            }
            reader.close();
            return compras;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*  leer las cosas filtrar por id 
        pasar unidad a peso
        devolver un dto
    */
    private CompraDTO filtrarId(int id){
        List<CompraDTO> compras = leerCsv(); 
        if (compras == null) {
            return null;
        }
        CompraDTO compra = compras.stream()
            .filter(c -> c.getId() == id)
            .findAny()
            .orElse(null);
        
        if (compra != null) {
             logger.debug(compra.toString());
        } else {
             logger.debug("Compra con id {} no encontrada", id);
        }
        
        return compra;
    }

    public CompraDTO buscarCompraId(int id){
        CompraDTO compra = filtrarId(id);
        if (compra == null) { return null; }
        if (compra.getUnidad().equals("UF")) {
            compra.setMonto_pesos( compra.getMonto() * UNIDAD_UF);
            compra.setValor_unidad(UNIDAD_UF);
        } else if (compra.getUnidad().equals("USD")) { 
            compra.setMonto_pesos( compra.getMonto() * UNIDAD_USD);
            compra.setValor_unidad(UNIDAD_USD);
        } else {
            compra.setMonto_pesos(compra.getMonto());
            compra.setValor_unidad(1.0);
        }
        return compra;
    }
}
