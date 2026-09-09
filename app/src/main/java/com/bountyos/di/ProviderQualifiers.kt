package com.bountyos.di

import javax.inject.Qualifier

/** 标记 HackerOne 平台的绑定。 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HackerOne

/** 标记 Bugcrowd 平台的绑定。 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Bugcrowd
