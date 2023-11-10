package com.kickbrain.db.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class GenericRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements GenericRepository<T, ID>{
	
	private EntityManager entityManager;
	 
	public GenericRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

	public List<T> findAllWithOrder(Class<? extends Serializable> clazz, String orderBy) {
    	return entityManager.createQuery("from " + clazz.getName() + " " + orderBy).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<T> findAllWithFilter(String filter, Class<? extends Serializable> clazz, String orderBy) {
    	return entityManager.createQuery("from " + clazz.getName() + " where " + filter + " " + orderBy).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<T> findSqlQuery(String sql, Class<? extends Serializable> clazz) {
    	Query query = entityManager.createNativeQuery(sql,clazz);
    	return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Tuple> findSqlQuery(String sql) {
    	Query query = entityManager.createNativeQuery(sql,Tuple.class);
    	return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> findSqlQueryToMap(String sql) {
    	Query query = entityManager.createNativeQuery(sql, Tuple.class);
    	
    	List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
    	List<Tuple> records = query.getResultList();
    	for(Tuple record : records)
    	{
    		Map<String,Object> item = new HashMap<String, Object>();
    		
    		List<TupleElement<?>> elements = record.getElements();
    		for(TupleElement<?> element : elements)
    		{
    			String name = element.getAlias();
    			item.put(name, record.get(name));
    		}
    		
    		result.add(item);
    	}
    	return result;
    }
 
    public void customUpdateQuery(String sql)
    {
    	Query query = entityManager.createNativeQuery(sql);
    	query.executeUpdate();
    }
}