package com.poly.edu.project.graduation.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.poly.edu.project.graduation.model.ShopFavoutiteEntity;

@Repository
public interface FavouriteRepository extends JpaRepository<ShopFavoutiteEntity, Integer>{

	@Query(value = " SELECT * FROM shop_favoutite where user_id = ?1", nativeQuery =  true)
	List<ShopFavoutiteEntity> findById(String id);
	
	@Transactional
	@Modifying
	@Query(value = " INSERT INTO shop_favoutite(user_id,product_id) VALUES (:idUser, :product)", nativeQuery =  true)
	void createHearth(int idUser, String product);
	
	@Query(value = "SELECT  shop_favoutite.product_id, shop_favoutite.user_id, shop_products.id, shop_products.image, shop_products.product_name, shop_products.list_price "
			+ "FROM shop_favoutite "
			+ "INNER JOIN app_user ON shop_favoutite.user_id = app_user.user_id "
			+ "INNER JOIN shop_products ON shop_favoutite.product_id = shop_products.id Where app_user.user_id = ?1 "
			+ "GROUP BY shop_favoutite.product_id", nativeQuery =  true)	
	List<ShopFavoutiteEntity> getListFavourite(String id);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM shop_favoutite WHERE product_id = ?1", nativeQuery =  true)	
	void deletehearth(String id);

	@Query(value = "SELECT count(*) FROM shop_favoutite Where shop_favoutite.user_id = ?1 "
			+ "GROUP BY shop_favoutite.product_id;", nativeQuery =  true)	
	int countQuantity(String idUser);

}
