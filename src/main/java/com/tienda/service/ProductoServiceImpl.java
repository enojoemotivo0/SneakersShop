package com.tienda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tienda.model.Producto;
import com.tienda.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
    
    @Override
    @SuppressWarnings("all")
    public Page<Producto> findAll(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }
    
    @Override
    @SuppressWarnings("all")
    public Page<Producto> findByNombre(String nombre, Pageable pageable) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    @SuppressWarnings("all")
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    @SuppressWarnings("all")
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    @SuppressWarnings("all")
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}
