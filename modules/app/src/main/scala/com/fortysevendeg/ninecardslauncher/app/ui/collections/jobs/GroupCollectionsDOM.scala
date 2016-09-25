package com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs

import android.support.v4.app.{DialogFragment, FragmentActivity}
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.ui.collections.{CollectionAdapter, CollectionPresenter, CollectionsPagerAdapter, ScrollType}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.FabButtonTags._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ViewOps._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.TR
import com.fortysevendeg.ninecardslauncher2.TypedResource.TypedView
import macroid.{ActivityContextWrapper, Ui}

trait GroupCollectionsDOM {

  finder: TypedView =>

  lazy val toolbar = findView(TR.collections_toolbar)

  lazy val toolbarTitle = findView(TR.collections_toolbar_title)

  lazy val titleContent = findView(TR.collections_title_content)

  lazy val titleName = findView(TR.collections_title_name)

  lazy val titleIcon = findView(TR.collections_title_icon)

  lazy val selector = findView(TR.collections_selector)

  lazy val root = findView(TR.collections_root)

  lazy val viewPager = findView(TR.collections_view_pager)

  lazy val tabs = findView(TR.collections_tabs)

  lazy val iconContent = findView(TR.collections_icon_content)

  lazy val icon = findView(TR.collections_icon)

  lazy val fabButton = findView(TR.fab_button)

  lazy val fabMenuContent = findView(TR.fab_menu_content)

  lazy val fabMenu = findView(TR.fab_menu)

  lazy val fragmentContent = findView(TR.action_fragment_content)

  val tagDialog = "dialog"

  def isFabButtonVisible: Boolean = fabButton.getVisibility == View.VISIBLE

  def isAutoHide: Boolean = fabButton.getField[Boolean](autoHideKey) getOrElse false

  def isMenuOpened: Boolean = fabButton.getField[Boolean](opened) getOrElse false

  def getCurrentCollection: Option[Collection] = getAdapter flatMap { adapter =>
    adapter.getCurrentFragmentPosition flatMap adapter.collections.lift
  }

  def getCollection(position: Int): Option[Collection] = getAdapter flatMap (_.collections.lift(position))

  def getAdapter: Option[CollectionsPagerAdapter] = viewPager.getAdapter match {
    case adapter: CollectionsPagerAdapter => Some(adapter)
    case _ => None
  }

  def getScrollType: Option[ScrollType] = getAdapter map (_.statuses.scrollType)

  def getActivePresenter: Option[CollectionPresenter] = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
  } yield fragment.presenter

  def getActiveCollectionAdapter: Option[CollectionAdapter] = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
    collectionAdapter <- fragment.getAdapter
  } yield collectionAdapter

  def notifyItemChangedCollectionAdapter(position: Int): Unit =
    getActiveCollectionAdapter foreach(_.notifyItemChanged(position))

  def notifyDataSetChangedCollectionAdapter(): Unit =
    getActiveCollectionAdapter foreach(_.notifyDataSetChanged())

  def invalidateOptionMenu(implicit activityContextWrapper: ActivityContextWrapper): Unit = {
    activityContextWrapper.original.get match {
      case Some(activity: FragmentActivity) => activity.supportInvalidateOptionsMenu()
      case _ =>
    }
  }

  def showDialog(dialog: DialogFragment)(implicit activityContextWrapper: ActivityContextWrapper): Unit = {
    activityContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

}

trait GroupCollectionsUiListener {

  def closeEditingMode(): Unit
//    collectionsPagerPresenter.statuses.collectionMode match {
//      case EditingCollectionMode => collectionsPagerPresenter.closeEditingMode()
//      case _ =>
//    }

  def isNormalMode: Boolean

  def isEditingMode: Boolean
// collectionsPagerPresenter.statuses.collectionMode == EditingCollectionMode

}
