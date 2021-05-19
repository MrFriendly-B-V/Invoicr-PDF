package dev.array21.invoicrpdf.auth;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import dev.array21.invoicrpdf.InvoicrPdf;
import dev.array21.invoicrpdf.annotations.Nullable;
import dev.array21.invoicrpdf.gson.Configuration.HmacKey;
import dev.array21.invoicrpdf.util.Utils;

public class HmacValidator {

	/**
	 * Check a HMAC authentication String
	 * Format: apiKey + ':' + base64(hmacSha256(Request Method + " /" + uri, secretKey))
	 * @param hmacString The hmac string provided by the user
	 * @param method The request method used in the request (e.g GET, POST)
	 * @param path The path used, excluding prefix '/' (E.g generate/invoice)
	 * @return Returns true if the requester is authorized. False if it is not correct, or their provided String is invalid
	 */
	@Nullable
	public static boolean validateHmac(String hmacString, String method, String path) {
		String[] parts = hmacString.split(Pattern.quote(":"));
		if(parts.length < 2) {
			return false;
		}
		
		String apiKey = parts[0];
		String rxDigest = parts[1];
		byte[] rxDigestBytes;
		try {
			 rxDigestBytes = Base64.getDecoder().decode(rxDigest.getBytes());
		} catch(IllegalArgumentException e) {
			return false;
		}
		
		String matchedSecretKey = getHmacSecret(apiKey);
		if(matchedSecretKey == null) {
			return false;
		}
		
		SecretKey macKey = new SecretKeySpec(matchedSecretKey.getBytes(), "HmacSHA256");
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA256");
		} catch(NoSuchAlgorithmException e) {
			InvoicrPdf.logErr("Unable to instantiate Mac instance: " + e.getMessage());
			InvoicrPdf.logDebug(Utils.getStackTrace(e));
			return false;
		}
		
		try {
			mac.init(macKey);
		} catch(InvalidKeyException e) {
			InvoicrPdf.logErr("Unable to init Mac: " + e.getMessage());
			InvoicrPdf.logDebug(Utils.getStackTrace(e));
			return false;
		}
		
		byte[] digest = mac.doFinal(String.format("%s / %s", method, path).getBytes());
		return digest == rxDigestBytes;
	}
	
	private static String getHmacSecret(String key) {
		for(HmacKey hmac : InvoicrPdf.getConfig().getHmacKeys()) {
			if(hmac.key.equals(key)) {
				return hmac.secret;
			}
		}
		
		return null;
	}
	
}
