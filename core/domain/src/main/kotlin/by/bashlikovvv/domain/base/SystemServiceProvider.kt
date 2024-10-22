package by.bashlikovvv.domain.base

interface SystemServiceProvider {
    fun getSystemService(name: String): Any?
}