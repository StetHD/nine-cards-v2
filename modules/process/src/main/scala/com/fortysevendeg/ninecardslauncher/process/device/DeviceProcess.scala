package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.device.models.AppItem

import scala.concurrent.Future
import scalaz.\/
import scalaz.concurrent.Task

trait DeviceProcess {
  def getApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]]
  def getCategorizedApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]]
  def getAppsByCategory(category: String)(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]]
  def categorizeApps(implicit context: ContextSupport):  Task[NineCardsException \/ Unit]
  def createBitmapsForNoPackagesInstalled(packages: Seq[String])(implicit context: ContextSupport):  Task[NineCardsException \/ Seq[String]]
}
