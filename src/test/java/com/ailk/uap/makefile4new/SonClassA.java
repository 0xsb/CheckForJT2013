package com.ailk.uap.makefile4new;

public class SonClassA extends AbstractClassA{

	@Override
	public void run() {
		System.out.println("run.......");
	}

	public  String getName() {
		System.out.println("run name2");
		return "name2";
	}
	
	public static void main(String []args) {
		AbstractClassA AbstractClassA = new SonClassA();
		AbstractClassA.template();
	}
	
}
