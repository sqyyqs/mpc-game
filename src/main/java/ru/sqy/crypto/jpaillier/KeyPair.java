package ru.sqy.crypto.jpaillier;

import java.math.BigInteger;

public class KeyPair {
    private final PaillierPrivateKey paillierPrivateKey;
    private final PaillierPublicKey paillierPublicKey;
    private final BigInteger upperBound;

    public KeyPair(PaillierPrivateKey paillierPrivateKey, PaillierPublicKey paillierPublicKey, BigInteger upperBound) {
        this.paillierPrivateKey = paillierPrivateKey;
        this.paillierPublicKey = paillierPublicKey;
        this.upperBound = upperBound;
    }

    public PaillierPrivateKey getPrivateKey() {
        return paillierPrivateKey;
    }

    public PaillierPublicKey getPublicKey() {
        return paillierPublicKey;
    }

    public final BigInteger decrypt(BigInteger c) {
        BigInteger n = paillierPublicKey.getN();
        BigInteger nSquare = paillierPublicKey.getnSquared();
        BigInteger lambda = paillierPrivateKey.getLambda();

        BigInteger u = paillierPrivateKey.getPreCalculatedDenominator();

        BigInteger p = c.modPow(lambda, nSquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);

        if (upperBound != null && p.compareTo(upperBound) > 0) {
            p = p.subtract(n);
        }

        return p;
    }
}
