package com.kickbrain.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kickbrain.beans.AnswerAdminRequest;
import com.kickbrain.beans.AnswerAdminResult;
import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.BaseResult;
import com.kickbrain.beans.GeneralLookup;
import com.kickbrain.beans.QuestionAdminRequest;
import com.kickbrain.beans.QuestionAdminResult;
import com.kickbrain.beans.SubmitQuestionRequest;
import com.kickbrain.beans.configuration.QuestionVO;
import com.kickbrain.db.service.QuestionService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private QuestionService questionService;
	
	@RequestMapping(value = "/submitQuestion", method = RequestMethod.POST, consumes="application/json")
	public BaseResult submitQuestion(@RequestBody SubmitQuestionRequest request) {
		
		GeneralLookup result = new GeneralLookup();
		
		String questionEn = request.getQuestionEn();
		String questionAr = request.getQuestionAr();
		String answersEn = request.getAnswersEn();
		String answersAr = request.getAnswersAr();
		try
		{
			if(StringUtils.isEmpty(questionAr) || StringUtils.isEmpty(questionEn) || StringUtils.isEmpty(answersEn) || StringUtils.isEmpty(answersAr))
			{
				result.setStatus(0);
			}
			else
			{
				QuestionVO question = new QuestionVO();
				question.setPromptAr(request.getQuestionAr());
				question.setPromptEn(request.getQuestionEn());
				
				question = questionService.addQuestion(question);
				
				List<AnswerVO> answers = new ArrayList<AnswerVO>();
				List<String> answersArLst = Arrays.asList(answersAr.split("\\s*,\\s*"));
				List<String> answersEnLst = Arrays.asList(answersEn.split("\\s*,\\s*"));
				for(int i=0; i<answersEnLst.size(); i++)
				{
					String answerEn = answersEnLst.get(i);
					AnswerVO answerVO = new AnswerVO();
					answerVO.setAnswerEn(answerEn);
					answerVO.setAnswerAr(answersArLst.get(i));
					answerVO.setQuestionId(question.getId());
					
					answers.add(answerVO);
				}
				question.setAnswers(answers);
				
				questionService.addQuestion(question);
				result.setStatus(1);
			}
		}
		catch(Exception ex)
		{
			result.setStatus(0);
			ex.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping(value = "/importQuestions", method = RequestMethod.POST, consumes="application/json")
	public BaseResult importQuestions(@RequestBody String file) {
		
		BaseResult result = new BaseResult();
		
		if(file.startsWith("data:"))
		{
			file = file.split(";")[1].replace("base64,", "");
		}
		byte[] excelBytes = Base64.decodeBase64(file);
		InputStream is = new ByteArrayInputStream(excelBytes);
		
		try (Workbook workbook = WorkbookFactory.create(is)) {
			
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			Row headerRow = rowIterator.next();
			Iterator<Cell> headerCells = headerRow.cellIterator();
			Map<Integer, String> headers = new HashMap<Integer, String>();
			while (headerCells.hasNext()) {

				Cell headerCell = headerCells.next();
				String headerValue = headerCell.getStringCellValue().trim().replaceAll("\\s+", "_");
				if(!StringUtils.isEmpty(headerValue))
				{
					// replace white space characters with empty string
					headerValue = headerValue.replaceAll("[\\\\\"'/?+!@.$#]", "");
					headers.put(headerCell.getColumnIndex(), headerValue);
				}
			}

			JSONArray records = new JSONArray();
			FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

			while (rowIterator.hasNext()) {
				 
				JSONObject record = new JSONObject();

				Row excelRow = rowIterator.next();
				int lastColumn = excelRow.getLastCellNum();
				for (int cn = 0; cn < lastColumn; cn++) {
					Cell cell = excelRow.getCell(cn, MissingCellPolicy.RETURN_BLANK_AS_NULL);
					
					if(cell != null)
					{
						String cellValue = "";
						CellType cellType = cell.getCellType();
						switch(cellType)
						{
							case STRING:
							{
								cellValue = cell.getStringCellValue();
								break;
							}
							case NUMERIC:
							{
								cellValue = String.valueOf(cell.getNumericCellValue());
								break;
							}
							case FORMULA:
							{
								CellValue cellValueObj = formulaEvaluator.evaluate(cell);
				                switch (cellValueObj.getCellType()) {
				                    case STRING:
				                    	cellValue = cellValueObj.getStringValue();
				                        // Process stringValue as needed
				                        break;
				                    // Handle other cell types as required
				                }
							}
							default:
							{
								System.out.println("Cell type = " + cellType + " for index " +cn );
								break;
							}
						}
						
						if(!cellValue.isEmpty())
						{
							String header = headers.get(cell.getColumnIndex());
							if(header != null)
							{
								record.put(header, cellValue);
							}
						}
					}
				}

				if(!record.isEmpty())
				{
					records.put(record);
				}
			}
			
			// Flush the import tables
			questionService.flushQuestionImports();
			
			Iterator<Object> iterator = records.iterator();
			while(iterator.hasNext())
			{
				JSONObject record = (JSONObject)iterator.next();
				
				QuestionVO question = new QuestionVO();
				question.setPromptAr(record.getString("Question_Ar"));
				question.setPromptEn(record.getString("Question_En"));
				question.setCategory(record.getInt("Category"));
				
				question = questionService.importQuestion(question);
				
				String answersAr = record.getString("Answer_Ar");
				String answersEn = record.getString("Answer_En");
				List<AnswerVO> answers = new ArrayList<AnswerVO>();
				List<String> answersArLst = Arrays.asList(answersAr.split("\\s*,\\s*"));
				List<String> answersEnLst = Arrays.asList(answersEn.split("\\s*,\\s*"));
				for(int i=0; i<answersEnLst.size(); i++)
				{
					String answerEn = answersEnLst.get(i);
					AnswerVO answerVO = new AnswerVO();
					answerVO.setAnswerEn(answerEn);
					System.out.println(answerEn);
					answerVO.setAnswerAr(answersArLst.get(i));
					answerVO.setQuestionId(question.getId());
					
					answers.add(answerVO);
				}
				question.setAnswers(answers);
				
				questionService.importQuestion(question);
			}
			
			result.setStatus(1);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			result.setStatus(0);
		}
		finally{
		}
		
		return result;
	}
	
	@RequestMapping(value = "/createQuestion", method = RequestMethod.POST, consumes="application/json")
	public BaseResult createQuestion(@RequestBody QuestionAdminRequest request) {
		
		BaseResult result = new BaseResult();
		try
		{
			if(StringUtils.isEmpty(request.getDescriptionAr()) || StringUtils.isEmpty(request.getDescriptionEn()) || request.getAnswers().isEmpty() || request.getCategory() == 0 || request.getTag() == 0)
			{
				result.setStatus(0);
			}
			else
			{
				QuestionVO question = new QuestionVO();
				question.setPromptAr(request.getDescriptionAr());
				question.setPromptEn(request.getDescriptionEn());
				question.setCategory(request.getCategory());
				question.setTag(request.getTag());
				
				question = questionService.addQuestion(question);
				
				List<AnswerVO> answers = new ArrayList<AnswerVO>();
				for(int answerId : request.getAnswers())
				{
					AnswerVO answerVO = new AnswerVO();
					answerVO.setAnswerId(answerId);
					answerVO.setQuestionId(question.getId());
					
					answers.add(answerVO);
				}
				question.setAnswers(answers);
				
				questionService.addQuestion(question);
				
				result.setStatus(1);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			result.setStatus(0);
		}
		
		return result;
	}
	
	@RequestMapping(value = "/saveQuestion", method = RequestMethod.POST, consumes="application/json")
	public BaseResult saveQuestion(@RequestBody QuestionAdminRequest request) {
		
		BaseResult result = new BaseResult();
		try
		{
			if(StringUtils.isEmpty(request.getDescriptionAr()) || StringUtils.isEmpty(request.getDescriptionEn()) || request.getAnswers().isEmpty() || request.getCategory() == 0 || request.getTag() == 0)
			{
				result.setStatus(0);
			}
			else
			{
				QuestionVO question = new QuestionVO();
				
				if(request.getId() != null)
				{
					question.setId(request.getId());
				}
				question.setPromptAr(request.getDescriptionAr());
				question.setPromptEn(request.getDescriptionEn());
				question.setCategory(request.getCategory());
				question.setTag(request.getTag());
				
				if(request.getId() == null)
				{
					question = questionService.addQuestion(question);
				}
				
				List<AnswerVO> answers = new ArrayList<AnswerVO>();
				for(int answerId : request.getAnswers())
				{
					AnswerVO answerVO = new AnswerVO();
					answerVO.setAnswerId(answerId);
					answerVO.setQuestionId(question.getId());
					
					answers.add(answerVO);
				}
				question.setAnswers(answers);
				
				questionService.addQuestion(question);
				
				result.setStatus(1);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			result.setStatus(0);
		}
		
		return result;
	}
	
	@RequestMapping(value = "/questions", method = RequestMethod.GET)
	public List<QuestionAdminResult> retrieveQuestions(@RequestParam(name="search",required=false) String search, @RequestParam(name="offset",required=false) Integer offset, @RequestParam(name="max",required=false) Integer max) {
		
		List<QuestionAdminResult> questionsResult = new ArrayList<QuestionAdminResult>();
		try
		{
			List<QuestionVO> questions = questionService.retrieveAllQuestions(search, offset, max);
			for(QuestionVO question : questions)
			{
				QuestionAdminResult questionResult = new QuestionAdminResult();
				questionResult.setId(question.getId());
				questionResult.setDescriptionAr(question.getPromptAr());
				questionResult.setDescriptionEn(question.getPromptEn());
				questionResult.setCategory(question.getCategory());
				questionResult.setTag(question.getTag());
				questionResult.setAnswers(question.getAnswers());
				
				String answersEn = "";
				String answersAr = "";
				for(AnswerVO answerVO : question.getAnswers())
				{
					if(!answersEn.isEmpty())
					{
						answersEn += ",";
					}
					if(!answersAr.isEmpty())
					{
						answersAr += ",";
					}
					answersEn += answerVO.getAnswerEn();
					answersAr += answerVO.getAnswerAr();
				}
				questionResult.setAnswersAr(answersAr);
				questionResult.setAnswersEn(answersEn);
				
				questionsResult.add(questionResult);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return questionsResult;
	}

	@RequestMapping(value = "/answers", method = RequestMethod.GET)
	public List<AnswerAdminResult> retrieveAnswers(@RequestParam(name="search",required=false) String search, @RequestParam(name="offset",required=false) Integer offset, @RequestParam(name="max",required=false) Integer max) {
		
		List<AnswerAdminResult> answersResult = new ArrayList<AnswerAdminResult>();
		try
		{
			List<AnswerVO> answers = questionService.retrieveAllAnswers(search, offset, max);
			for(AnswerVO answer : answers)
			{
				AnswerAdminResult answerResult = new AnswerAdminResult();
				answerResult.setId(answer.getId());
				answerResult.setNameAr(answer.getAnswerAr());
				answerResult.setNameEn(answer.getAnswerEn());
				answerResult.setType(answer.getType());
				
				answersResult.add(answerResult);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return answersResult;
	}
	
	@RequestMapping(value = "/saveAnswer", method = RequestMethod.POST, consumes="application/json")
	public BaseResult saveAnswer(@RequestBody AnswerAdminRequest request) {
		
		BaseResult result = new BaseResult();
		try
		{
			if(StringUtils.isEmpty(request.getNameAr()) || StringUtils.isEmpty(request.getNameEn()) || request.getType() == 0)
			{
				result.setStatus(0);
			}
			else
			{
				AnswerVO answer = new AnswerVO();
				
				if(request.getId() != null)
				{
					answer.setId(request.getId());
				}
				answer.setAnswerAr(request.getNameAr());
				answer.setAnswerEn(request.getNameEn());
				answer.setType(request.getType());
				
				questionService.addAnswer(answer);
				
				result.setStatus(1);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			result.setStatus(0);
		}
		
		return result;
	}
	
}
