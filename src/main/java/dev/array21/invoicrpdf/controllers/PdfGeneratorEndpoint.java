package dev.array21.invoicrpdf.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import dev.array21.invoicrpdf.InvoicrPdf;
import dev.array21.invoicrpdf.Pair;
import dev.array21.invoicrpdf.gson.FileIdResponse;
import dev.array21.invoicrpdf.gson.ErrorResponse;
import dev.array21.invoicrpdf.gson.PdfInvoiceRequest;
import dev.array21.invoicrpdf.pdf.InvoiceGenerator;
import dev.array21.invoicrpdf.util.Utils;

@RestController
@RequestMapping("/pdf")
public class PdfGeneratorEndpoint {
	
	private static final Gson GSON = new Gson();
	
	@RequestMapping(value = "invoice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> generateInvoice(@RequestBody String bodyRaw) {
		PdfInvoiceRequest body = GSON.fromJson(bodyRaw, PdfInvoiceRequest.class);
		Pair<Boolean, String> validation = Utils.validateType(body);
		if(validation.getA() == null) {
			return new ResponseEntity<String>(GSON.toJson(new ErrorResponse(validation.getB())), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(!validation.getA()) {
			return new ResponseEntity<String>(GSON.toJson(new ErrorResponse(validation.getB())), HttpStatus.BAD_REQUEST);
		}
		
		if(!InvoicrPdf.getConfig().getApiKeys().contains(body.apiKey)) {
			return new ResponseEntity<String>(GSON.toJson(new ErrorResponse("Invalid apiKey")), HttpStatus.FORBIDDEN);
		}
		
		String fileId = Utils.randomString(16);
		new Thread(new Runnable() {	
			@Override
			public void run() {
				new InvoiceGenerator(body).generate(fileId);
			}
		}, "PDFGenerationThread-" + fileId).start();
		
		return new ResponseEntity<String>(GSON.toJson(new FileIdResponse(fileId)), HttpStatus.OK);
	}
}
