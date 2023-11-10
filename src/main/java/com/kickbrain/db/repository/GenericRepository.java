package com.kickbrain.db.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	public List<T> findAllWithOrder(Class<? extends Serializable> clazz, String orderBy);

	public List<T> findAllWithFilter(String filter, Class<? extends Serializable> clazz, String orderBy);

	public List<T> findSqlQuery(String query, Class<? extends Serializable> clazz);

	public List<Tuple> findSqlQuery(String query);

	public List<Map<String, Object>> findSqlQueryToMap(String sql);

	public void customUpdateQuery(String sql);
}