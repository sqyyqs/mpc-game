package main.kotlin.ru.sqy.service.mapper

import com.weavechain.zk.bulletproofs.Proof
import main.kotlin.ru.sqy.model.message.Counter
import main.kotlin.ru.sqy.model.message.EncryptedShare
import main.kotlin.ru.sqy.model.message.PublicKey
import main.kotlin.ru.sqy.model.message.RangeProof
import ru.sqy.crypto.jpaillier.PaillierPublicKey
import java.math.BigInteger

class CryptoMapper(
    private val from: String,
) {
    fun publicKeyToMessage(paillierPublicKey: PaillierPublicKey): PublicKey {
        return PublicKey(
            n = paillierPublicKey.n,
            g = paillierPublicKey.g,
            from = from
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