package se.umu.cs.ens21jfg.thirty

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Julia Forsberg
 * @version 1.0
 *
 * Result contains a result for one round of the game.
 */
data class Result(val gameMode: String?, val score: Int): Parcelable {
    /**
     * Constructor for parcelable.
     * @param parcel parcel
     */
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt()
    )

    /**
     * Describes the contents of the parcel.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Writes the parcel.
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(gameMode)
        dest.writeInt(score)
    }

    /**
     * Companion object for parcelable
     */
    companion object CREATOR : Parcelable.Creator<Result> {
        override fun createFromParcel(parcel: Parcel): Result {
            return Result(parcel)
        }

        override fun newArray(size: Int): Array<Result?> {
            return arrayOfNulls(size)
        }
    }
}