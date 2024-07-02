import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faces_table")
data class Face(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "faceID")
    var id: Long = 0L,
    var smilingProbability: Float,
    var leftEyeOpenProbability: Float,
    var rightEyeOpenProbability: Float,
    var eulerX: Float,
    var eulerY: Float,
    var eulerZ: Float,
    var leftEyeX: Float,
    var leftEyeY: Float,
    var rightEyeX: Float,
    var rightEyeY: Float,
    var mouthLeftX: Float,
    var mouthLeftY: Float,
    var mouthRightX: Float,
    var mouthRightY: Float,
    var mouthBottomX: Float,
    var mouthBottomY: Float
){
    override fun toString(): String {
        return "Face(id=$id, smilingProbability=$smilingProbability, leftEyeOpenProbability=$leftEyeOpenProbability, rightEyeOpenProbability=$rightEyeOpenProbability, eulerX=$eulerX, eulerY=$eulerY, eulerZ=$eulerZ, leftEyeX=$leftEyeX, leftEyeY=$leftEyeY, rightEyeX=$rightEyeX, rightEyeY=$rightEyeY, mouthLeftX=$mouthLeftX, mouthLeftY=$mouthLeftY, mouthRightX=$mouthRightX, mouthRightY=$mouthRightY, mouthBottomX=$mouthBottomX, mouthBottomY=$mouthBottomY)"
    }
}