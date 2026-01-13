package ru.sqy.crypto.jpaillier;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class KeyPairBuilder {
    private int bits = 1024;
    private int certainty = 0;
    private Random rng;
    private BigInteger upperBound;

    public KeyPairBuilder bits(int bits) {
        this.bits = bits;
        return this;
    }

    public KeyPairBuilder certainty(int certainty) {
        this.certainty = certainty;
        return this;
    }

    public KeyPairBuilder randomNumberGenerator(Random rng) {
        this.rng = rng;
        return this;
    }

    public KeyPairBuilder upperBound(BigInteger b) {
        this.upperBound = b;
        return this;
    }

    public KeyPair generateKeyPair() {
        if (rng == null) {
            rng = new SecureRandom();
        }

        BigInteger p, q;
        int length = bits / 2;
        if (certainty > 0) {
            p = new BigInteger(length, certainty, rng);
            q = new BigInteger(length, certainty, rng);
        } else {
            p = BigInteger.probablePrime(length, rng);
            q = BigInteger.probablePrime(length, rng);
        }

        BigInteger n = p.multiply(q);
        BigInteger nSquared = n.multiply(n);

        BigInteger pMinusOne = p.subtract(BigInteger.ONE);
        BigInteger qMinusOne = q.subtract(BigInteger.ONE);

        BigInteger lambda = this.lcm(pMinusOne, qMinusOne);

        BigInteger g;
        BigInteger helper;

        do {
            g = new BigInteger(bits, rng);
            helper = calculateL(g.modPow(lambda, nSquared), n);

        } while (!helper.gcd(n).equals(BigInteger.ONE));

        PaillierPublicKey paillierPublicKey = new PaillierPublicKey(n, nSquared, g, bits);
        PaillierPrivateKey paillierPrivateKey = new PaillierPrivateKey(lambda, helper.modInverse(n));

        return new KeyPair(paillierPrivateKey, paillierPublicKey, upperBound);

    }

    private BigInteger calculateL(BigInteger u, BigInteger n) {
        BigInteger result = u.subtract(BigInteger.ONE);
        result = result.divide(n);
        return result;
    }

    private BigInteger lcm(BigInteger a, BigInteger b) {
        BigInteger result;
        BigInteger gcd = a.gcd(b);

        result = a.abs().divide(gcd);
        result = result.multiply(b.abs());

        return result;
    }
}
