package com.ailk.uap.makefile4new;

public abstract  class AbstractClassA {

	public void template() {
		String name = getName();
		System.out.println("template name = " + name);
		run();
		System.out.println("end");
	}
	
	public  String getName() {
		System.out.println("run name1");
		return "name1";
	}

	public abstract void run();
}
