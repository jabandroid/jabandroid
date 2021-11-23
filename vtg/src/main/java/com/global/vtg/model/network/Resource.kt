package com.global.vtg.model.network

import com.global.vtg.model.network.result.BaseError


sealed class Resource<out T> {
    class Success<out T>(val data: T) : Resource<T>()
    class Error<out T>(val error: BaseError) : Resource<T>()
    class Loading<out T>(val loading : EnumLoading) : Resource<T>()
}