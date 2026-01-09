package ru.sqy.crypto.jpaillier;

import java.math.BigInteger;

/**
 * A class that holds a pair of associated public and private keys.
 */
public class KeyPair {
    private final PaillierPrivateKey paillierPrivateKey;
    private final PaillierPublicKey paillierPublicKey;
    private final BigInteger upperBound;

    KeyPair(PaillierPrivateKey paillierPrivateKey, PaillierPublicKey paillierPublicKey, BigInteger upperBound) {
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

    /**
     * Decrypts the given ciphertext.
     *
     * @param c The ciphertext that should be decrypted.
     * @return The corresponding plaintext. If an upper bound was given to {@link KeyPairBuilder},
     * the result can also be negative. See {@link KeyPairBuilder#upperBound(BigInteger)} for details.
     */
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
