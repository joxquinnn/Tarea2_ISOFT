package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CompraDTO;
import com.example.demo.service.CompraService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
public class comprasController {
    private final CompraService service;

    public comprasController(CompraService service){
        this.service = service;
    }

    @GetMapping("/compras/detalle/{id}")
    public ResponseEntity<CompraDTO> getDetalleCompra(@PathVariable int id) {
        CompraDTO compra = service.buscarCompraId(id);
        if (compra == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);    
        }
        return ResponseEntity.ok(compra);
    }
    
}
