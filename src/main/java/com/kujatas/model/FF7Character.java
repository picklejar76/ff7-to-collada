package com.kujatas.model;

import java.io.File;

public enum FF7Character {
	
	CLOUD("Cloud", "Cloud Strife", 21),
	BARRETT("Barrett", "Barrett Wallace", 21),
	TIFA("Tifa", "Tifa Lockhart", 24),
	AERIS("Aeris", "Aeris Gainsborough", 23),
	RED_XIII("Red", "Red XIII", 29),
	CID("Cid", "Cid Highwind", 21),
	CAIT_SITH("Cait Sith", "Cait Sith", 28),
	YUFFIE("Yuffie", "Yuffie Kisaragi", 24),
	VINCENT("Vincent", "Vincent Valentine", 25);
	
	private String shortName;
	private String longName;
	private int numBones;
	
	private FF7Character(String shortName, String longName, int numBones) {
		this.shortName = shortName;
		this.longName = longName;
		this.numBones = numBones;
	}
	
	public String getShortName() {
		return this.shortName;
	}
	
	public String getLongName() {
		return this.longName;
	}
	
	public int getNumBones() {
		return this.numBones;
	}
}
