package com.fortysevendeg.ninecardslauncher.process.cloud.models

import java.util.Date

import com.fortysevendeg.ninecardslauncher.process.commons.types.{NineCardsMoment, NineCardCategory, CollectionType}

trait CloudStorageResource {
  def cloudId: String
  def deviceId: Option[String]
  def deviceName: String
  def createdDate: Date
  def modifiedDate: Date
}

case class CloudStorageDeviceSummary(
  cloudId: String,
  deviceId: Option[String],
  deviceName: String,
  createdDate: Date,
  modifiedDate: Date,
  currentDevice: Boolean) extends CloudStorageResource

case class CloudStorageDevice(
  cloudId: String,
  createdDate: Date,
  modifiedDate: Date,
  data: CloudStorageDeviceData) extends CloudStorageResource {

  override def deviceId: Option[String] = Some(data.deviceId)

  override def deviceName: String = data.deviceName
}

case class CloudStorageDeviceData(
  deviceId: String,
  deviceName: String,
  documentVersion: Int,
  collections: Seq[CloudStorageCollection],
  moments: Option[Seq[CloudStorageMoment]])

case class CloudStorageCollection(
  name: String,
  originalSharedCollectionId: Option[String],
  sharedCollectionId: Option[String],
  sharedCollectionSubscribed: Option[Boolean],
  items: Seq[CloudStorageCollectionItem],
  collectionType: CollectionType,
  icon: String,
  category: Option[NineCardCategory],
  moment: Option[CloudStorageMoment])

case class CloudStorageCollectionItem(
  itemType: String,
  title: String,
  intent: String)

case class CloudStorageMoment(
  timeslot: Seq[CloudStorageMomentTimeSlot],
  wifi: Seq[String],
  headphones: Boolean,
  momentType: Option[NineCardsMoment])

case class CloudStorageMomentTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])