package com.kickbrain.db.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "ANSWERS")
@NamedQuery(name = "Answers.findAll", query = "SELECT a FROM Answers a")
public class Answers implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@TableGenerator( 
	        name = "AnswersSeqStore", 
	        table = "SEQUENCE_STORE", 
	        pkColumnName = "SEQ_NAME", 
	        pkColumnValue = "ANSWERS_SEQ", 
	        valueColumnName = "SEQ_VALUE", 
	        initialValue = 4100, 
	        allocationSize = 1 )
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "AnswersSeqStore")
	private long id;
	
	@Column(name = "TYPE")
	private int type;
	
	@Column(name = "NAME_EN")
	private String nameEn;
	
	@Column(name = "NAME_AR")
	private String nameAr;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameAr() {
		return nameAr;
	}

	public void setNameAr(String nameAr) {
		this.nameAr = nameAr;
	}

}
