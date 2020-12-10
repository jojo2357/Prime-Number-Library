Not much to see here, but here are some things I think you may find useful!

Uses java 8

Parameters are as follows: (String directoryOfLibraryFile, int librarySize, Class classOfTargetType)

# Usage for Integers! 

```java
PrimeLibrary<Integer> IntegerPrimeLibrary = new PrimeLibrary<Integer>("", 50, Integer.class);
int the50thPrimeNumber = IntegerPrimeLibrary.prime[50];
```

```java
PrimeLibrary<BigInteger> BigIntegerPrimeLibrary = new PrimeLibrary<BigInteger>("", 50000, BigInteger.class);
BigInteger the50000thPrimeNumberSquared = BigIntegerPrimeLibrary.prime[50000].pow(2);
```

Note: 
Compiler will get angery with you if you want array of primes as Strings. Look in the source code for how to make it accept String as a parameter and as a class! Be careful, though, as there is a bit of black magic going on with unchecked casts and the like.