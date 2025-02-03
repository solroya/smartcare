package com.nado.smartcare.food.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nado.smartcare.food.entity.Coment;

@Repository
public interface IComentRepository extends JpaRepository<Coment, Long>{
	List<Coment> findByFoodPlace_FnoOrderByCreatedAtDesc(Long foodPlaceId);
}
