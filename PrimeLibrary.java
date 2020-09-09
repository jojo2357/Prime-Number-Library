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

public class PrimeLibrary<T extends Number> {

	public T[] prime;
	/*
	 * index 0 = 1 so if you think that 1 is trivial, just pretend it is
	 * one-indexed. When inputting length, it makes an array of length + 1. ie. if
	 * you want the 50th prime, you need length 50 and index 50 (prime[50])
	 */

	private static final String DEFAULT_DIRECTORY = System.getProperty("user.dir");

	public static final BigInteger THREE = new BigInteger("3");// Used in calculating big primes.
	public static final BigInteger TWO = new BigInteger("2");// Better to only create one, and it can be used elsewhere!
	public static final BigInteger ONE = new BigInteger("1");
	public static final BigInteger ZERO = new BigInteger("0");

	/*
	 * Parses library data
	 */
	@SuppressWarnings("unchecked")
	public <t extends Number> PrimeLibrary(String DIR, final int size, final Class<t> clazz)
			throws NoSuchMethodException, SecurityException, IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (!Number.class.isAssignableFrom(clazz))
			throw new RuntimeException("Class " + clazz.getName() + " is not valid (must extend java.lang.Number)");

		prime = (T[]) Array.newInstance(clazz, size + 1);

		if (DIR.isEmpty())
			DIR = DEFAULT_DIRECTORY;

		File primeFile = new File(DIR + "\\PrimeLibrary.csv");
		if (!primeFile.exists())
			buildLibrary(DIR, size);
		Scanner primeFileParser = new Scanner(primeFile);
		final Constructor<t> c = clazz.getConstructor(String.class);
		int index = 0;
		if (primeFileParser.hasNext()) {
			String data = primeFileParser.next();
			String[] dataIn = data.split(",");
			if (dataIn.length < size) {// if it wasn't long enough, remake it and reread it
				primeFileParser.close();
				buildLibrary(DIR, size);
				primeFile = new File(DIR + "\\PrimeLibrary.csv");
				primeFileParser = new Scanner(primeFile);
				dataIn = primeFileParser.next().split(",");
			}
			for (final String number : dataIn) {
				if (index == prime.length)
					break;
				prime[index++] = (T) c.newInstance(number);
			}
		}
		primeFileParser.close();
	}

	/*
	 * Generates and saves Primes in the CWD
	 */
	public static void buildLibrary(String DIR, final int length) throws IOException {
		if (DIR.isEmpty())
			DIR = DEFAULT_DIRECTORY;
		final File primeFile = new File(DIR + "\\PrimeLibrary.csv");
		if (primeFile.exists()) {
			Scanner recycler = new Scanner(primeFile);
			if (recycler.hasNext()) {
				String wholeLine = recycler.nextLine();
				recycler.close();
				String[] numbers = wholeLine.split(",");
				BigInteger lastNumber = new BigInteger(numbers[numbers.length - 1]);
				final FileWriter primeWriter = new FileWriter(primeFile);
				primeWriter.append(wholeLine);
				int pos = numbers.length;
				for (BigInteger i = lastNumber.add(TWO); pos <= length; i = i.add(TWO)) {
					if (isPrime(i)) {
						primeWriter.append(i + ",");
						pos++;
					}
				}
				primeWriter.close();
				return;
			}
			recycler.close();
		}
		primeFile.createNewFile();
		final FileWriter primeWriter = new FileWriter(primeFile);
		primeWriter.append("1,2,");
		int pos = 2;
		for (BigInteger i = THREE; pos <= length; i = i.add(TWO)) {
			if (isPrime(i)) {
				primeWriter.append(i + ",");
				pos++;
			}
		}
		primeWriter.close();
	}

	public static <t extends Number> PrimeLibrary<t> buildAndReturnLibrary(final String DIR, final int length,
			final Class<t> clazz) throws IOException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		buildLibrary(DIR, length);
		return new PrimeLibrary<t>(DIR, length, clazz);
	}

	/*
	 * Nothing revolutionary. True = argument is prime
	 */
	private static boolean isPrime(final BigInteger sum) {
		if (sum.compareTo(ONE) == 0) {
			return false;
		}
		if (sum.compareTo(TWO) == 0) {
			return true;
		}
		if (sum.mod(TWO).compareTo(ONE) != 0) {
			return false;
		}
		final BigInteger goal = sum.sqrt().add(ONE);
		for (BigInteger i = new BigInteger("3"); goal.compareTo(i) == 1; i = i.add(TWO)) {
			if (sum.mod(i).compareTo(ZERO) == 0) {
				return false;
			}
		}
		return true;
	}
}
