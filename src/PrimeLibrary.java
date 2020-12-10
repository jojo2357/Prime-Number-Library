package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Scanner;

/*
NOTE: PARSING INTO STRING IS SUPPORTED!!! 
Change all istances of <T extends Number> to <T> and <t extends Number> and remove the following:

if (!Number.class.isAssignableFrom(clazz))
	throw new RuntimeException("Class " + clazz.getName() + " is not valid (must extend java.lang.Number)");
*/
@SuppressWarnings("unchecked")
public class PrimeLibrary<T extends Number> {

	final public T[] prime;
	/*
	 * index 0 = 1 so if you think that 1 is trivial, just pretend it is
	 * one-indexed. When inputting length, it makes an array of length + 1. ie. if
	 * you want the 50th prime, you need length = 50 and index 50 (prime[50])
	 */

	private static final String DEFAULT_DIRECTORY = System.getProperty("user.dir");

	public static final BigInteger ELEVEN = BigInteger.valueOf(11);
	public static final BigInteger SEVEN = BigInteger.valueOf(7);
	public static final BigInteger SIX = BigInteger.valueOf(6);
	public static final BigInteger FIVE = BigInteger.valueOf(5);
	public static final BigInteger FOUR = BigInteger.valueOf(4);
	public static final BigInteger THREE = BigInteger.valueOf(3);// Used in calculating big primes.
	public static final BigInteger TWO = BigInteger.valueOf(2);// Better to only create one, and it can be used elsewhere!
	public static final BigInteger ONE = BigInteger.valueOf(1);
	public static final BigInteger ZERO = BigInteger.valueOf(0);

	public PrimeLibrary(final int size, final Class<T> clazz) throws IOException,
			NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		this(DEFAULT_DIRECTORY, size, clazz);
	}

	/*
	 * Parses library data
	 */
	public PrimeLibrary(String DIR, final int size, final Class<T> clazz) throws NoSuchMethodException, SecurityException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (size <= 0)
			throw new IllegalStateException("Library size invalid");
		if (DIR.isEmpty())
			DIR = DEFAULT_DIRECTORY;
		if (!Number.class.isAssignableFrom(clazz))
			throw new RuntimeException("Class " + clazz.getName() + " is not valid (must extend java.lang.Number)");
		prime = (T[]) Array.newInstance(clazz, size + 1);
		buildLibrary(DIR, size, clazz.getConstructor(String.class));
	}

	/*
	 * Generates and saves Primes in the CWD
	 */
	private void buildLibrary(String DIR, final int length, final Constructor<T> c) throws IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final File primeFile = new File(DIR + "\\PrimeLibrary.csv");
		int index = 0;
		if (primeFile.exists()) {
			final Scanner recycler = new Scanner(primeFile);
			recycler.useDelimiter(",");
			while (recycler.hasNext()) {
				prime[index++] = (T) c.newInstance(recycler.next());
				if (index == prime.length){
					recycler.close();
					return;
				}
			}
			System.out.println("I only found " + index + " primes in " + DIR + "\\PrimeLibrary.csv");
			recycler.close();
			BigInteger lastNumber = (BigInteger)prime[index - 1];
			StringBuilder primeStuff = new StringBuilder();
			for (int i = 0; i < index; i++)
				primeStuff.append(i).append(",");
			for (BigInteger i = lastNumber.mod(SIX).compareTo(FIVE) == 0 ? lastNumber.add(SIX) : lastNumber.add(FOUR); index <= length; i = i.add(SIX)) {
				try {
					if (isPrime(i)) {
						prime[index++] = (T) c.newInstance(i.toString());
						primeStuff.append(i).append(",");
					}
					if (isPrime(i.add(TWO))) {
						if (index < length)
							prime[index++] = (T) c.newInstance(i.add(TWO).toString());
						primeStuff.append(i.add(TWO)).append(",");
					}
				} catch (NumberFormatException e){
					System.out.println("WARNING: prime # " + index + " is too large so all instances after this index is null");
					break; //continue and save data
				}
			}
			try{
				final FileWriter primeWriter = new FileWriter(primeFile);
				primeWriter.append(primeStuff.toString());
				primeWriter.close();
			}catch (Exception e){
			}
			return;
		}
		if (length > 4){
			prime[4] = (T) c.newInstance("7");
			prime[3] = (T) c.newInstance("5");
			prime[2] = (T) c.newInstance("3");
			prime[1] = (T) c.newInstance("2");
			prime[0] = (T) c.newInstance("1");
		}else
			switch(length){
				case 4: prime[4] = (T) c.newInstance("7");
				case 3: prime[3] = (T) c.newInstance("5");
				case 2: prime[2] = (T) c.newInstance("3");
				case 1: prime[1] = (T) c.newInstance("2"); prime[0] = (T) c.newInstance("1"); return;
				default: throw new RuntimeException("How the hell did I miss that? bad lib size");
			}
		StringBuilder primeText = new StringBuilder();
		primeText.append("1,2,3,5,7,");
		index = 5;
		for (BigInteger i = ELEVEN; index <= length; i = i.add(SIX)) {
			try{
				if (isPrime(i)) {
					prime[index++] = (T) c.newInstance(i.toString());
					primeText.append(i).append(",");
				}
				if (isPrime(i.add(TWO))) {
					if (index < length)
						prime[index++] = (T) c.newInstance(i.add(TWO).toString());
					primeText.append(i.add(TWO)).append(",");
				}
			} catch (NumberFormatException e){
				System.out.println("WARNING: prime # " + index + " is too large so all instances after this index is null");
				break; //continue and save data
			}
		}
		try{
			primeFile.createNewFile();
			final FileWriter primeWriter = new FileWriter(primeFile);
			primeWriter.append(primeText.toString());
			primeWriter.close();
		}catch (IOException e){
			System.out.println("AN ERROR OCURRED USING " + DIR + "\\PrimeLibrary.csv!!! This will only affect the runtime length because PrimeNumberLibrary will have to recalculate all of the primes instead of picking up where it left off");
		}
	}

	//Keep those eyes moving nothing to see here
	@Deprecated
	public static <t extends Number> PrimeLibrary<t> buildAndReturnLibrary(final String DIR, final int length, final Class<t> clazz) throws IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//buildLibrary(DIR, length);
		return new PrimeLibrary<t>(DIR, length, clazz);
	}

	/*
	 * Nothing revolutionary. True = argument is prime
	 */
	private static boolean isPrime(final BigInteger sum) throws IllegalArgumentException {
		if (sum.mod(FIVE).compareTo(ZERO) == 0) 
			return false;
		if (sum.mod(SEVEN).compareTo(ZERO) == 0) 
			return false;
		final BigInteger goal = bsqrt(sum);
		if (goal.multiply(goal).compareTo(sum) == 0)//if perfect square, go home
			return false;
		for (BigInteger i = ELEVEN; goal.compareTo(i) >= 0 && i.compareTo(sum) != 0; i = i.add(SIX)) 
			if (sum.mod(i).compareTo(ZERO) == 0 || sum.mod(i.add(TWO)).compareTo(ZERO) == 0) 
				return false;
		return true;
	}

	//BigInteger has a native sqrt function is VS code but in command line no so heres the manual one that will ensure the limiting factor is your computer's specs
	private static BigInteger bsqrt(final BigInteger x) throws IllegalArgumentException {
		if (x.compareTo(BigInteger.ZERO) < 0)
			throw new IllegalArgumentException("Negative argument.");
		// square roots of 0 and 1 are trivial and
		// y == 0 will cause a divide-by-zero exception
		if (x == BigInteger.ZERO || x == BigInteger.ONE)
			return x;
		BigInteger y;
		// starting with y = x / 2 avoids magnitude issues with x squared
		for (y = x.divide(TWO); y.compareTo(x.divide(y)) > 0; y = ((x.divide(y)).add(y)).divide(TWO));
		if (x.compareTo(y.multiply(y)) == 0)
			return y;
		else
			return y.add(BigInteger.ONE);
	}
}
