package com.tienda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tienda.model.Categoria;
import com.tienda.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Override
    @SuppressWarnings("null")
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
    
    @Override
    @SuppressWarnings("null")
    public Categoria findById(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }
}
