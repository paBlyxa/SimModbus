package com.we.simModbus.test;


import org.junit.Test;

public class TestConcurrence {

	private volatile int count = 0;
	
	@Test
	public void test() {
		Thread anotherThread = new Thread(){
			
			@Override
			public void run(){
				count(1);
			}
		};
		anotherThread.start();
		count(0);
	}
	
	private void count(int numThread){
		for (int i = 0; i < 100; i++){
			if (numThread == 0){
				count++;
				System.out.println("MainThread - count: " + count);
			} else {
				count = 0;
				System.out.println("AnotherThread - count: " + count);
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
