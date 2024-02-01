package com.kickbrain.db.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * The persistent class for the USERS database table.
 * 
 */
@Entity
@Table(name = "USERS")
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator( 
	        name = "UsersSeqStore", 
	        table = "SEQUENCE_STORE", 
	        pkColumnName = "SEQ_NAME", 
	        pkColumnValue = "USERS_SEQ", 
	        valueColumnName = "SEQ_VALUE", 
	        initialValue = 4, 
	        allocationSize = 1 )
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "UsersSeqStore")
	private long id;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;
	
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	@Column(name = "TOTAL_SCORE")
	private int totalScore;
	
	@Column(name = "PREMIUM_POINTS")
	private float premiumPoints;
	
	@Column(name = "REFERRED_BY")
	private String referredBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public int getTotalScore() {
		return totalScore;
	}
	
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	
	public float getPremiumPoints() {
		return premiumPoints;
	}
	
	public void setPremiumPoints(float premiumPoints) {
		this.premiumPoints = premiumPoints;
	}
	
	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}
	
	public String getReferredBy() {
		return referredBy;
	}
	
}