package com.amaze.filemanager.utils

import android.app.Service
import android.os.Binder

class ObtainableServiceBinder<T : Service?>(@JvmField val service: T) : Binder()
