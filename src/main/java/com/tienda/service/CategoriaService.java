package com.tienda.service;

import com.tienda.model.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> findAll();
    Categoria save(Categoria categoria);
    Categoria findById(Long id);
}
