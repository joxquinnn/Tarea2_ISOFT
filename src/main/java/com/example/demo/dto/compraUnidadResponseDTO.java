package com.example.demo.dto;

import java.util.List;

public class compraUnidadResponseDTO {
    private String unidad;
    private List<compraUnidadDTO> compras;
    private long total_pesos;

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public List<compraUnidadDTO> getCompras() { return compras; }
    public void setCompras(List<compraUnidadDTO> compras) { this.compras = compras; }
    public long getTotal_pesos() { return total_pesos; }
    public void setTotal_pesos(long total_pesos) { this.total_pesos = total_pesos; }
}