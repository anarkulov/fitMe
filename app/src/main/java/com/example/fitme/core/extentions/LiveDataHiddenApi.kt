package androidx.lifecycle //DO NOT UPDATE THIS PACKAGE
// We need to access LiveData's pcakage visibile methond getVesrion() for comparasion

fun LiveData<*>.dataVersion(): Int {
    return this.version
}

