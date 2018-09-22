package com.paymentez.android.rest.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CardBinResponse{

	@SerializedName("carrier")
	private String carrier;

	@SerializedName("url_logo_png")
	private String urlLogoPng;

	@SerializedName("url_logo")
	private String urlLogo;

	@SerializedName("installments_options")
	private List<Object> installmentsOptions;

	@SerializedName("cvv_length")
	private int cvvLength;

	@SerializedName("card_mask")
	private String cardMask;

	@SerializedName("card_type")
	private String cardType;

	@SerializedName("otp")
	private boolean otp;


	public void setCarrier(String carrier){
		this.carrier = carrier;
	}

	public String getCarrier(){
		return carrier;
	}

	public void setUrlLogoPng(String urlLogoPng){
		this.urlLogoPng = urlLogoPng;
	}

	public String getUrlLogoPng(){
		return urlLogoPng;
	}

	public void setUrlLogo(String urlLogo){
		this.urlLogo = urlLogo;
	}

	public String getUrlLogo(){
		return urlLogo;
	}

	public void setInstallmentsOptions(List<Object> installmentsOptions){
		this.installmentsOptions = installmentsOptions;
	}

	public List<Object> getInstallmentsOptions(){
		return installmentsOptions;
	}

	public void setCvvLength(int cvvLength){
		this.cvvLength = cvvLength;
	}

	public int getCvvLength(){
		return cvvLength;
	}

	public void setCardMask(String cardMask){
		this.cardMask = cardMask;
	}

	public String getCardMask(){
		return cardMask;
	}

	public void setCardType(String cardType){
		this.cardType = cardType;
	}

	public String getCardType(){
		return cardType;
	}

	public boolean isOtp() {
		return otp;
	}

	public void setOtp(boolean otp) {
		this.otp = otp;
	}

	@Override
	public String toString() {
		return "CardBinResponse{" +
				"carrier='" + carrier + '\'' +
				", urlLogoPng='" + urlLogoPng + '\'' +
				", urlLogo='" + urlLogo + '\'' +
				", installmentsOptions=" + installmentsOptions +
				", cvvLength=" + cvvLength +
				", cardMask='" + cardMask + '\'' +
				", cardType='" + cardType + '\'' +
				", otp='" + otp + '\'' +
				'}';
	}
}