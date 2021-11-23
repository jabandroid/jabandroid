package com.global.vtg.appview.config

interface F2<in A, in B, in C> {
    operator fun invoke(object1: A, object2: B, object3: C)
}
