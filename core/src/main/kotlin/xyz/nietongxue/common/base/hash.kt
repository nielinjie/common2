package xyz.nietongxue.common.base

import com.appmattus.crypto.Algorithm


data class Hash(val hex: String)

interface WithHash {
    fun getHash(): Hash
}


fun hashString(s: String): Hash {
    return hashBytes(s.encodeToByteArray())
}

fun hashBytes(bytes: ByteArray): Hash {
    val digest = Algorithm.MD5.createDigest()
    return digest.digest(bytes).toHexString().let {
        Hash(it)
    }
}