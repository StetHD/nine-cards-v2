package com.fortysevendeg.ninecardslauncher.app.commons

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, AddCollectionRequest, PrivateCard, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, Contact, ContactEmail => DeviceContactEmail, ContactInfo => DeviceContactInfo, ContactPhone => DeviceContactPhone}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}
import com.fortysevendeg.ninecardslauncher.process.types.{AppCardType, AppsCollectionType, NoInstalledAppCardType}
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollection, UserCollectionItem}

import scala.util.Random

trait Conversions
  extends NineCardIntentConversions {

  def toSeqUnformedApp(apps: Seq[App]): Seq[UnformedApp] = apps map toUnformedApp

  def toUnformedApp(app: App): UnformedApp = UnformedApp(
    name = app.name,
    packageName = app.packageName,
    className = app.className,
    imagePath = app.imagePath,
    category = app.category)

  def toSeqUnformedContact(contacts: Seq[Contact]): Seq[UnformedContact] = contacts map toUnformedContact

  def toUnformedContact(contact: Contact): UnformedContact = UnformedContact(
    name = contact.name,
    lookupKey = contact.lookupKey,
    photoUri = contact.photoUri,
    info = contact.info map toContactInfo)

  def toContactInfo(item: DeviceContactInfo): ContactInfo = ContactInfo(
    emails = item.emails map toContactEmail,
    phones = item.phones map toContactPhone)

  def toContactEmail(item: DeviceContactEmail): ContactEmail = ContactEmail(
    address = item.address,
    category = item.category.toString)

  def toContactPhone(item: DeviceContactPhone): ContactPhone = ContactPhone(
    number = item.number,
    category = item.category.toString)

  def toSeqFormedCollection(collections: Seq[UserCollection]): Seq[FormedCollection] = collections map toFormedCollection

  def toFormedCollection(userCollection: UserCollection): FormedCollection = FormedCollection(
    name = userCollection.name,
    originalSharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
    items = userCollection.items map toFormedItem,
    collectionType = userCollection.collectionType,
    constrains = userCollection.constrains,
    icon = userCollection.icon,
    category = userCollection.category)

  def toFormedItem(item: UserCollectionItem): FormedItem = FormedItem(
    itemType = item.itemType,
    title = item.title,
    intent = item.intent)

  def toAddCollectionRequest(privateCollection: PrivateCollection): AddCollectionRequest =
    AddCollectionRequest(
      name = privateCollection.name,
      collectionType = privateCollection.collectionType,
      icon = privateCollection.icon,
      themedColorIndex = privateCollection.themedColorIndex,
      appsCategory = privateCollection.appsCategory)

  def toAddCollectionRequest(privateCard: PrivateCard): AddCardRequest =
    AddCardRequest(
      term = privateCard.term,
      packageName = privateCard.packageName,
      cardType = privateCard.cardType,
      intent = privateCard.intent,
      imagePath = privateCard.imagePath)

  def toAddCollectionRequest(collection: SharedCollection): AddCollectionRequest =
    AddCollectionRequest(
      name = collection.name,
      collectionType = AppsCollectionType,
      icon = collection.icon,
      themedColorIndex = Random.nextInt(numSpaces),
      appsCategory = Option(collection.category),
      originalSharedCollectionId = Option(collection.sharedCollectionId))

  def toAddCollectionRequest(app: SharedCollectionPackage): AddCardRequest =
    AddCardRequest(
      term = app.title,
      packageName = Option(app.packageName),
      cardType = NoInstalledAppCardType,
      intent = toNineCardIntent(app),
      imagePath = "")

  def toAddCollectionRequest(app: App): AddCardRequest =
    AddCardRequest(
      term = app.name,
      packageName = Option(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = app.imagePath)

}
