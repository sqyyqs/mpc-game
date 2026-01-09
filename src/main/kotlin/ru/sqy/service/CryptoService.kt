package main.kotlin.ru.sqy.service

import main.kotlin.ru.sqy.model.dto.CryptoState
import main.kotlin.ru.sqy.model.dto.result.CheckCounterComputationResult
import main.kotlin.ru.sqy.model.message.Counter
import main.kotlin.ru.sqy.model.message.EncryptedShare
import main.kotlin.ru.sqy.model.message.PublicKey
import main.kotlin.ru.sqy.service.mapper.CryptoMapper
import ru.sqy.crypto.jpaillier.KeyPairBuilder
import java.math.BigInteger

class CryptoService(
    val cryptoState: CryptoState,
    val cryptoMapper: CryptoMapper,
) {
    fun generatePublicKey(): PublicKey {
        cryptoState.keyPair = KeyPairBuilder().generateKeyPair()
        return cryptoMapper.publicKeyToMessage(cryptoState.publicKey)
    }

    fun encryptCounter(counter: Int): BigInteger {
        return cryptoState.publicKey.encrypt(BigInteger.valueOf(counter.toLong()))
    }

    fun checkCounterComputation(counterMessage: Counter): CheckCounterComputationResult {
        val (value, oldValue, shares, _) = counterMessage

        return if (value == oldValue + shares.sumOf { it }) {
            CheckCounterComputationResult.SUCCESS
        } else {
            CheckCounterComputationResult.FAILED
        }
    }

    fun encryptShare(generateShare: Int): EncryptedShare {
        val encryptedShare = cryptoState.publicKey.encrypt(BigInteger.valueOf(generateShare.toLong()))
        return cryptoMapper.encryptedShareToMessage(encryptedShare)
    }

    fun decryptShares(encryptedShares: List<EncryptedShare>): List<Int> {
        return encryptedShares.map {
            cryptoState.keyPair.decrypt(it.share).toInt()
        }
    }

    fun checkIsBelow() {
        TODO("Not yet implemented")
    }
}