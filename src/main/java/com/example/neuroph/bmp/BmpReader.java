package com.example.neuroph.bmp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BmpReader {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		BufferedImage image=ImageIO.read(new File("E:\\SOHO_Working\\写书\\深入浅出神经网络_电子版\\res\\writehands\\1.bmp"));
		for(int j=0;j<image.getHeight();j++){
			for(int i=0;i<image.getWidth();i++){
				int k=isBlank(image,i,j);
				System.out.print(k+" ");
			}
			System.out.println();
		}
	}

	public static double[] convertBmp2Inputs(String filename) throws IOException{
		//读取图片信息
		BufferedImage image=ImageIO.read(new File(filename));
		double[] re=new double[256];
		//将2维数组映射到1维数组
		for(int j=0;j<image.getHeight();j++){
			for(int i=0;i<image.getWidth();i++){
				//该点是否为白色
				int k=isBlank(image,i,j);
				re[i*16+j] = k;
			}
		}
		return re;
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static double[] convertBmp2Outputs(String filename) throws IOException{
		int i=filename.indexOf('.');
		int j=filename.lastIndexOf('\\');
		int number=Integer.parseInt(filename.substring(j+1, i));
		double[] re=new double[10];
		re[number]=1;
		return re;
	}
	
	public static int isBlank(BufferedImage image, int x, int y){
		int[] rgb=getRGB(image,x,y);
		if(rgb[0]==0 && rgb[0]==0 && rgb[0]==0)
			return 1;
		else
			return -1;
	}
	
	public static int[] getRGB(BufferedImage image, int x, int y) {
		int[] rgb = null;
		if (image != null && x < image.getWidth() && y < image.getHeight()) {
			rgb = new int[3];
			int pixel = image.getRGB(x, y);
			rgb[0] = (pixel & 0xff0000) >> 16;
			rgb[1] = (pixel & 0xff00) >> 8;
			rgb[2] = (pixel & 0xff);
		}
		return rgb;
	}
}
