package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "producto", "monto", "unidad", "fecha", "valor_unidad", "monto_pesos" })
public class CompraDTO {
    
    private int id;
    private String producto;
    private double monto;
    private String unidad;
    private String fecha;
    private double valor_unidad;
    private long monto_pesos;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public double getValor_unidad() { return valor_unidad; }
    public void setValor_unidad(double valor_unidad) { this.valor_unidad = valor_unidad; }
    public long getMonto_pesos() { return monto_pesos; }
    public void setMonto_pesos(long monto_pesos) { this.monto_pesos = monto_pesos; }
}