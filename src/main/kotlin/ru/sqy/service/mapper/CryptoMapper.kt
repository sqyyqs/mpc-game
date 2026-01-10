package main.kotlin.ru.sqy.service.mapper

import com.weavechain.zk.bulletproofs.Proof
import ru.sqy.model.message.Counter
import ru.sqy.model.message.EncryptedShare
import ru.sqy.model.message.PublicKey
import ru.sqy.model.message.RangeProof
import ru.sqy.crypto.jpaillier.PaillierPublicKey
import java.math.BigInteger

class CryptoMapper(
    private val from: String,
) {
    fun publicKeyToMessage(paillierPublicKey: PaillierPublicKey): PublicKey {
        return PublicKey(
            n = paillierPublicKey.n,
            g = paillierPublicKey.g,
            bits = paillierPublicKey.bits,
            from = from
        )
    }

    fun publicKeyFromMessage(publicKey: PublicKey): PaillierPublicKey {
        return PaillierPublicKey(
            publicKey.n,
            publicKey.n.multiply(publicKey.n),
            publicKey.g,
            publicKey.bits
        )
    }

    fun encryptedCounterToMessage(
        encryptedCounter: BigInteger,
        encryptedOldCounter: BigInteger,
        shares: List<EncryptedShare>
    ): Counter {
        return Counter(
            value = encryptedCounter,
            oldValue = encryptedOldCounter,
            shares = shares.map { it.share },
            from = from
        )
    }

    fun encryptedShareToMessage(encryptedShare: BigInteger): EncryptedShare {
        return EncryptedShare(
            encryptedShare,
            from
        )
    }

    fun rangeProofToMessage(proof: Proof): RangeProof {
        return RangeProof(
            proof.serialize(),
            from
        )
    }
}