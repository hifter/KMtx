package io.github.hifter.kmtx.module

import io.github.aakira.napier.Napier

class InitModule{
    companion object{
        fun init(){
            initNapier()
            Napier.d(tag = "InitModule", message = "init success")
        }
    }
}

expect fun initNapier()