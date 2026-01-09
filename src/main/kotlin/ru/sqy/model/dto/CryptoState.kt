package main.kotlin.ru.sqy.model.dto

import ru.sqy.crypto.jpaillier.KeyPair
import ru.sqy.crypto.jpaillier.PaillierPublicKey

class CryptoState {
    lateinit var keyPair: KeyPair

    val publicKey: PaillierPublicKey
        get() = keyPair.publicKey
}