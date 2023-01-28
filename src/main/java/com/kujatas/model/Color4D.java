package com.kujatas.model;

public class Color4D {
	
	public byte r;
	public byte g;
	public byte b;
	public byte a;
	
	public Color4D(byte r, byte g, byte b, byte a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	@Override
	public String toString() {
		return "RGBA(" + asUnsigned(r) + ", " + asUnsigned(g) + ", " + asUnsigned(b) + ", " + asUnsigned(a) + ")";
	}
	
	public static short asUnsigned(byte b) {
		return (short)(b & 0xFF);
	}
	
	public static double toDouble(byte b) {
		short value = asUnsigned(b);
		return value / 255.0;
	}
	
	public double rAsDouble() {
		return toDouble(this.r);
	}

	public double gAsDouble() {
		return toDouble(this.g);
	}

	public double bAsDouble() {
		return toDouble(this.b);
	}
}
