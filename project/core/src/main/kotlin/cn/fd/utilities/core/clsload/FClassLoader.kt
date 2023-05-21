package cn.fd.utilities.core.clsload

object FClassLoader : ClassLoader() {

    /**
     * º”‘ÿ¿‡
     */
//    public override fun loadClass(className: String, isInitialized: Boolean): Class<*> {
//        return Class.forName(className, isInitialized, Thread.currentThread().contextClassLoader)
//        //return super.loadClass(className, isInitialized)
//    }

    override fun loadClass(className: String): Class<*> {
        return loadClass(className, false)
    }


}