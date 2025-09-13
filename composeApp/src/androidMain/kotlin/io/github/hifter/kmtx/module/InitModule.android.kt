package io.github.hifter.kmtx.module

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

actual fun initNapier(){
    Napier.base(DebugAntilog())
//    if (BuildConfig.DEBUG) {
//            Napier.base(DebugAntilog())
//    } else {
//        // 生产环境可以禁用日志或只记录错误
//            Napier.base(ReleaseAntilog())
//    }
}