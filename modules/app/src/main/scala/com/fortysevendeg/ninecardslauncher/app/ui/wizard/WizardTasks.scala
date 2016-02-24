package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{OperationCanceledException, Account, AccountManager}
import android.os.Build
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserPermissions, UserCloudDevices}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.{ImplicitsCloudStorageProcessExceptions, CloudStorageProcess, CloudStorageProcessException}
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageCollectionItem, CloudStorageCollection, CloudStorageDevice, CloudStorageDeviceSummary}
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardIntentImplicits, Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollectionItem, UserCollection, UserDevice}
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import play.api.libs.json.Json
import rapture.core._
import NineCardIntentImplicits._

import scala.reflect.ClassTag
import scalaz.{-\/, \/-, \/}
import scalaz.concurrent.Task

trait WizardTasks
  extends ImplicitsCloudStorageProcessExceptions {

  self: WizardActivity =>

  def requestUserPermissions(
    accountManager: AccountManager,
    account: Account,
    client: GoogleApiClient): ServiceDef2[UserPermissions, AuthTokenException with AuthTokenOperationCancelledException] = {
    val oauthScopes = "androidmarket" // TODO - This should be removed when we switch off the server v1
    val driveScope = resGetString(R.string.oauth_scopes)
    for {
      token <- getAuthToken(accountManager, account, oauthScopes)
      _ = setToken(token)
      token2 <- getAuthToken(accountManager, account, driveScope)
    } yield UserPermissions(token, Seq(oauthScopes))

  }

  def loadUserDevices(
    client: GoogleApiClient,
    androidId: String,
    username: String,
    userPermissions: UserPermissions): ServiceDef2[UserCloudDevices, UserException with UserConfigException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client, username)
    val device = Device(
        name = Build.MODEL,
        deviceId = androidId,
        secretToken = userPermissions.token,
        permissions = userPermissions.oauthScopes)
    for {
      response <- di.userProcess.signIn(username, device)
      cloudStorageResources <- cloudStorageProcess.getCloudStorageDevices
      userCloudDevices <- verifyAndUpdate(cloudStorageProcess, username, cloudStorageResources)
    } yield userCloudDevices

  }

  def storeActualDevice(
    client: GoogleApiClient,
    androidId: String,
    username: String): ServiceDef2[Unit, CollectionException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client, username)
    for {
      collections <- di.collectionProcess.getCollections
      device = toCloudStorageDevice(
        deviceId = androidId,
        deviceName = Build.MODEL,
        collections = collections)
      _ <- cloudStorageProcess.createOrUpdateCloudStorageDevice(device)
    } yield ()
  }

  private[this] def verifyAndUpdate(
    cloudStorageProcess: CloudStorageProcess,
    name: String,
    cloudStorageResources: Seq[CloudStorageDeviceSummary]): ServiceDef2[UserCloudDevices, UserConfigException with CloudStorageProcessException] = {
    if (cloudStorageResources.isEmpty) {
      for {
        userInfo <- di.userConfigProcess.getUserInfo
        cloudStorageDevices = userInfo.devices map toCloudStorageDevice
        _ <- storeOnCloud(cloudStorageProcess, cloudStorageDevices)
      } yield UserCloudDevices(userInfo.name, cloudStorageDevices)
    } else {
      for {
        devices <- loadFromCloud(cloudStorageProcess, cloudStorageResources)
        _ <- fakeUserConfigException
      } yield UserCloudDevices(name, devices)
    }
  }

  private[this] def storeOnCloud(cloudStorageProcess: CloudStorageProcess, cloudStorageDevices: Seq[CloudStorageDevice]) = Service {
    val tasks = cloudStorageDevices map (d => cloudStorageProcess.createOrUpdateCloudStorageDevice(d).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CloudStorageProcessException](c.collect { case Answer(r) => r}))
  }

  private[this] def loadFromCloud(cloudStorageProcess: CloudStorageProcess, cloudStorageResources: Seq[CloudStorageDeviceSummary]) = Service {
    val tasks = cloudStorageResources map (r => cloudStorageProcess.getCloudStorageDevice(r.resourceId).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CloudStorageProcessException](c.collect { case Answer(r) => r}))
  }

  private[this] def fakeUserConfigException: ServiceDef2[Unit, UserConfigException] = Service(Task(Answer()))

  private[this] def getAuthToken(
    accountManager: AccountManager,
    account: Account,
    scopes: String): ServiceDef2[String, AuthTokenException with AuthTokenOperationCancelledException] = Service {
    Task {
      \/.fromTryCatchNonFatal{
        val result = accountManager.getAuthToken(account, scopes, javaNull, this, javaNull, javaNull).getResult
        result.getString(AccountManager.KEY_AUTHTOKEN)
      } match {
        case \/-(x) => Result.answer(x)
        case -\/(e: OperationCanceledException) => Errata(Seq((
          implicitly[ClassTag[AuthTokenOperationCancelledException]],
          (e.getMessage, AuthTokenOperationCancelledException(e.getMessage, Some(e))))))
        case -\/(e) => Errata(Seq((
          implicitly[ClassTag[AuthTokenException]],
          (e.getMessage, AuthTokenException(e.getMessage, Some(e))))))
      }
    }
  }

  def toCloudStorageDevice(userDevice: UserDevice) =
    CloudStorageDevice(
      deviceId = userDevice.deviceId,
      deviceName = userDevice.deviceName,
      documentVersion = CloudStorageProcess.actualDocumentVersion,
      userDevice.collections map toCloudStorageCollection)

  def toCloudStorageCollection(userCollection: UserCollection) =
    CloudStorageCollection(
      name = userCollection.name,
      originalSharedCollectionId = userCollection.originalSharedCollectionId,
      sharedCollectionId = userCollection.sharedCollectionId,
      sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
      items = userCollection.items map toCloudStorageCollectionItem,
      collectionType = userCollection.collectionType,
      icon = userCollection.icon,
      category = userCollection.category)

  def toCloudStorageCollectionItem(userCollectionItem: UserCollectionItem) =
    CloudStorageCollectionItem(
      itemType = userCollectionItem.itemType,
      title = userCollectionItem.title,
      intent = userCollectionItem.intent)

  def toCloudStorageDevice(deviceId: String, deviceName: String, collections: Seq[Collection]) =
    CloudStorageDevice(
      deviceId = deviceId,
      deviceName = deviceName,
      documentVersion = CloudStorageProcess.actualDocumentVersion,
      collections map toCloudStorageCollection)

  def toCloudStorageCollection(collection: Collection) =
    CloudStorageCollection(
      name = collection.name,
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = Some(collection.sharedCollectionSubscribed),
      items = collection.cards map toCloudStorageCollectionItem,
      collectionType = collection.collectionType,
      icon = collection.icon,
      category = collection.appsCategory)

  def toCloudStorageCollectionItem(card: Card) =
    CloudStorageCollectionItem(
      itemType = card.cardType.name,
      title = card.term,
      intent = Json.toJson(card.intent).toString())
}
