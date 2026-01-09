package main.kotlin.ru.sqy.model.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "typeName"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Counter::class, name = "Counter"),
    JsonSubTypes.Type(value = EncryptedShare::class, name = "EncryptedShare"),
    JsonSubTypes.Type(value = OutOfGame::class, name = "OutOfGame"),
    JsonSubTypes.Type(value = Players::class, name = "Players"),
    JsonSubTypes.Type(value = PublicKey::class, name = "PublicKey"),
    JsonSubTypes.Type(value = RangeProof::class, name = "RangeProof"),
)
interface Message {
    fun from(): String
    fun typeName(): String
}