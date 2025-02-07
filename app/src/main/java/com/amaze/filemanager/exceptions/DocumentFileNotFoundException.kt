package com.amaze.filemanager.exceptions

import android.net.Uri

class DocumentFileNotFoundException(rootUri: Uri, path: String) :
    RuntimeException("Root uri: %s and path %s".format(rootUri.path, path))
