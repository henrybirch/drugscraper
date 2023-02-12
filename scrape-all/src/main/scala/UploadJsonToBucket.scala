import com.google.cloud.storage.{
  Blob,
  BlobId,
  BlobInfo,
  Storage,
  StorageOptions,
  Option => _
}
import io.circe.Json

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

object UploadJsonToBucket {
  private val outputBucketName: String = sys.env("outputBucketName")
  private val storage: Storage = StorageOptions
    .newBuilder()
    .setProjectId("trans-sunset-370818-4cba431ef717")
    .build()
    .getService

  /** Uploads the json to the bucket (named in sys env) after formatting the json to be one object per line (suitable
    * for bigquery)
    *
    * @param fileName fileName for the json to arrive in bucket
    * @param json     WebsiteRecord as json
    * @return
    */
  def uploadJsonToBucket(fileName: String, json: Json): Option[Blob] = {
    val formattedJsonForBigQuery: Option[String] = {
      val jsonArray: Option[Vector[Json]] = json.asArray
      jsonArray.map(_.map(_.noSpaces))
    }.map(_.mkString("\n"))
    val blobId: BlobId = BlobId.of(outputBucketName, fileName)
    val blobInfo: BlobInfo = BlobInfo.newBuilder(blobId).build()
    val content: Option[Array[Byte]] =
      formattedJsonForBigQuery.map(_.getBytes(StandardCharsets.UTF_8))

    content.map((bytes: Array[Byte]) =>
      storage.createFrom(blobInfo, new ByteArrayInputStream(bytes))
    )
  }

}
