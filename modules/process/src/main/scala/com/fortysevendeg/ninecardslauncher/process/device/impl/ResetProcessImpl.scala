package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.process.device.{ImplicitsDeviceException, DeviceConversions, ResetException}
import com.fortysevendeg.ninecardslauncher.services.persistence.ImplicitsPersistenceServiceExceptions

trait ResetProcessImpl {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsPersistenceServiceExceptions =>

  def resetSavedItems() =
    (for {
      _ <- persistenceServices.deleteAllApps()
      _ <- persistenceServices.deleteAllCollections()
      _ <- persistenceServices.deleteAllCards()
      _ <- persistenceServices.deleteAllDockApps()
    } yield ()).resolve[ResetException]

}