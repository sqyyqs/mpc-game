package ru.sqy.model.dto

import ru.sqy.crypto.jpaillier.KeyPair
import ru.sqy.crypto.jpaillier.PaillierPrivateKey
import ru.sqy.crypto.jpaillier.PaillierPublicKey

class CryptoState {
    val keyPair: KeyPair
        get() = KeyPair(paillierPrivateKey, paillierPublicKey, null)

    lateinit var paillierPrivateKey: PaillierPrivateKey
    lateinit var paillierPublicKey: PaillierPublicKey
}