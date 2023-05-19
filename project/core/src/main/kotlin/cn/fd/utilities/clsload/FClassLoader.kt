package cn.fd.utilities.clsload

object FClassLoader : ClassLoader() {


    /**
     * º”‘ÿ¿‡
     */
    @Throws(ClassNotFoundException::class)
    public override fun loadClass(className: String, isInitialized: Boolean): Class<*> {
        return Class.forName(className, isInitialized, Thread.currentThread().contextClassLoader)
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String): Class<*> {
        return loadClass(className, false)
    }


}