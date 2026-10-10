package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CompraDTO;
import com.example.demo.service.CompraService;

@RestController 
public class ComprasController {
    
    private final CompraService service;

    public ComprasController(CompraService service){
        this.service = service;
    }

    @GetMapping("/compras/detalle/{id}")
    public ResponseEntity<CompraDTO> getDetalleCompra(@PathVariable int id) {
        CompraDTO compra = service.buscarCompraId(id);
        if (compra == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();    
        }
        return ResponseEntity.ok(compra);
    }
}