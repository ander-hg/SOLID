package br.ufscar.dc.dsw.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.ufscar.dc.dsw.domain.*;
import br.ufscar.dc.dsw.service.spec.*;

@Controller
@RequestMapping("/profissionais")
public class ProfissionaisController {

	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private IProfissionaisService service;

	@Autowired
	private IConsultasService consultaService;

	@GetMapping("/cadastrar")
	public String cadastrar(ModelMap model) {
		model.addAttribute("profissionais", new Profissionais());
		return "profissional/cadastro";
	}

	@GetMapping("/listar")
	public String listar(ModelMap model) {
		model.addAttribute("profissionais", service.buscarTodos());
		return "profissional/lista";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid Profissionais profissional, BindingResult result, RedirectAttributes attr) {

		if (result.hasErrors()) {
			return "profissional/cadastro";
		}
		profissional.setSenha(encoder.encode(profissional.getSenha()));
		service.salvar(profissional);
		attr.addFlashAttribute("sucess", "Profissional inserido com sucesso.");
		return "redirect:/profissionais/listar";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("profissionais", service.buscarPorId(id));
		return "profissional/cadastro";
	}

	@PostMapping("/editar")
	public String editar(@Valid Profissionais profissional, BindingResult result, RedirectAttributes attr) {

		if (result.hasErrors()) {
			return "profissional/cadastro";
		}

		service.salvar(profissional);
		attr.addFlashAttribute("sucess", "Profissional editado com sucesso.");
		return "redirect:/profissionais/listar";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, ModelMap model) {
		if (service.profissionalTemConsultas(id)) {
			model.addAttribute("fail", "Profissional não excluído. Possui consulta(s) vinculada(s).");
		} else {
			service.excluir(id);
			model.addAttribute("sucess", "Profissional excluído com sucesso.");
		}
		return listar(model);
	}

	@ModelAttribute("consulta")
	public List<Consultas> listaConsultas() {
		return consultaService.buscarTodos();
	}
}
