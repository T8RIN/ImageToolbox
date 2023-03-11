package ru.tech.imageresizershrinker.workers

//import android.os.Parcel
//import android.os.Parcelable
//import androidx.work.Data
//import java.io.*
//
//object WorkerUtils {
//
//    fun Data.Builder.putParcelable(key: String, parcelable: Parcelable): Data.Builder {
//        val parcel = Parcel.obtain()
//        try {
//            parcelable.writeToParcel(parcel, 0)
//            putByteArray(key, parcel.marshall())
//        } finally {
//            parcel.recycle()
//        }
//        return this
//    }
//
//    fun Data.Builder.putParcelableList(key: String, list: List<Parcelable>): Data.Builder {
//        list.forEachIndexed { i, item ->
//            putParcelable("$key$i", item)
//        }
//        return this
//    }
//
//    fun Data.Builder.putSerializable(
//        key: String,
//        serializable: Serializable
//    ): Data.Builder {
//        ByteArrayOutputStream().use { bos ->
//            ObjectOutputStream(bos).use { out ->
//                out.writeObject(serializable)
//                out.flush()
//            }
//            putByteArray(key, bos.toByteArray())
//        }
//        return this
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    inline fun <reified T : Parcelable> Data.getParcelable(key: String): T? {
//        val parcel = Parcel.obtain()
//        try {
//            val bytes = getByteArray(key) ?: return null
//            parcel.unmarshall(bytes, 0, bytes.size)
//            parcel.setDataPosition(0)
//            val creator = T::class.java.getField("CREATOR").get(null) as Parcelable.Creator<T>
//            return creator.createFromParcel(parcel)
//        } finally {
//            parcel.recycle()
//        }
//    }
//
//    inline fun <reified T : Parcelable> Data.getParcelableList(key: String): MutableList<T> {
//        val list = mutableListOf<T>()
//        with(keyValueMap) {
//            while (containsKey("$key${list.size}")) {
//                list.add(getParcelable<T>("$key${list.size}") ?: break)
//            }
//        }
//        return list
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    fun <T : Serializable> Data.getSerializable(key: String): T? {
//        val bytes = getByteArray(key) ?: return null
//        ByteArrayInputStream(bytes).use { bis ->
//            ObjectInputStream(bis).use { ois ->
//                return ois.readObject() as T
//            }
//        }
//    }
//
//}