package cn.fd.utilities.util

import java.io.*


class FClassLoader : ClassLoader() {

    @Throws(ClassNotFoundException::class)
    override fun findClass(name: String): Class<*> {
        try {
            val classDate = getDate(name)
            if (classDate == null) {
            } else {
                // defineClass方法将字节码转化为类
                return defineClass(null, classDate, 0, classDate.size)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return super.findClass(name)
    }

    // 返回类的字节码
    @Throws(IOException::class)
    private fun getDate(className: String): ByteArray? {
        var `in`: InputStream? = null
        var out: ByteArrayOutputStream? = null
        try {
            `in` = FileInputStream(className)
            out = ByteArrayOutputStream()
            val buffer = ByteArray(2048)
            var len = 0
            while (`in`.read(buffer).also { len = it } != -1) {
                out.write(buffer, 0, len)
            }
            return out.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            `in`!!.close()
            out!!.close()
        }
        return null
    }
}