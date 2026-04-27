package com.tienda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.model.Cliente;
import com.tienda.repository.ClienteRepository;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "cliente/lista";
    }

    @GetMapping("/nuevo")
    public String createForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/form";
    }

    @PostMapping("/guardar")
    @SuppressWarnings("null")
    public String save(@ModelAttribute Cliente cliente) {
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    @SuppressWarnings("null")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteRepository.findById(id).orElse(null));
        return "cliente/form";
    }

    @GetMapping("/eliminar/{id}")
    @SuppressWarnings("null")
    public String delete(@PathVariable Long id) {
        clienteRepository.deleteById(id);
        return "redirect:/clientes";
    }
}
