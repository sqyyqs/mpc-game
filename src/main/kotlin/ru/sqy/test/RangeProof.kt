package ru.sqy.test

import com.weavechain.ec.Scalar
import com.weavechain.zk.bulletproofs.BulletProofGenerators
import com.weavechain.zk.bulletproofs.BulletProofs
import com.weavechain.zk.bulletproofs.PedersenCommitment
import com.weavechain.zk.bulletproofs.Proof
import com.weavechain.zk.bulletproofs.Utils
import com.weavechain.zk.bulletproofs.gadgets.Gadgets
import com.weavechain.zk.bulletproofs.gadgets.NumberInRange
import com.weavechain.zk.bulletproofs.gadgets.NumberInRangeParams
import main.kotlin.ru.sqy.crypto.zkprange.ZkpRange
import main.kotlin.ru.sqy.model.dto.GameParameters


fun main() {

    val generateProof = ZkpRange(GameParameters(10, 20)).generateProof(30)
    val verify = ZkpRange(GameParameters(10, 20)).verify(generateProof)
    println(verify)

}