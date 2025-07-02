package se.umu.cs.ens21jfg.thirty

import android.os.Parcel
import android.os.Parcelable
import kotlin.random.Random

/**
 * @author Julia Forsberg, ens21jfg
 * @version 1.0
 *
 * Dice class represents a single dice with a value and a selected state.
 */
class Dice() : Parcelable {
    var value = 0
    var isSelected = false

    /**
     * Constructor for creating a dice from a parcel.
     * @param parcel The parcel from which to read the dice's state.
     */
    constructor(parcel: Parcel) : this() {
        value = parcel.readInt()
        isSelected = parcel.readByte().toInt() != 0
    }

    /**
     * Resets the value of the dice to 0.
     */
    fun reset() {
        value = 0
    }

    /**
     * Rolls the dice by generating a random number between 1 and 6.
     */
    fun roll() {
        value = Random.nextInt(1, 7)
    }

    /**
     * Toggles the selected state of the dice.
     */
    fun toggleSelected() {
        isSelected = !isSelected
    }

    /**
     * Describes the contents of the Parcelable object.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Writes the contents of the Parcelable object to a Parcel.
     * @param dest The Parcel in which to write the object's data.
     * @param flags Additional flags about how the object should be written.
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(value)
        dest.writeByte(if (isSelected) 1 else 0)
    }

    /**
     * Companion object for implementing the Parcelable interface.
     */
    companion object CREATOR : Parcelable.Creator<Dice> {
        override fun createFromParcel(parcel: Parcel): Dice {
            return Dice(parcel)
        }

        override fun newArray(size: Int): Array<Dice?> {
            return arrayOfNulls(size)
        }
    }
}