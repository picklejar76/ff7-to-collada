package com.kujatas.model.loader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageTester {
	
	public ImageTester() {
		
	}
	
	public void doTest() throws IOException {
		int i =0;
		int w = 450;
		int h = 350;
		int [] imageData = new int[w * h];
		for(int y =0;y<h;y++){
		for(int x=0; x<w;x++){
		int r =(x^y)&0xff;
		int g = (x*2^y*2)&0xff;
		int b = (x*4^y*4)&0xff;
		int a = (255-y/2)&0xff;
		imageData[i++] = (a<<24)|(r <<16)|(g <<8)|b;}}

		BufferedImage finalImage;	
		finalImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);	
		finalImage.setRGB(0, 0, w, h, imageData, 0 ,w);	
		ImageIO.write(finalImage, "png", new File("c:\\ff7_models\\test.png"));
	}
	
	public static void main(String[] args) throws IOException {
		new ImageTester().doTest();
	}
}
