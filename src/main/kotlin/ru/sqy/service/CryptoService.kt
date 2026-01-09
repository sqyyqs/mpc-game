package main.kotlin.ru.sqy.service

import com.weavechain.zk.bulletproofs.Proof
import main.kotlin.ru.sqy.crypto.zkprange.ZkpRange
import main.kotlin.ru.sqy.model.dto.CryptoState
import main.kotlin.ru.sqy.model.dto.result.CheckCounterComputationResult
import main.kotlin.ru.sqy.model.message.Counter
import main.kotlin.ru.sqy.model.message.EncryptedShare
import main.kotlin.ru.sqy.model.message.PublicKey
import main.kotlin.ru.sqy.model.message.RangeProof
import main.kotlin.ru.sqy.service.mapper.CryptoMapper
import ru.sqy.crypto.jpaillier.KeyPairBuilder
import java.math.BigInteger

class CryptoService(
    private val cryptoState: CryptoState,
    private val cryptoMapper: CryptoMapper,
    private val zkpRange: ZkpRange,
) {
    fun generatePublicKey(): PublicKey {
        val generateKeyPair = KeyPairBuilder().generateKeyPair()
        cryptoState.paillierPublicKey = generateKeyPair.publicKey
        cryptoState.paillierPrivateKey = generateKeyPair.privateKey

        return cryptoMapper.publicKeyToMessage(cryptoState.paillierPublicKey)
    }

    fun encryptCounter(counter: Int): BigInteger {
        return cryptoState.paillierPublicKey.encrypt(BigInteger.valueOf(counter.toLong()))
    }

    fun checkCounterComputation(counterMessage: Counter): CheckCounterComputationResult {
        val (value, oldValue, shares, _) = counterMessage

        return if (value == oldValue + shares.sumOf { it }) {
            CheckCounterComputationResult.SUCCESS
        } else {
            CheckCounterComputationResult.FAILED
        }
    }

    fun encryptShare(share: Int, publicKey: PublicKey): EncryptedShare {
        cryptoState.paillierPublicKey = cryptoMapper.publicKeyFromMessage(publicKey)
        val encryptedShare = cryptoState.paillierPublicKey.encrypt(BigInteger.valueOf(share.toLong()))
        return cryptoMapper.encryptedShareToMessage(encryptedShare)
    }

    fun decryptShares(encryptedShares: List<EncryptedShare>): List<Int> {
        return encryptedShares.map {
            cryptoState.keyPair.decrypt(it.share).toInt()
        }
    }

    fun generateProof(counter: Int): RangeProof {
        return cryptoMapper.rangeProofToMessage(zkpRange.generateProof(counter))
    }

    fun verifyProof(proof: RangeProof): Boolean {
        return zkpRange.verify(Proof.deserialize(proof.data))
    }
}