package com.kickbrain.controller;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ModelAttribute
	public void setGlobalAttributes(Locale locale, Model model, HttpSession session) {
		session.setAttribute("test", "test");
	}
}