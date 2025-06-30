package se.umu.cs.ens21jfg.thirty

import android.os.Parcel
import android.os.Parcelable
import kotlin.random.Random

class Dice() : Parcelable {
    var value = 0
        private set

    var isSelected = false

    constructor(parcel: Parcel) : this() {
        value = parcel.readInt()
        isSelected = parcel.readByte().toInt() != 0
    }

    fun reset() {
        value = 0
    }

    fun roll() {
        value = Random.nextInt(1, 7)
    }

    fun toggleSelected() {
        isSelected = !isSelected
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(value)
        dest.writeByte(if (isSelected) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Dice> {
        override fun createFromParcel(parcel: Parcel): Dice {
            return Dice(parcel)
        }

        override fun newArray(size: Int): Array<Dice?> {
            return arrayOfNulls(size)
        }
    }
}