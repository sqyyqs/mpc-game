package main.kotlin.ru.sqy.crypto.zkprange

import com.weavechain.zk.bulletproofs.BulletProofGenerators
import com.weavechain.zk.bulletproofs.BulletProofs
import com.weavechain.zk.bulletproofs.PedersenCommitment
import com.weavechain.zk.bulletproofs.Proof
import com.weavechain.zk.bulletproofs.Utils
import com.weavechain.zk.bulletproofs.gadgets.Gadgets
import com.weavechain.zk.bulletproofs.gadgets.NumberInRange
import com.weavechain.zk.bulletproofs.gadgets.NumberInRangeParams
import main.kotlin.ru.sqy.model.dto.GameParameters

class ZkpRange(
    gameParameters: GameParameters
) {
    private val pedersenCommitment = PedersenCommitment.getDefault()
    private val bulletProofs = BulletProofs().apply { registerGadget(NumberInRange()) }
    private val random = Utils.randomScalar()
    private val numberInRangeParams = NumberInRangeParams(0, gameParameters.m.toLong(), 31)
    private val generator = BulletProofGenerators(128, 1)

    fun generateProof(
        counter: Int,
    ): Proof {
        val generator = BulletProofGenerators(128, 1)
        return bulletProofs.generate(
            Gadgets.number_in_range,
            counter,
            numberInRangeParams,
            random,
            pedersenCommitment,
            generator
        )
    }

    fun verify(proof: Proof): Boolean {
        return bulletProofs.verify(
            Gadgets.number_in_range,
            numberInRangeParams,
            proof,
            pedersenCommitment,
            generator
        )
    }
}