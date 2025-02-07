package com.tcube.api.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptorUtil {
	

//	private static final Logger logger = (Logger) LogManager.getLogger(PropertiesConfig.class);
	/**
	 * This method is used to encrypt the string.
	 *
	 * @param textToEncrypt
	 *            the text to encrypt
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public static String encryptPropertyValue(String textToEncrypt) throws Exception {
//		logger.info("EncryptorUtil(encryptPropertyValue) >> Entry");
//		logger.debug("EncryptorUtil(encryptPropertyValue) >> Request Details {}" + textToEncrypt);
		final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("tcube#1");
		final String encryptedPassword = encryptor.encrypt(textToEncrypt);
//		logger.info("EncryptorUtil(encryptPropertyValue) >> Exit");
		return encryptedPassword;
	}
	/**
	 * This method is used to decrypt the string.
	 *
	 * @param textTodecrypt
	 *            the text todecrypt
	 * @return the string
	 * @throws ConfigurationException
	 *             the configuration exception
	 */
	public static String decryptPropertyValue(String textTodecrypt) throws Exception {
//		logger.info("EncryptorUtil(decryptPropertyValue) >> Entry");
//		logger.debug("EncryptorUtil(decryptPropertyValue) >> Request Details {}" + textTodecrypt);
		final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("tcube#1");
		final String decryptedPropertyValue = encryptor.decrypt(textTodecrypt);
//		logger.info("EncryptorUtil(decryptPropertyValue) >> Exit");
		return decryptedPropertyValue;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(EncryptorUtil.decryptPropertyValue("1BnO42MHZfGtdF1h43cmRXy0ipy8WLZY"));
	}



}
