package com.crionline.activiti.dao;

import com.crionline.activiti.model.Comp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompRepository extends JpaRepository<Comp, Long> {
	
}
