package src;

import java.math.BigInteger;

class PrimeLibraryRunner{
	public static void main(String args[]) throws Exception{
		final int libSize = Integer.parseInt(args[0]);
		final long timeIn = System.currentTimeMillis();
		//You can omit the second <BigInteger> but it gets verbose fast
		//PrimeLibrary<BigInteger> lib = new PrimeLibrary<BigInteger>(libSize, BigInteger.class);
		PrimeLibrary<BigInteger> lib = PrimeLibrary.createLibrary("", 50, BigInteger.class);
		System.out.println("Results for lib of size " + libSize);
		System.out.println("Runtime time: " + (System.currentTimeMillis() - timeIn) + "ms");
		System.out.println("Average time: " + ((System.currentTimeMillis() - timeIn) / (double)libSize) + "ms");
		try{
			System.out.println(lib.prime[80]);
			System.out.println(lib.prime[1080]);
			System.out.println(lib.prime[2080]);
			System.out.println(lib.prime[3080]);
			System.out.println(lib.prime[4080]);
			System.out.println(lib.prime[5080]);
			System.out.println(lib.prime[6080]);
			System.out.println(lib.prime[7080]);
			System.out.println(lib.prime[8080]);
		} catch (Exception e){
		}
	}
}