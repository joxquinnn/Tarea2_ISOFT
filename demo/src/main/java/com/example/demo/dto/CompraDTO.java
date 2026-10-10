package com.example.demo.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter 
@NoArgsConstructor 
@ToString 
@JsonPropertyOrder({ "id", "producto", "monto", "unidad", "fecha", "valor_unidad", "monto_pesos" })
public class CompraDTO {
    private int id;
    private String producto;
    private double monto;
    private String unidad;
    private LocalDate fecha;
    private double valor_unidad;
    private double monto_pesos;
}
