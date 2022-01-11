package com.example.neuroph.util;

public class Utils {
	/**
	 * 将活跃度最高的
	 * @param d
	 * @return
	 */
	public static double[] competition(double[] d){
		double[] output=d;
		double[] re=new double[output.length];
		int maxIndex=0;
		double maxValue=Double.MIN_VALUE;
		for(int i=0;i<output.length;i++){
			if(output[i] > maxValue){
				maxIndex=i;
				maxValue=output[i];
			}
		}
		for(int i=0;i<re.length;i++){
			if(i==maxIndex){
				re[i]=1;
			}else{
				re[i]=0;
			}
		}
        return re;
	}
}
