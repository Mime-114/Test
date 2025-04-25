package com.pagoDocente.repository;

import com.pagoDocente.entity.DocentePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocentePagoRepository extends JpaRepository<DocentePago, Long> {

}