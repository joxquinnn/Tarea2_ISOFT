package com.example.demo.service;

import com.example.demo.dto.compraUnidadDTO;
import com.example.demo.dto.compraUnidadResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CompraService {

    private final RestTemplate restTemplate = new RestTemplate();

    public compraUnidadResponseDTO obtenerComprasPorUnidad(String unidadParam) {
        String unidadFiltro;
        String indicadorApi;
        
        if (unidadParam.equalsIgnoreCase("uf")) {
            unidadFiltro = "UF";
            indicadorApi = "uf";
        } else if (unidadParam.equalsIgnoreCase("dolar")) {
            unidadFiltro = "USD";
            indicadorApi = "dolar";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unidad no válida. Use 'uf' o 'dolar'.");
        }

        // Obtener ruta del CSV desde variable de entorno o usar la raíz por defecto
        String csvPath = System.getenv("CSV_PATH");
        if (csvPath == null || csvPath.isEmpty()) {
            csvPath = "compras.csv"; 
        }

        List<compraUnidadDTO> comprasFiltradas = new ArrayList<>();
        long totalPesos = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; 
                }

                String[] columnas = linea.split(",");
                if (columnas.length < 5) continue;

                String unidadCsv = columnas[3].trim();

                if (unidadCsv.equalsIgnoreCase(unidadFiltro)) {
                    compraUnidadDTO compra = new compraUnidadDTO();
                    compra.setId(Integer.parseInt(columnas[0].trim()));
                    compra.setProducto(columnas[1].trim());
                    compra.setMonto(Double.parseDouble(columnas[2].trim()));
                    compra.setFecha(columnas[4].trim());

                    double valorUnidad = obtenerValorIndicador(indicadorApi, compra.getFecha());
                    compra.setValor_unidad(valorUnidad);

                    long montoPesos = Math.round(compra.getMonto() * valorUnidad);
                    compra.setMonto_pesos(montoPesos);

                    totalPesos += montoPesos;
                    comprasFiltradas.add(compra);
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar el archivo CSV: " + e.getMessage());
        }

        compraUnidadResponseDTO response = new compraUnidadResponseDTO();
        response.setUnidad(unidadFiltro);
        response.setCompras(comprasFiltradas);
        response.setTotal_pesos(totalPesos);

        return response;
    }

    private double obtenerValorIndicador(String indicador, String fechaIso) {
        try {
            LocalDate date = LocalDate.parse(fechaIso);
            String fechaMindicador = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            
            String url = "https://mindicador.cl/api/" + indicador + "/" + fechaMindicador;
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("serie")) {
                List<Map<String, Object>> serie = (List<Map<String, Object>>) response.get("serie");
                if (!serie.isEmpty()) {
                    return Double.parseDouble(serie.get(0).get("valor").toString());
                }
            }
            return 0.0;
        } catch (Exception e) {
            System.err.println("Error al obtener valor de la API para fecha " + fechaIso + ": " + e.getMessage());
            return 0.0;
        }
    }
}