package com.example.demo.service;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.CompraDTO;

@Service 
public class CompraService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);
    
    @Value("${CSV_PATH:compras.csv}")
    private String csvPath;
    private final RestTemplate restTemplate;

    public CompraService() {
        this.restTemplate = new RestTemplate();
    }

    private List<CompraDTO> leerCsv() {
        List<CompraDTO> compras = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvPath))) {
            String linea;
            boolean primeraLinea = true; 
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; 
                }
                
                String[] valores = linea.split(","); 
                CompraDTO compra = new CompraDTO();
                compra.setId(Integer.parseInt(valores[0].trim()));
                compra.setProducto(valores[1].trim());
                compra.setMonto(Double.parseDouble(valores[2].trim()));
                compra.setUnidad(valores[3].trim());
                compra.setFecha(LocalDate.parse(valores[4].trim())); 
                compras.add(compra);
            }
            return compras;
        } catch (Exception e) {
            logger.error("Error al leer el archivo CSV en la ruta: {}", csvPath, e);
            return null;
        }
    }

    public CompraDTO buscarCompraId(int id) {
        List<CompraDTO> compras = leerCsv(); 
        if (compras == null) {
            return null;
        }
        
        CompraDTO compra = compras.stream()
            .filter(c -> c.getId() == id)
            .findAny()
            .orElse(null);
        
        if (compra != null) {
            double valorUnidad = obtenerValorMindicador(compra.getUnidad(), compra.getFecha());
            compra.setValor_unidad(valorUnidad);
            compra.setMonto_pesos((int) Math.round(compra.getMonto() * valorUnidad));
        } else {
             logger.debug("Compra con id {} no encontrada", id);
        }
        
        return compra;
    }

    @SuppressWarnings("unchecked")
    private double obtenerValorMindicador(String unidad, LocalDate fecha) {
        String indicador = unidad.equalsIgnoreCase("UF") ? "uf" : "dolar";
        String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String url = String.format("https://mindicador.cl/api/%s/%s", indicador, fechaFormateada);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("serie")) {
                List<Map<String, Object>> serie = (List<Map<String, Object>>) response.get("serie");
                if (!serie.isEmpty()) {
                    Object valorObj = serie.get(0).get("valor");
                    if (valorObj instanceof Number) {
                        return ((Number) valorObj).doubleValue();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error al consultar la API mindicador para la unidad {} en la fecha {}", unidad, fechaFormateada, e);
        }
        return 0.0;
    }
}