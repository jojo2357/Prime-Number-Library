package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/*
NOTE: It is recommended that only classes extending Number are used, it is 
required that the class used has a constructor that accepts a single string argument
*/
public class PrimeLibrary<T> {

    private static final String DEFAULT_DIRECTORY = System.getProperty("user.dir");

    // Used in calculating big primes.
    // Better to only create one, and it can be used elsewhere!
    public static final BigInteger ELEVEN = BigInteger.valueOf(11);
    public static final BigInteger SEVEN = BigInteger.valueOf(7);
    public static final BigInteger SIX = BigInteger.valueOf(6);
    public static final BigInteger FIVE = BigInteger.valueOf(5);
    public static final BigInteger FOUR = BigInteger.valueOf(4);
    public static final BigInteger THREE = BigInteger.valueOf(3);
    public static final BigInteger TWO = BigInteger.valueOf(2);
    public static final BigInteger ONE = BigInteger.valueOf(1);
    public static final BigInteger ZERO = BigInteger.valueOf(0);

    /*
     * index 0 = 1 so if you think that 1 is trivial, just pretend it is
     * one-indexed. When inputting length, it makes an array of length + 1. ie. if
     * you want the 50th prime, you need length = 50 and index 50 (prime[50])
     */

    final public T[] prime;
    private List<T> primeList;
    // this is so that we can use the functions and consumers
    private int index = 0;
    private Scanner recycler;

    // Some prep here, see buildLibrary for heavy lifting
    public PrimeLibrary(final int size, final Class<T> clazz) throws IllegalArgumentException, RuntimeException {
        this(DEFAULT_DIRECTORY, size, clazz);
    }

    /*
     * Some prep here, see buildLibrary for heavy lifting
     */
    @SuppressWarnings("unchecked")
    public PrimeLibrary(String DIR, final int size, final Class<T> clazz)
            throws IllegalArgumentException, RuntimeException {
        if (size <= 0)
            throw new IllegalArgumentException("Library size invalid");
        if (DIR.isEmpty())
            DIR = DEFAULT_DIRECTORY;
        prime = (T[]) Array.newInstance(clazz, size + 1);
        try {
            buildLibrary(DIR, size, clazz.getConstructor(String.class));
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getName() + " does not have a constructor that accepts a single string");
        }
    }

    /*
     * Generates and saves Primes in the CWD If the library does not exist, it will
     * be created If the library is too large, it will simple read until filled If
     * the library is too small, it will recycle the old one to make the new one
     */
    private void buildLibrary(String DIR, final int length, final Constructor<T> c) throws RuntimeException {
        final File primeFile = new File(DIR + "\\PrimeLibrary.csv");
        StringBuilder primeText = new StringBuilder();
        recycler = null;

        // This is how we save data on exit
        Function<Boolean, Boolean> quitAndSave = (val) -> {
            try {
                primeFile.createNewFile();
                final FileWriter primeWriter = new FileWriter(primeFile);
                primeWriter.append(primeText.toString());
                primeWriter.close();
                if (recycler != null)
                    recycler.close();
            } catch (IOException ignoredIOException) {
                System.out.println("AN ERROR OCURRED USING " + DIR
                        + "\\PrimeLibrary.csv!!! This will only affect the runtime length because PrimeNumberLibrary will have to recalculate all of the primes instead of picking up where it left off");
            }
            return val;
        };

        // This creates the number from the string
        Function<String, T> constructor = numberIn -> {
            try {
                return (T) c.newInstance(numberIn);
            } catch (InstantiationException | IllegalArgumentException | InvocationTargetException
                    | IllegalAccessException e) {
                e.printStackTrace();
                throw new NumberFormatException("Something went wrong constructing the object (maybe overflow)");
            }
        };

        // This both makes and assigns the number in a wrapped way
        Function<String, Boolean> assignNewNumber = numberIn -> {
            try {
                this.prime[this.index++] = constructor.apply(numberIn);
                primeText.append(numberIn).append(",");
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                return quitAndSave.apply(Boolean.valueOf(true));
            }
            return this.index == prime.length ? quitAndSave.apply(Boolean.valueOf(true)) : false;
        };

        if (primeFile.exists()) {
            try {
                recycler = new Scanner(primeFile);
            } catch (FileNotFoundException fnfe) {
                // this should never happen but if it does we are boned
                fnfe.printStackTrace();
                throw new RuntimeException("Something went horribly wrong I think im getting trolled");
            }
            recycler.useDelimiter(",");
            while (recycler.hasNext())
                if (assignNewNumber.apply(recycler.next()))
                    return;
            System.out.println("I only found " + this.index + " primes in " + DIR + "\\PrimeLibrary.csv");
            BigInteger lastNumber = BigInteger.valueOf(Long.parseLong("" + prime[this.index - 1]));
            for (BigInteger i = lastNumber.mod(SIX).compareTo(FIVE) == 0 ? lastNumber.add(SIX)
                    : lastNumber.add(FOUR); this.index <= length; i = i.add(SIX)) {
                if (isPrime(i))
                    if (assignNewNumber.apply(i.toString()))
                        return;
                if (isPrime(i.add(TWO)))
                    if (assignNewNumber.apply(i.add(TWO).toString()))
                        return;
            }
        } else {
            if (length > 4) {
                if (assignNewNumber.apply("1") || assignNewNumber.apply("2") || assignNewNumber.apply("3")
                        || assignNewNumber.apply("5") || assignNewNumber.apply("7"))
                    return;
            } else
                switch (length) {
                    case 4:
                        prime[4] = constructor.apply("7");
                    case 3:
                        prime[3] = constructor.apply("5");
                    case 2:
                        prime[2] = constructor.apply("3");
                    case 1:
                        prime[1] = constructor.apply("2");
                        prime[0] = constructor.apply("1");
                        quitAndSave.apply(null);
                        return;
                    default:
                        throw new RuntimeException("How the hell did I miss that? bad lib size");
                }
            for (BigInteger i = ELEVEN; this.index <= length; i = i.add(SIX)) {
                if (isPrime(i))
                    if (assignNewNumber.apply(i.toString()))
                        return;
                if (isPrime(i.add(TWO)))
                    if (assignNewNumber.apply(i.add(TWO).toString()))
                        return;
            }
        }
        quitAndSave.apply(null);
    }

    // This will make a new instance. for example: 
    // PrimeLibrary<Integer> lib = PrimeLibrary.createLibrary("", 50, Integer.class);
    public static <t extends Number> PrimeLibrary<t> createLibrary(final String DIR, final int length,
            final Class<t> clazz) throws IllegalArgumentException, RuntimeException {
        return new PrimeLibrary<t>(DIR, length, clazz);
    }

    // This will make a new instance. for example: 
    //PrimeLibrary<Integer> lib = PrimeLibrary.createLibrary(50, Integer.class);
    public static <t extends Number> PrimeLibrary<t> createLibrary(final int length, final Class<t> clazz)
            throws IllegalArgumentException, RuntimeException {
        return new PrimeLibrary<t>(length, clazz);
    }

    // Recreates the list
    public List<T> remakeList() {
        this.primeList = Arrays.asList(this.prime);
        return this.primeList;
    }

    // Creates the list
    public PrimeLibrary<T> makeList() {
        if (this.primeList == null)
            this.primeList = Arrays.asList(this.prime);
        return this;
    }

    // Not recommended, but its here
    public T get(int index) throws IndexOutOfBoundsException {
        return this.prime[index];
    }

    // Identical to getList()
    public List<T> get() {
        return this.remakeList();
    }

    // Identical to get()
    public List<T> getList() {
        return this.remakeList();
    }

    /*
     * Nothing revolutionary. True = argument is prime.
     * 
     * Sum MUST NEVER BE COMPOSITE TO 2 or 3
     */
    private static boolean isPrime(final BigInteger sum) throws RuntimeException {
        // Lets not calculate the square root O(n^2(log(n))) if we can help it
        if (sum.mod(FIVE).compareTo(ZERO) == 0)
            return false;
        if (sum.mod(SEVEN).compareTo(ZERO) == 0)
            return false;
        final BigInteger goal;
        try {
            goal = bsqrt(sum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error calculating the square root! This should NEVER happen!");
        }
        if (goal.multiply(goal).compareTo(sum) == 0)// if perfect square, go home
            return false;
        for (BigInteger i = ELEVEN; goal.compareTo(i) >= 0 && i.compareTo(sum) != 0; i = i.add(SIX))
            if (sum.mod(i).compareTo(ZERO) == 0 || sum.mod(i.add(TWO)).compareTo(ZERO) == 0)
                return false;
        return true;
    }

    // BigInteger has a native sqrt function is VS code but in command line no so
    // heres the manual one that will ensure the limiting factor is your computer's
    // specs
    private static BigInteger bsqrt(final BigInteger x) throws IllegalArgumentException {
        if (x.compareTo(BigInteger.ZERO) < 0)
            throw new IllegalArgumentException("Negative argument.");
        if (x == BigInteger.ZERO || x == BigInteger.ONE)
            return x;
        BigInteger y;
        for (y = x.divide(TWO); y.compareTo(x.divide(y)) > 0; y = ((x.divide(y)).add(y)).divide(TWO))
            ;
        if (x.compareTo(y.multiply(y)) == 0)
            return y;
        else
            return y.add(BigInteger.ONE);
    }
}
