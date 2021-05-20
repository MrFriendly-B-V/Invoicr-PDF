package dev.array21.invoicrpdf.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import dev.array21.invoicrpdf.InvoicrPdf;
import dev.array21.invoicrpdf.Pair;
import dev.array21.invoicrpdf.auth.HmacValidator;
import dev.array21.invoicrpdf.gson.requests.GetPdfRequest;
import dev.array21.invoicrpdf.util.Utils;

@RestController
@RequestMapping("/pdf")
public class GetPdfEndpoint {
	
	private static final Gson GSON = new Gson();
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
	public @ResponseBody ResponseEntity<byte[]> getPdf(@RequestBody String bodyRaw, @RequestHeader(value = "X-Hmac-Authorization", required = false) String hmacHeader) {
		if(hmacHeader == null) {
			return new ResponseEntity<byte[]>(new byte[0], HttpStatus.UNAUTHORIZED);
		}
		
		GetPdfRequest body = GSON.fromJson(bodyRaw, GetPdfRequest.class);
		Pair<Boolean, String> validation = Utils.validateType(body);
		if(validation.getA() == null) {
			return new ResponseEntity<byte[]>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(!validation.getA()) {
			return new ResponseEntity<byte[]>(new byte[0], HttpStatus.BAD_REQUEST);
		}
		
		if(!HmacValidator.validateHmac(hmacHeader, "POST", "pdf")) {
			return new ResponseEntity<byte[]>(new byte[0], HttpStatus.FORBIDDEN);
		}
		
		String pdfType = null;
		switch(body.type) {
		case INVOICE:
			pdfType = "invoice";
			break;
		case QUOTE:
			pdfType = "quote";
			break;
		}
		
		File pdfFile = new File(InvoicrPdf.getConfig().outDir, String.format("%s.%s.pdf", pdfType, body.id));
		if(!pdfFile.exists()) {
			return new ResponseEntity<byte[]>(new byte[0], HttpStatus.NOT_FOUND);
		}
		
		byte[] fileContent;
		try {
			fileContent = Files.readAllBytes(pdfFile.toPath());
		} catch(IOException e) {
			InvoicrPdf.logErr(String.format("Failed to read PDF file '%s.%s.pdf': %s", pdfType, body.id, e.getMessage()));
			InvoicrPdf.logDebug(Utils.getStackTrace(e));
			
			return new ResponseEntity<byte[]>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<byte[]>(fileContent, HttpStatus.OK);
		
	}
}
