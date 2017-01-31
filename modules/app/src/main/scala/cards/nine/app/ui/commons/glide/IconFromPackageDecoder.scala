/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.commons.glide

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.util.Util
import macroid.extras.DeviceVersion.Marshmallow
import macroid.ContextWrapper

@SuppressLint(Array("NewApi"))
class IconFromPackageDecoder(packageName: String)(implicit contextWrapper: ContextWrapper)
    extends ResourceDecoder[Int, Bitmap] {

  override def getId: String = packageName

  override def decode(source: Int, width: Int, height: Int): Resource[Bitmap] = {

    val resources =
      contextWrapper.application.getPackageManager.getResourcesForApplication(packageName)
    val icon = Marshmallow ifSupportedThen {
      resources.getDrawable(source, null).asInstanceOf[BitmapDrawable].getBitmap
    } getOrElse {
      resources.getDrawable(source).asInstanceOf[BitmapDrawable].getBitmap
    }

    val pool = Glide.get(contextWrapper.bestAvailable).getBitmapPool

    new BitmapResource(icon, pool) {
      override def getSize: Int = Util.getBitmapByteSize(icon)

      override def recycle(): Unit = {}
    }

  }

}
