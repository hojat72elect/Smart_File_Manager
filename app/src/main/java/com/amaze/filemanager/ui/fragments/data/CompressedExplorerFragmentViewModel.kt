package com.amaze.filemanager.ui.fragments.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amaze.filemanager.adapters.data.CompressedObjectParcelable

class CompressedExplorerFragmentViewModel : ViewModel() {
    val elements: MutableLiveData<ArrayList<CompressedObjectParcelable>> by lazy {
        MutableLiveData()
    }

    var folder: String? = null
}
