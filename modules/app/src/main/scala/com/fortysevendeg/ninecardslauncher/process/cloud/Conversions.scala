package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageResource
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile

trait Conversions {

  def toDriveDevice(driveServiceFile: DriveServiceFile): CloudStorageResource =
    CloudStorageResource(
      resourceId = driveServiceFile.driveId,
      title = driveServiceFile.title,
      createdDate = driveServiceFile.createdDate,
      modifiedDate = driveServiceFile.modifiedDate)

}