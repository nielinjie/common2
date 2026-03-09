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

fun ByteArray.md5(): String {
    return java.security.MessageDigest.getInstance("MD5")
        .digest(this).toHexString()
}

fun String.sha256(): String {
    return java.security.MessageDigest.getInstance("SHA-256")
        .digest(this.toByteArray()).toHexString()
}

fun ByteArray.sha256(): String {
    return java.security.MessageDigest.getInstance("SHA-256")
        .digest(this).toHexString()
}

/*

 */
fun parasSignature(paras: List<Pair<String, String>>, urlSafe: Boolean = false): String {
    val parasString = paras.joinToString("&") {
        "${it.first}=${it.second}"
    }
    return (parasString.sha256().toByteArray(charset("UTF-8"))).base64(urlSafe)
}