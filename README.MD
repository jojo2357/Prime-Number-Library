Not much to see here, but here are some things I think you may find useful!

Uses java 8

Parameters are as follows: (String directoryOfLibraryFile, int librarySize, Class classOfTargetType)

Note that any of these ways are valid, but it is probably cleaner to do the static `PrimeLibrary.createLibrary` way

# Usage for Integers! 
```java
PrimeLibrary<Integer> lib = PrimeLibrary.createLibrary("", 50, Integer.class);
int the3rdPrime = lib.prime[3];
```

```java
PrimeLibrary<Integer> IntegerPrimeLibrary = new PrimeLibrary<Integer>("", 50, Integer.class);
int the50thPrimeNumber = IntegerPrimeLibrary.prime[50];
```

# Usage for BigIntegers
```java
PrimeLibrary<BigInteger> lib = PrimeLibrary.createLibrary("", 50, BigInteger.class);
BigInteger the3rdPrime = lib.prime[3];
```

```java
PrimeLibrary<BigInteger> BigIntegerPrimeLibrary = new PrimeLibrary<BigInteger>("", 50000, BigInteger.class);
BigInteger the50000thPrimeNumberSquared = BigIntegerPrimeLibrary.prime[50000].pow(2);
```

# List Support
```java
PrimeLibrary<String> whyAmIUsingStrings = PrimeLibrary.createLibrary(50, String.class).;
List<String> butWhy = whyAmIUsingStrings.get();
```