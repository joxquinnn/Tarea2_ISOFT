package com.example.demo.controller;

import com.example.demo.dto.CompraDTO;
import com.example.demo.dto.compraUnidadResponseDTO;
import com.example.demo.service.CompraService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compras")
public class comprasController {

    private final CompraService compraService;

    public comprasController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping("/compras/detalle/{id}")
    public ResponseEntity<CompraDTO> getDetalleCompra(@PathVariable int id) {
        CompraDTO compra = compraService.buscarCompraId(id);
        if (compra == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();    
        }
        return ResponseEntity.ok(compra);
    }

    @GetMapping("/{unidad}")
    public compraUnidadResponseDTO getComprasPorUnidad(@PathVariable String unidad) {
        return compraService.obtenerComprasPorUnidad(unidad);
    }
}