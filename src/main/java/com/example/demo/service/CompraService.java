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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CompraDTO;
import com.example.demo.dto.compraUnidadDTO;
import com.example.demo.dto.compraUnidadResponseDTO;

@Service 
public class CompraService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);
    private final RestTemplate restTemplate;

    public CompraService() {
        this.restTemplate = new RestTemplate();
    }

    private List<CompraDTO> leerCsv() {
        List<CompraDTO> compras = new ArrayList<>();
        
        String csvPath = System.getenv("CSV_PATH");
        if (csvPath == null || csvPath.isEmpty()) {
            csvPath = "compras.csv"; 
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvPath))) {
            String linea;
            boolean primeraLinea = true; 
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

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
                compra.setFecha(valores[4].trim()); 
                
                compras.add(compra);
            }
            return compras;
        } catch (Exception e) {
            logger.error("Error al leer el archivo CSV en la ruta: {}", csvPath, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar el archivo CSV");
        }
    }

    @SuppressWarnings("unchecked")
    private double obtenerValorMindicador(String unidad, String fechaStr) {
        String indicador = unidad.equalsIgnoreCase("UF") ? "uf" : "dolar";
        
        LocalDate fecha = LocalDate.parse(fechaStr);
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

    public CompraDTO buscarCompraId(int id) {
        List<CompraDTO> compras = leerCsv(); 
        
        CompraDTO compra = compras.stream()
            .filter(c -> c.getId() == id)
            .findAny()
            .orElse(null);
        
        if (compra != null) {
            double valorUnidad = obtenerValorMindicador(compra.getUnidad(), compra.getFecha()); 
            compra.setValor_unidad(valorUnidad);
            compra.setMonto_pesos(Math.round(compra.getMonto() * valorUnidad));
        } else {
             logger.debug("Compra con id {} no encontrada", id);
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada");
        }
        
        return compra;
    }

    public compraUnidadResponseDTO obtenerComprasPorUnidad(String unidadParam) {
        String unidadFiltro;
        
        if (unidadParam.equalsIgnoreCase("uf")) {
            unidadFiltro = "UF";
        } else if (unidadParam.equalsIgnoreCase("dolar")) {
            unidadFiltro = "USD";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unidad no válida. Use 'uf' o 'dolar'.");
        }

        List<CompraDTO> todasLasCompras = leerCsv(); 
        List<compraUnidadDTO> comprasFiltradas = new ArrayList<>();
        long totalPesos = 0;

        for (CompraDTO compraBase : todasLasCompras) {
            if (compraBase.getUnidad().equalsIgnoreCase(unidadFiltro)) {
                
                double valorUnidad = obtenerValorMindicador(compraBase.getUnidad(), compraBase.getFecha()); 
                long montoPesos = Math.round(compraBase.getMonto() * valorUnidad);
                
                compraUnidadDTO dtoFiltrado = new compraUnidadDTO();
                dtoFiltrado.setId(compraBase.getId());
                dtoFiltrado.setProducto(compraBase.getProducto());
                dtoFiltrado.setMonto(compraBase.getMonto());
                dtoFiltrado.setFecha(compraBase.getFecha());
                dtoFiltrado.setValor_unidad(valorUnidad);
                dtoFiltrado.setMonto_pesos(montoPesos);

                totalPesos += montoPesos;
                comprasFiltradas.add(dtoFiltrado);
            }
        }

        compraUnidadResponseDTO response = new compraUnidadResponseDTO();
        response.setUnidad(unidadFiltro);
        response.setCompras(comprasFiltradas);
        response.setTotal_pesos(totalPesos);

        return response;
    }
}