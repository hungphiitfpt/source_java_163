package com.poly.edu.project.graduation.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.edu.project.graduation.dao.FavouriteRepository;
import com.poly.edu.project.graduation.dao.UserRepository;
import com.poly.edu.project.graduation.model.ShopFavoutiteEntity;
import com.poly.edu.project.graduation.services.UserService;


@RestController
@RequestMapping("/api/graduation")
public class FavouriteRestController {

	@Autowired
	FavouriteRepository favouriteRepository;
	
	@Autowired 
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
    PasswordEncoder passwordEncoder;

	@GetMapping("favorite-product2/{id}")
	public List<ShopFavoutiteEntity> index(@PathVariable String id) {

		List<ShopFavoutiteEntity> sfv = favouriteRepository.findById(id);

		return sfv;
		
	}
	
	@RequestMapping(value = "/createHearthForUser", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public  void createHearth(Principal principal,
			@RequestParam(name = "IdProduct", required = false, defaultValue = "") String product) {	
		String userName = principal.getName();
		String idUser  = userService.findIdUserByPrincipal(userName);
		favouriteRepository.createHearth(Integer.parseInt(idUser),product);

	}
	
	@RequestMapping(value = "/deleteHearth", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void deleteHearht(
			@RequestParam(name = "IdProduct", required = false, defaultValue = "") String product,Model model,Principal principal) {	
		favouriteRepository.deletehearth(product);
		
	}
	
	@RequestMapping(value = "/countQuantity", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public int countQuantityHearthProduct(Principal principal) {
		String userName = principal.getName();
		String idUser  = userService.findIdUserByPrincipal(userName);
		int result = favouriteRepository.countQuantity(idUser);
		return result;
	}
	
	@RequestMapping(value = "/changePass", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void Update(Principal principal,
					  @RequestParam(name = "passWord", required = false, defaultValue = "") String password) {
		String userName = principal.getName();
		String idUser  = userService.findIdUserByPrincipal(userName);
		String passencoder = passwordEncoder.encode(password);
		userRepository.changePass(passencoder, idUser);
	}
	
}
