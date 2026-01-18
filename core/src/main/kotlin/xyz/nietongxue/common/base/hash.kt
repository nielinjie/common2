package xyz.nietongxue.common.base

import com.appmattus.crypto.Algorithm


data class Hash(val hex: String)

interface WithHash {
    fun getHash(): Hash
}


fun hashString(s: String): Hash {
    return hashBytes(s.encodeToByteArray())
}

fun hashBytes(bytes: ByteArray, algorithm: String = "MD5"): Hash {
    return java.security.MessageDigest.getInstance(algorithm)
        .digest(bytes).toHexString().let {
        Hash(it)
    }
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}

fun String.md5(): String {
    return java.security.MessageDigest.getInstance("MD5")
    .digest(this.toByteArray()).toHexString()
}

fun ByteArray.md5():String{
    return java.security.MessageDigest.getInstance("MD5")
    .digest(this).toHexString()
}