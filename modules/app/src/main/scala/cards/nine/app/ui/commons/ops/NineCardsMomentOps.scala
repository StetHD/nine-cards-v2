package cards.nine.app.ui.commons.ops

import cards.nine.models.types.NineCardsMoment
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

object NineCardsMomentOps {

  implicit class NineCardsMomentOp(nineCardsMoment: NineCardsMoment) {

    def getIconCollectionWorkspace(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${nineCardsMoment.getIconResource}") getOrElse R.drawable.icon_collection_default

    def getIconCollectionDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${nineCardsMoment.getIconResource}_detail") getOrElse R.drawable.icon_collection_default_detail

    def getName(implicit contextWrapper: ContextWrapper): String =
      resGetString(nineCardsMoment.getStringResource) getOrElse nineCardsMoment.name

    def getDescription(implicit contextWrapper: ContextWrapper): String =
      resGetString(s"${nineCardsMoment.getStringResource}Description") getOrElse nineCardsMoment.name

  }

}
