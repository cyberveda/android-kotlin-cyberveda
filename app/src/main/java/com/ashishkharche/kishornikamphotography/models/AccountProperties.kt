package com.ashishkharche.kishornikamphotography.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Local data class for modeling: https://kishornikamphotography.com/api/account/properties
 * NOTES:
 * 1) local 'auth_token' table has foreign key relationship to 'account_properties' table
 * 2) pk of 'account_properties' matches the pk on server (open-api.xyz)
 *
 * Docs: https://kishornikamphotography.com/api/
 */
@Entity(tableName = "account_properties")
data class AccountProperties(

    @SerializedName("pk")
    @Expose
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "pk") var pk: Int,

    @SerializedName("email")
    @Expose
    @ColumnInfo(name = "email") var email: String,

    @SerializedName("username")
    @Expose
    @ColumnInfo(name = "username") var username: String
)
{

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false

        other as AccountProperties

        if (pk != other.pk) return false
        if (email != other.email) return false
        if (username != other.username) return false

        return true
    }

}



























