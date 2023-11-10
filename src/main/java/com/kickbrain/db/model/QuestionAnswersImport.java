package com.kickbrain.db.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "QUESTION_ANSWERS_IMPORT")
@NamedQuery(name = "QuestionAnswersImport.findAll", query = "SELECT q FROM QuestionAnswersImport q")
public class QuestionAnswersImport implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@TableGenerator( 
	        name = "QuestionAnswersImportSeqStore", 
	        table = "SEQUENCE_STORE", 
	        pkColumnName = "SEQ_NAME", 
	        pkColumnValue = "QUESTION_ANSWERS_IMPORT_SEQ", 
	        valueColumnName = "SEQ_VALUE", 
	        initialValue = 4, 
	        allocationSize = 1 )
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "QuestionAnswersImportSeqStore")
	private long id;
	
	@ManyToOne
	private QuestionImport question;
	
	@Column(name = "ANSWER_EN")
	private String answerEn;
	
	@Column(name = "ANSWER_AR")
	private String answerAr;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public QuestionImport getQuestion() {
		return question;
	}

	public void setQuestion(QuestionImport question) {
		this.question = question;
	}

	public String getAnswerEn() {
		return answerEn;
	}

	public void setAnswerEn(String answerEn) {
		this.answerEn = answerEn;
	}

	public String getAnswerAr() {
		return answerAr;
	}

	public void setAnswerAr(String answerAr) {
		this.answerAr = answerAr;
	}

}
