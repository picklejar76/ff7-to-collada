package com.kujatas.model;

import java.util.HashMap;
import java.util.Map;

import static com.kujatas.model.FF7Character.*;

public class FF7File {
	
	//public static String BASE_DIRECTORY = "c:\\ff7_models\\";
	public static String BASE_DIRECTORY = "C:\\git\\ff7\\ff7\\unpacked\\field\\char.lgp\\";
	//public static String BASE_TEXTURE_DIRECTORY = "c:\\ff7_models\\textures\\";
	private static Map<FF7Character, String> HRC_FILENAME_MAP;
	private static Map<ToonAnimation, Map<Integer, String>> A_FILENAME_MAP;
	
	static {
		HRC_FILENAME_MAP = new HashMap<FF7Character, String>();
		HRC_FILENAME_MAP.put(CLOUD, "aaaa");
		HRC_FILENAME_MAP.put(TIFA, "aagb");
		HRC_FILENAME_MAP.put(CID, "abda");
		HRC_FILENAME_MAP.put(YUFFIE, "abjb");
		HRC_FILENAME_MAP.put(BARRETT, "acgd");
		HRC_FILENAME_MAP.put(RED_XIII, "adda");
		HRC_FILENAME_MAP.put(CAIT_SITH, "aebc");
		HRC_FILENAME_MAP.put(VINCENT, "aehd");
		HRC_FILENAME_MAP.put(AERIS, "auff");
		
		A_FILENAME_MAP = new HashMap<ToonAnimation, Map<Integer, String>>();
		// STAND animations
		Map<Integer, String> standMap = new HashMap<Integer, String>();
		standMap.put(21, "aafe"); // or acfe?
		A_FILENAME_MAP.put(ToonAnimation.STAND, standMap);
		
		// WALK animations
		Map<Integer, String> walkMap = new HashMap<Integer, String>();
		walkMap.put(0, "");
		A_FILENAME_MAP.put(ToonAnimation.WALK, walkMap);
		
		// RUN animations
		Map<Integer, String> runMap = new HashMap<Integer, String>();
		runMap.put(21, "aaga"); // or adcd
		A_FILENAME_MAP.put(ToonAnimation.RUN, runMap);
		
		// JUMP animations
		Map<Integer, String> jumpMap = new HashMap<Integer, String>();
		jumpMap.put(0, "");
		A_FILENAME_MAP.put(ToonAnimation.JUMP, jumpMap);
		
		/* add these later
		FOR CLOUD/21:
		  MOVEMENT:
		stand:          aafe
		run:            aaga adcd
		walk:           bajc bkba or cxbf
		jump???:        aqbe
		loooong jump:   btce
		walk backwards: byif
		climb:          afvd or bygc, cmhf
		turn around:    cafb
		fall on back:   cweb
		lay still:      cmdc
		  EMOTES:
		laugh:          asic, caha
		angry:          agfd once plus agfe repeating
		cry:            cwec
		yes:            ajie ajff ajdc
		no:             ajga ajgb ajdd
		shrug:          apda, apjf,aqbc,cddf,crib
		salute:         apjc
		giggle:         apdc
		take stand:     agee
		scratch head:   adcf
		  BATTLE:
		battle stance:  cudd
		battle victory: ajid ajeb
		swing sword:    cued
		magic cast:     csec,cuec,cuef
		punch:          bida
		hold sword up:  cuee
		left side kick: bxbd
		*/
	}
	
	public static String getHrcBaseFilename(FF7Character ff7Character) {
		return HRC_FILENAME_MAP.get(ff7Character);
	}
	
	public static String getAnimationBaseFilename(FF7Character ff7Character, ToonAnimation ff7Animation) {
		int numBones = ff7Character.getNumBones();
		if (numBones == 21) { // e.g. Cloud, Barrett, Cid
			switch(ff7Animation) {
				case STAND: return "aafe";
				case RUN: return "aaga";
				default: return "aafe";
			}
		} else if (numBones == 24) { // e.g. Tifa, Yuffie
			switch(ff7Animation) {
				case STAND: return "abcd";
				case RUN: return "abce";
			}
		}
		else if (numBones == 29) { // e.g. Red XIII
			switch(ff7Animation) {
				case STAND: return "aeae";
				case RUN: return "aeba";
			}
		} else if (numBones == 23) { // e.g. Aeris
			switch(ff7Animation) {
				case STAND: return "anhd";
				case RUN: return "aojf";
			}
		} else if (numBones == 25) { // e.g. Vincent
			switch(ff7Animation) {
				case STAND: return "afdf";
				case RUN: return "afeb";
			}
		} else if (numBones == 28) { // e.g. Cait Sith
			switch(ff7Animation) {
				case STAND: return "aeha";
				case RUN: return "avcf";
			}
		}
		return null;
	}
}
