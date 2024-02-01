package com.kickbrain.db.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "QUESTION_ANSWERS")
@NamedQuery(name = "QuestionAnswers.findAll", query = "SELECT q FROM QuestionAnswers q")
public class QuestionAnswers implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@TableGenerator( 
	        name = "QuestionAnswersSeqStore", 
	        table = "SEQUENCE_STORE", 
	        pkColumnName = "SEQ_NAME", 
	        pkColumnValue = "QUESTION_ANSWERS_SEQ", 
	        valueColumnName = "SEQ_VALUE", 
	        initialValue = 4, 
	        allocationSize = 1 )
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "QuestionAnswersSeqStore")
	private long id;
	
	@ManyToOne
	private Question question;
	
	@Column(name = "ANSWER_EN")
	private String answerEn;
	
	@Column(name = "ANSWER_AR")
	private String answerAr;
	
	@OneToOne
	@JoinColumn(name = "answer_id")
	private Answers answer;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
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

	public Answers getAnswer() {
		return answer;
	}

	public void setAnswer(Answers answer) {
		this.answer = answer;
	}

}
