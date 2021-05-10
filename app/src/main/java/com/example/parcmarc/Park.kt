package com.example.parcmarc

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "park")
class Park(
    @ColumnInfo var name: String,
    @ColumnInfo var location: LatLng,
    @ColumnInfo var endDate: Date?
) : Parcelable {

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    override fun toString() = name
}