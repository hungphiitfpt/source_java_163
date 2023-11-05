package com.poly.edu.project.graduation.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.poly.edu.project.graduation.dao.ProductsRepository;
import com.poly.edu.project.graduation.model.CartEntity;
import com.poly.edu.project.graduation.model.ShopProductsEntity;
import com.poly.edu.project.graduation.services.OrderService;
import com.poly.edu.project.graduation.services.ProductServices;

@RestController
@SuppressWarnings("unchecked")
public class CartRestController<T> {

	@Autowired
	ProductServices productServices;
	
	@Autowired
	ProductsRepository productsRepository;

	@Autowired
	OrderService orderService;


	@RequestMapping(value = "/api/addCart", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, String>> viewAdd(@RequestBody CartEntity params, HttpSession session) {
		Map<Long, CartEntity> cartItems = (Map<Long, CartEntity>) session.getAttribute("cart");
		
		if (cartItems == null) {
			cartItems = new HashMap<>();
		}
		Long productId = params.getProductId();
		Long check = productsRepository.checkTonKho(productId);
		session.setAttribute("tonkho", check);
		if (cartItems.containsKey(productId) == true) {// Sản phẩm đã có trong giỏ hàng
			CartEntity shopsdetail = cartItems.get(productId);
			
			if(shopsdetail.getQuantity() + 1 > check) {
				shopsdetail.setQuantity(shopsdetail.getQuantity());
			}else {
	
				shopsdetail.setQuantity(shopsdetail.getQuantity() + 1);
			}
			
		
		} else { // Sản phẩm chưa có trong gỏ hàng
			cartItems.put(productId, params);
		}

		session.setAttribute("cart", cartItems);
		// biến này là tổng số sản phẩm đang được chọn trong giỏ hàng
		session.getAttribute("countCartItems");
	    return new ResponseEntity<>(Utils.cartStarts(cartItems,session), HttpStatus.OK);
	
	}

	@RequestMapping(value = "/api/updateCart", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, String>> updateCartInput(@RequestBody CartEntity params, HttpSession session) {
		try {
			Map<Long, CartEntity> cartItems = (Map<Long, CartEntity>) session.getAttribute("cart");
			if (cartItems == null) {
				cartItems = new HashMap<>();
			}
			Long productId = params.getProductId();
			ShopProductsEntity stock = productsRepository.findById_c(productId);

			if (cartItems.containsKey(productId) == true) {
				CartEntity shopsdetail = cartItems.get(productId);
				shopsdetail.setQuantity(params.getQuantity());
				switch (params.getQuantity()) {
				case 0:
					cartItems.remove(productId, shopsdetail);
					session.setAttribute("cart", cartItems);
					break;
				default:
						cartItems.put(productId, params);
					break;
				}
			} else { // Sản phẩm chưa có trong gỏ hàng
				session.setAttribute("cart", cartItems);
			}
			// biến này là tổng số sản phẩm đang được chọn trong giỏ hàng
			session.getAttribute("countCartItems");
			return new ResponseEntity<>(Utils.cartStarts(cartItems, session), HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/api/deleteCart/{id}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, String>> deleteCartItem(@PathVariable(value = "id") long id,
			HttpSession session) {
		try {
			Map<Long, CartEntity> cartItems = (Map<Long, CartEntity>) session.getAttribute("cart");
			if (cartItems != null && cartItems.containsKey(id)) {
				cartItems.remove(id);
				session.setAttribute("cart", cartItems);
			}
			return new ResponseEntity<>(Utils.cartStarts(cartItems,session), HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/api/pay", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public HttpStatus PayCart(HttpSession session) {
		
		orderService.CreateOrder((Map<Long, CartEntity>) session.getAttribute("cart"), session);
		session.removeAttribute("cart");
		session.removeAttribute("countCartItems");
		session.removeAttribute("userInf");
		return HttpStatus.OK;

	}
}
