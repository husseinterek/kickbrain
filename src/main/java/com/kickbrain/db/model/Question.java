package com.kickbrain.db.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "QUESTIONS")
@NamedQuery(name = "Question.findAll", query = "SELECT q FROM Question q")
public class Question implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@TableGenerator( 
	        name = "QuestionsSeqStore", 
	        table = "SEQUENCE_STORE", 
	        pkColumnName = "SEQ_NAME", 
	        pkColumnValue = "QUESTIONS_SEQ", 
	        valueColumnName = "SEQ_VALUE", 
	        initialValue = 4, 
	        allocationSize = 1 )
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "QuestionsSeqStore")
	private long id;
	
	@Column(name = "DESCRIPTION_EN")
	private String descriptionEn;
	
	@Column(name = "DESCRIPTION_AR")
	private String descriptionAr;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "question")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<QuestionAnswers> answers;
	
	@Column(name = "CATEGORY_ID")
	private int categoryId;
	
	@Column(name = "TAG")
	private int tag;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	public String getDescriptionAr() {
		return descriptionAr;
	}

	public void setDescriptionAr(String descriptionAr) {
		this.descriptionAr = descriptionAr;
	}

	public List<QuestionAnswers> getAnswers() {
		return answers;
	}

	public void setAnswers(List<QuestionAnswers> answers) {
		this.answers = answers;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public int getTag() {
		return tag;
	}
}
